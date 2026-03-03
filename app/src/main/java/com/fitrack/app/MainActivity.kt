package com.fitrack.app

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
    
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        database = AppDatabase.getDatabase(this)
        
        setupUI()
        loadUserProfile()
        loadTodayStats()
        checkPermissions()
    }
    
    private fun setupUI() {
        // Tab navigation
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
    }
    
    private fun switchTab(tab: String) {
        // Reset all tabs
        binding.tabFitness.isSelected = false
        binding.tabProfile.isSelected = false
        binding.tabCalculators.isSelected = false
        binding.tabNutrition.isSelected = false
        
        // Hide all sections
        binding.sectionFitness.visibility = View.GONE
        binding.sectionProfile.visibility = View.GONE
        binding.sectionCalculators.visibility = View.GONE
        binding.sectionNutrition.visibility = View.GONE
        
        // Activate selected tab
        when (tab) {
            "fitness" -> {
                binding.tabFitness.isSelected = true
                binding.sectionFitness.visibility = View.VISIBLE
            }
            "profile" -> {
                binding.tabProfile.isSelected = true
                binding.sectionProfile.visibility = View.VISIBLE
                loadUserProfile()
            }
            "calculators" -> {
                binding.tabCalculators.isSelected = true
                binding.sectionCalculators.visibility = View.VISIBLE
            }
            "nutrition" -> {
                binding.tabNutrition.isSelected = true
                binding.sectionNutrition.visibility = View.VISIBLE
            }
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
                Toast.makeText(this, "Please fill all workout fields", Toast.LENGTH_SHORT).show()
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
        
        binding.btnCalculateBodyFat.setOnClickListener {
            calculateBodyFat()
        }
        
        binding.btnCalculateHeartRate.setOnClickListener {
            calculateHeartRate()
        }
    }
    
    private fun setupNutritionSection() {
        binding.btnAnalyzeFood.setOnClickListener {
            val foodDescription = binding.editFoodDescription.text.toString()
            if (foodDescription.isNotEmpty()) {
                val nutrition = MockNutritionService.estimateCalories(foodDescription)
                displayNutritionInfo(nutrition)
            } else {
                Toast.makeText(this, "Enter food name or description", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnLogFood.setOnClickListener {
            val foodName = binding.editFoodDescription.text.toString()
            val nutrition = MockNutritionService.estimateCalories(foodName)
            
            if (foodName.isNotEmpty()) {
                logFood(foodName, nutrition.calories, nutrition.protein, nutrition.carbs, nutrition.fat)
                binding.editFoodDescription.text?.clear()
                binding.tvNutritionResult.text = ""
                Toast.makeText(this, "Food logged! 🍎", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnTakePhoto.setOnClickListener {
            Toast.makeText(this, "Camera feature coming soon! For now, describe your food.", Toast.LENGTH_LONG).show()
        }
    }
    
    // ==================== User Profile Functions ====================
    
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
                
                // Calculate and display BMI if we have data
                if (profile.heightCm > 0 && profile.weightKg > 0) {
                    val bmi = HealthCalculators.calculateBMI(profile.weightKg, profile.heightCm)
                    val bmiCategory = HealthCalculators.getBMICategoryName(bmi)
                    binding.tvBmiDisplay.text = "Your BMI: $bmi ($bmiCategory)"
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
            Toast.makeText(this, "Please enter height and weight", Toast.LENGTH_SHORT).show()
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
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val dateStr = "$selectedYear-${selectedMonth + 1}-$selectedDay"
            binding.editDateOfBirth.setText(dateStr)
        }, year, month, day).show()
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

💡 ${if (bmi < 18.5f) "You're underweight." else if (bmi > 24.9f) "You're above normal weight." else "You're in the healthy range!"}

🎯 Ideal weight range for your height: ${idealRange.first} - ${idealRange.second} kg

📝 Advice: $advice
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
        val recommendedCalories = HealthCalculators.getRecommendedCalories(tdee, goal)
        val waterIntake = HealthCalculators.calculateWaterIntake(weight, 30)
        
        binding.tvDailyNeedsResult.text = """
🔥 Basal Metabolic Rate (BMR): $bmr cal/day
⚡ Total Daily Energy Expenditure (TDEE): $tdee cal/day

🎯 Recommended Daily Calories: $recommendedCalories cal
   (${HealthCalculators.getCalorieGoalDescription(goal)})

💧 Daily Water Intake: ${waterIntake / 1000}L (${waterIntake}ml)

📊 Macros Recommendation:
   • Protein: ${recommendedCalories * 0.3f / 4}g (30%)
   • Carbs: ${recommendedCalories * 0.4f / 4}g (40%)
   • Fats: ${recommendedCalories * 0.3f / 9}g (30%)
        """.trimIndent()
    }
    
    private fun calculateBodyFat() {
        val bmiStr = binding.editCalcBmi.text.toString()
        val ageStr = binding.editCalcAge.text.toString()
        val gender = binding.spinnerCalcGender.selectedItem.toString().lowercase()
        
        if (bmiStr.isEmpty() || ageStr.isEmpty()) {
            Toast.makeText(this, "Enter BMI and age", Toast.LENGTH_SHORT).show()
            return
        }
        
        val bmi = bmiStr.toFloat()
        val age = ageStr.toInt()
        val bodyFat = HealthCalculators.estimateBodyFat(bmi, age, gender)
        val category = HealthCalculators.getBodyFatCategory(bodyFat, gender)
        
        binding.tvBodyFatResult.text = """
📊 Estimated Body Fat: $bodyFat%
📌 Category: $category

ℹ️ Note: This is an estimation using BMI method. 
For accurate measurement, consider DEXA scan or calipers.

🎯 Healthy body fat ranges:
   Men: 6-24% (Athletic: 6-13%)
   Women: 14-31% (Athletic: 14-20%)
        """.trimIndent()
    }
    
    private fun calculateHeartRate() {
        val ageStr = binding.editCalcAge.text.toString()
        
        if (ageStr.isEmpty()) {
            Toast.makeText(this, "Enter your age", Toast.LENGTH_SHORT).show()
            return
        }
        
        val age = ageStr.toInt()
        val zones = HealthCalculators.calculateTargetHeartRate(age)
        val maxHR = 220 - age
        
        binding.tvHeartRateResult.text = """
❤️ Maximum Heart Rate: $maxHR bpm

${zones.formatZones()}

💡 Training Tips:
   • Warm Up: Light activity to prepare muscles
   • Fat Burn: Moderate intensity, sustainable effort
   • Cardio: Improves cardiovascular fitness
   • Peak: High intensity intervals (short bursts)
        """.trimIndent()
    }
    
    // ==================== Data Logging Functions ====================
    
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
            
            // Update current weight in profile
            currentUserProfile?.let { profile ->
                val updatedProfile = profile.copy(
                    weightKg = weight,
                    updatedAt = System.currentTimeMillis()
                )
                database.userProfileDao().saveProfile(updatedProfile)
                currentUserProfile = updatedProfile
            }
            
            calculateBMI() // Refresh BMI calculation
            Toast.makeText(this@MainActivity, "Weight logged! Check your BMI.", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun logFood(name: String, calories: Int, protein: Float, carbs: Float, fat: Float) {
        lifecycleScope.launch {
            val foodLog = FoodLog(
                foodName = name,
                calories = calories,
                protein = protein,
                carbs = carbs,
                fat = fat
            )
            database.foodLogDao().insert(foodLog)
            loadTodayStats()
        }
    }
    
    private fun displayNutritionInfo(nutrition: MockNutritionService.NutritionData) {
        binding.tvNutritionResult.text = """
🔥 Calories: ${nutrition.calories} kcal
💪 Protein: ${nutrition.protein}g
🍞 Carbs: ${nutrition.carbs}g
🥑 Fat: ${nutrition.fat}g
        """.trimIndent()
    }
    
    private fun loadTodayStats() {
        lifecycleScope.launch {
            val calendar = Calendar.getInstance()
            val startOfDay = calendar.apply { 
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }.timeInMillis
            
            val workoutsToday = database.workoutDao()
                .getWorkoutsByDateRange(startOfDay, System.currentTimeMillis())
                .first()
                .size
            
            val totalCalories = database.foodLogDao()
                .getTotalCalories(startOfDay, System.currentTimeMillis())
                .first() ?: 0
            
            // Get latest weight
            val latestWeight = database.weightLogDao().getLatestWeight()
            
            binding.tvWorkoutsToday.text = "$workoutsToday"
            binding.tvCaloriesToday.text = "$totalCalories"
            binding.tvLatestWeight.text = latestWeight?.let { "${it.weightKg} kg" } ?: "Not logged"
        }
    }
    
    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                // Permission already granted
            }
            else -> {
                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
    
    // ==================== Helper Functions ====================
    
    private fun getGenderIndex(gender: String): Int {
        return when (gender.lowercase()) {
            "male" -> 0
            "female" -> 1
            else -> 2
        }
    }
    
    private fun getActivityIndex(activity: String): Int {
        return when (activity.lowercase()) {
            "sedentary" -> 0
            "light" -> 1
            "moderate" -> 2
            "active" -> 3
            "very_active" -> 4
            else -> 2
        }
    }
    
    private fun getGoalIndex(goal: String): Int {
        return when (goal.lowercase()) {
            "lose" -> 0
            "maintain" -> 1
            "gain" -> 2
            else -> 1
        }
    }
}
