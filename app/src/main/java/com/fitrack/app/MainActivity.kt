package com.fitrack.app

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fitrack.app.api.MockNutritionService
import com.fitrack.app.data.*
import com.fitrack.app.databinding.ActivityMainBinding
import com.fitrack.app.utils.HealthCalculators
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private var currentUserProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AppDatabase.getDatabase(this)
        
        setupUI()
        loadUserProfile()
        loadTodayStats()
    }
    
    private fun setupUI() {
        // Tab navigation - toggle section visibility
        binding.tabFitness.setOnClickListener { switchTab("fitness") }
        binding.tabProfile.setOnClickListener { switchTab("profile") }
        binding.tabCalculators.setOnClickListener { switchTab("calculators") }
        binding.tabNutrition.setOnClickListener { switchTab("nutrition") }
        
        // Fitness section
        setupFitnessSection()
        
        // Profile section
        setupProfileSection()
        
        // Calculators section
        setupCalculatorsSection()
        
        // Nutrition section
        setupNutritionSection()
        
        // Initialize: Show fitness, hide others
        showSection("fitness")
    }
    
    private fun switchTab(tab: String) {
        showSection(tab)
    }
    
    private fun showSection(section: String) {
        // Hide all sections
        binding.sectionFitness.visibility = View.GONE
        binding.sectionProfile.visibility = View.GONE
        binding.sectionCalculators.visibility = View.GONE
        binding.sectionNutrition.visibility = View.GONE
        
        // Show selected section
        when (section) {
            "fitness" -> binding.sectionFitness.visibility = View.VISIBLE
            "profile" -> {
                binding.sectionProfile.visibility = View.VISIBLE
                loadUserProfile()
            }
            "calculators" -> binding.sectionCalculators.visibility = View.VISIBLE
            "nutrition" -> binding.sectionNutrition.visibility = View.VISIBLE
        }
    }
    
    private fun setupFitnessSection() {
        binding.btnLogWorkout.setOnClickListener {
            val workoutName = binding.editWorkoutName.text.toString()
            val durationStr = binding.editDuration.text.toString()
            val caloriesStr = binding.editWorkoutCalories.text.toString()
            
            if (workoutName.isNotEmpty() && durationStr.isNotEmpty() && caloriesStr.isNotEmpty()) {
                logWorkout(workoutName, durationStr.toInt(), caloriesStr.toInt())
                binding.editWorkoutName.text?.clear()
                binding.editDuration.text?.clear()
                binding.editWorkoutCalories.text?.clear()
                Toast.makeText(this, "Workout logged! 💪", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnLogWeight.setOnClickListener {
            val weightStr = binding.editCurrentWeight.text.toString()
            if (weightStr.isNotEmpty()) {
                logWeight(weightStr.toFloat())
                binding.editCurrentWeight.text?.clear()
                Toast.makeText(this, "Weight logged! 📊", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupProfileSection() {
        binding.btnSaveProfile.setOnClickListener {
            saveUserProfile()
        }
        
        binding.editDateOfBirth.setOnClickListener {
            showDatePicker()
        }
    }
    
    private fun setupCalculatorsSection() {
        binding.btnCalculateBMI.setOnClickListener {
            calculateBMI()
        }
        
        binding.btnCalculateDailyNeeds.setOnClickListener {
            calculateDailyNeeds()
        }
    }
    
    private fun setupNutritionSection() {
        binding.btnAnalyzeFood.setOnClickListener {
            val foodDescription = binding.editFoodDescription.text.toString()
            if (foodDescription.isNotEmpty()) {
                val nutrition = MockNutritionService.estimateCalories(foodDescription)
                binding.tvNutritionResult.text = """
🔥 Calories: ${nutrition.calories} kcal
💪 Protein: ${nutrition.protein}g
🍞 Carbs: ${nutrition.carbs}g
🥑 Fat: ${nutrition.fat}g
                """.trimIndent()
            } else {
                Toast.makeText(this, "Enter food name", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnLogFood.setOnClickListener {
            val foodName = binding.editFoodDescription.text.toString()
            if (foodName.isNotEmpty()) {
                val nutrition = MockNutritionService.estimateCalories(foodName)
                logFood(foodName, nutrition.calories, nutrition.protein, nutrition.carbs, nutrition.fat)
                binding.editFoodDescription.text?.clear()
                binding.tvNutritionResult.text = ""
                Toast.makeText(this, "Food logged! 🍎", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // ==================== Profile Functions ====================
    
    private fun loadUserProfile() {
        lifecycleScope.launch {
            currentUserProfile = database.userProfileDao().getProfileOnce()
            currentUserProfile?.let { profile ->
                binding.editName.setText(profile.name)
                binding.editDateOfBirth.setText(profile.dateOfBirth)
                binding.spinnerGender.setSelection(getGenderIndex(profile.gender))
                binding.editHeight.setText(profile.heightCm.toInt().toString())
                binding.editWeight.setText(profile.weightKg.toInt().toString())
                binding.spinnerActivity.setSelection(getActivityIndex(profile.activityLevel))
                binding.spinnerGoal.setSelection(getGoalIndex(profile.fitnessGoal))
                binding.editTargetWeight.setText(profile.targetWeightKg.toInt().toString())
                
                // Update BMI display
                if (profile.heightCm > 0 && profile.weightKg > 0) {
                    val bmi = HealthCalculators.calculateBMI(profile.weightKg, profile.heightCm)
                    val category = HealthCalculators.getBMICategoryName(bmi)
                    binding.tvBmiDisplay.text = "Your BMI: $bmi ($category)"
                    binding.tvBmiCategory.text = HealthCalculators.getBMIAdvice(bmi)
                }
            }
        }
    }
    
    private fun saveUserProfile() {
        val name = binding.editName.text.toString()
        val dob = binding.editDateOfBirth.text.toString()
        val gender = binding.spinnerGender.selectedItem.toString().lowercase()
        val heightStr = binding.editHeight.text.toString()
        val weightStr = binding.editWeight.text.toString()
        val activityLevel = binding.spinnerActivity.selectedItem.toString().lowercase()
        val fitnessGoal = binding.spinnerGoal.selectedItem.toString().lowercase()
        val targetWeightStr = binding.editTargetWeight.text.toString()
        
        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Enter height and weight", Toast.LENGTH_SHORT).show()
            return
        }
        
        val profile = UserProfile(
            name = name.ifEmpty { "User" },
            dateOfBirth = dob,
            gender = gender,
            heightCm = heightStr.toFloat(),
            weightKg = weightStr.toFloat(),
            activityLevel = activityLevel,
            fitnessGoal = fitnessGoal,
            targetWeightKg = targetWeightStr.toFloatOrNull() ?: weightStr.toFloat(),
            updatedAt = System.currentTimeMillis()
        )
        
        lifecycleScope.launch {
            database.userProfileDao().saveProfile(profile)
            currentUserProfile = profile
            Toast.makeText(this@MainActivity, "Profile saved! ✅", Toast.LENGTH_SHORT).show()
            loadUserProfile()
        }
    }
    
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            binding.editDateOfBirth.setText("$year-${month + 1}-$day")
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }
    
    // ==================== Calculator Functions ====================
    
    private fun calculateBMI() {
        val heightStr = binding.editCalcHeight.text.toString()
        val weightStr = binding.editCalcWeight.text.toString()
        
        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Enter height and weight", Toast.LENGTH_SHORT).show()
            return
        }
        
        val height = heightStr.toFloat()
        val weight = weightStr.toFloat()
        val bmi = HealthCalculators.calculateBMI(weight, height)
        val category = HealthCalculators.getBMICategoryName(bmi)
        val advice = HealthCalculators.getBMIAdvice(bmi)
        val idealRange = HealthCalculators.calculateIdealWeightRange(height)
        
        binding.tvBmiResult.text = """
📊 Your BMI: $bmi
📌 Category: $category
🎯 Ideal weight: ${idealRange.first}-${idealRange.second} kg

💡 $advice
        """.trimIndent()
    }
    
    private fun calculateDailyNeeds() {
        val weightStr = binding.editCalcWeight.text.toString()
        val heightStr = binding.editCalcHeight.text.toString()
        val ageStr = binding.editCalcAge.text.toString()
        val gender = binding.spinnerCalcGender.selectedItem.toString().lowercase()
        val activity = binding.spinnerCalcActivity.selectedItem.toString().lowercase()
        val goal = binding.spinnerCalcGoal.selectedItem.toString().lowercase()
        
        if (weightStr.isEmpty() || heightStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }
        
        val weight = weightStr.toFloat()
        val height = heightStr.toFloat()
        val age = ageStr.toInt()
        
        val bmr = HealthCalculators.calculateBMR(weight, height, age, gender)
        val tdee = HealthCalculators.calculateTDEE(bmr, activity)
        val calories = HealthCalculators.getRecommendedCalories(tdee, goal)
        val water = HealthCalculators.calculateWaterIntake(weight, 30)
        
        binding.tvDailyNeedsResult.text = """
🔥 BMR: $bmr cal/day
⚡ TDEE: $tdee cal/day
🎯 Daily Calories: $calories
💧 Water: ${water / 1000}L/day

📊 Macros:
   Protein: ${calories * 0.3f / 4}g
   Carbs: ${calories * 0.4f / 4}g
   Fat: ${calories * 0.3f / 9}g
        """.trimIndent()
    }
    
    // ==================== Data Functions ====================
    
    private fun logWorkout(name: String, duration: Int, calories: Int) {
        lifecycleScope.launch {
            val workout = Workout(name = name, durationMinutes = duration, caloriesBurned = calories)
            database.workoutDao().insert(workout)
            loadTodayStats()
        }
    }
    
    private fun logWeight(weight: Float) {
        lifecycleScope.launch {
            val weightLog = WeightLog(weightKg = weight)
            database.weightLogDao().insert(weightLog)
            
            // Update profile weight
            currentUserProfile?.let { profile ->
                val updated = profile.copy(weightKg = weight, updatedAt = System.currentTimeMillis())
                database.userProfileDao().saveProfile(updated)
                currentUserProfile = updated
            }
        }
    }
    
    private fun logFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        lifecycleScope.launch {
            val foodLog = FoodLog(foodName = name, calories = calories, protein = protein, carbs = carbs, fat = fat)
            database.foodLogDao().insert(foodLog)
            loadTodayStats()
        }
    }
    
    private fun loadTodayStats() {
        lifecycleScope.launch {
            val calendar = Calendar.getInstance()
            val startOfDay = calendar.apply { 
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            
            val workoutsCount = database.workoutDao()
                .getWorkoutsByDateRange(startOfDay, System.currentTimeMillis())
                .first()
                .size
            
            val totalCalories = database.foodLogDao()
                .getTotalCalories(startOfDay, System.currentTimeMillis())
                .first() ?: 0
            
            val latestWeight = database.weightLogDao().getLatestWeight()
            
            binding.tvWorkoutsToday.text = "$workoutsCount"
            binding.tvCaloriesToday.text = "$totalCalories"
            binding.tvLatestWeight.text = latestWeight?.let { "${it.weightKg} kg" } ?: "--"
        }
    }
    
    // ==================== Helpers ====================
    
    private fun getGenderIndex(gender: String): Int = when (gender.lowercase()) {
        "male" -> 0
        "female" -> 1
        else -> 2
    }
    
    private fun getActivityIndex(activity: String): Int = when (activity.lowercase()) {
        "sedentary" -> 0
        "light" -> 1
        "moderate" -> 2
        "active" -> 3
        "very_active" -> 4
        else -> 2
    }
    
    private fun getGoalIndex(goal: String): Int = when (goal.lowercase()) {
        "lose" -> 0
        "maintain" -> 1
        "gain" -> 2
        else -> 1
    }
}
