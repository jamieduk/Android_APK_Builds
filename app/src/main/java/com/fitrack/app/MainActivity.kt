package com.fitrack.app

import android.app.DatePickerDialog
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
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    private var currentUserProfile: UserProfile? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            
            database = AppDatabase.getDatabase(this)
            
            setupTabListeners()
            setupFitnessTab()
            setupProfileTab()
            setupCalculatorsTab()
            setupNutritionTab()
            loadTodayStats()
            
            // Show fitness tab by default
            showSection(binding.sectionFitness)
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
    
    private fun setupTabListeners() {
        binding.tabFitness.setOnClickListener { showSection(binding.sectionFitness) }
        binding.tabProfile.setOnClickListener { showSection(binding.sectionProfile) }
        binding.tabCalculators.setOnClickListener { showSection(binding.sectionCalculators) }
        binding.tabNutrition.setOnClickListener { showSection(binding.sectionNutrition) }
    }
    
    private fun showSection(section: View) {
        binding.sectionFitness.visibility = View.GONE
        binding.sectionProfile.visibility = View.GONE
        binding.sectionCalculators.visibility = View.GONE
        binding.sectionNutrition.visibility = View.GONE
        section.visibility = View.VISIBLE
        
        if (section == binding.sectionProfile) {
            loadUserProfile()
        }
    }
    
    private fun setupFitnessTab() {
        binding.btnLogWorkout.setOnClickListener {
            val name = binding.editWorkoutName.text.toString()
            val duration = binding.editDuration.text.toString()
            val calories = binding.editWorkoutCalories.text.toString()
            
            if (name.isNotEmpty() && duration.isNotEmpty() && calories.isNotEmpty()) {
                lifecycleScope.launch {
                    database.workoutDao().insert(Workout(name = name, durationMinutes = duration.toInt(), caloriesBurned = calories.toInt()))
                    loadTodayStats()
                }
                Toast.makeText(this, "Workout logged! 💪", Toast.LENGTH_SHORT).show()
                binding.editWorkoutName.text?.clear()
                binding.editDuration.text?.clear()
                binding.editWorkoutCalories.text?.clear()
            } else {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
        
        binding.btnLogWeight.setOnClickListener {
            val weight = binding.editCurrentWeight.text.toString()
            if (weight.isNotEmpty()) {
                lifecycleScope.launch {
                    database.weightLogDao().insert(WeightLog(weightKg = weight.toFloat()))
                    loadTodayStats()
                }
                Toast.makeText(this, "Weight logged! 📊", Toast.LENGTH_SHORT).show()
                binding.editCurrentWeight.text?.clear()
            }
        }
    }
    
    private fun setupProfileTab() {
        binding.btnSaveProfile.setOnClickListener {
            saveUserProfile()
        }
        
        binding.editDateOfBirth.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                binding.editDateOfBirth.setText("$year-${month + 1}-$day")
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
    }
    
    private fun setupCalculatorsTab() {
        binding.btnCalculateBMI.setOnClickListener {
            val height = binding.editCalcHeight.text.toString()
            val weight = binding.editCalcWeight.text.toString()
            
            if (height.isNotEmpty() && weight.isNotEmpty()) {
                val bmi = HealthCalculators.calculateBMI(weight.toFloat(), height.toFloat())
                val category = HealthCalculators.getBMICategoryName(bmi)
                val advice = HealthCalculators.getBMIAdvice(bmi)
                val ideal = HealthCalculators.calculateIdealWeightRange(height.toFloat())
                
                binding.tvBmiResult.text = "BMI: $bmi\nCategory: $category\nIdeal: ${ideal.first}-${ideal.second} kg\n\n$advice"
            }
        }
        
        binding.btnCalculateDailyNeeds.setOnClickListener {
            val weight = binding.editCalcWeight.text.toString().toFloatOrNull() ?: 70f
            val height = binding.editCalcHeight.text.toString().toFloatOrNull() ?: 170f
            val age = binding.editCalcAge.text.toString().toIntOrNull() ?: 30
            val gender = binding.spinnerCalcGender.selectedItem.toString().lowercase()
            val activity = binding.spinnerCalcActivity.selectedItem.toString().lowercase()
            val goal = binding.spinnerCalcGoal.selectedItem.toString().lowercase()
            
            val bmr = HealthCalculators.calculateBMR(weight, height, age, gender)
            val tdee = HealthCalculators.calculateTDEE(bmr, activity)
            val calories = HealthCalculators.getRecommendedCalories(tdee, goal)
            val water = HealthCalculators.calculateWaterIntake(weight, 30)
            
            binding.tvDailyNeedsResult.text = "BMR: $bmr cal\nTDEE: $tdee cal\nDaily: $calories cal\nWater: ${water/1000}L\n\nMacros:\nProtein: ${calories * 0.3f / 4}g\nCarbs: ${calories * 0.4f / 4}g\nFat: ${calories * 0.3f / 9}g"
        }
    }
    
    private fun setupNutritionTab() {
        binding.btnAnalyzeFood.setOnClickListener {
            val food = binding.editFoodDescription.text.toString()
            if (food.isNotEmpty()) {
                val nutrition = MockNutritionService.estimateCalories(food)
                binding.tvNutritionResult.text = "Calories: ${nutrition.calories} kcal\nProtein: ${nutrition.protein}g\nCarbs: ${nutrition.carbs}g\nFat: ${nutrition.fat}g"
            }
        }
        
        binding.btnLogFood.setOnClickListener {
            val food = binding.editFoodDescription.text.toString()
            if (food.isNotEmpty()) {
                val nutrition = MockNutritionService.estimateCalories(food)
                lifecycleScope.launch {
                    database.foodLogDao().insert(FoodLog(foodName = food, calories = nutrition.calories, protein = nutrition.protein, carbs = nutrition.carbs, fat = nutrition.fat))
                    loadTodayStats()
                }
                Toast.makeText(this, "Food logged! 🍎", Toast.LENGTH_SHORT).show()
                binding.editFoodDescription.text?.clear()
                binding.tvNutritionResult.text = ""
            }
        }
    }
    
    private fun loadUserProfile() {
        lifecycleScope.launch {
            currentUserProfile = database.userProfileDao().getProfileOnce()
            currentUserProfile?.let { profile ->
                binding.editName.setText(profile.name)
                binding.editDateOfBirth.setText(profile.dateOfBirth)
                binding.spinnerGender.setSelection(getIndex(profile.gender, arrayOf("male", "female", "other")))
                binding.editHeight.setText(profile.heightCm.toInt().toString())
                binding.editWeight.setText(profile.weightKg.toInt().toString())
                binding.spinnerActivity.setSelection(getIndex(profile.activityLevel, arrayOf("sedentary", "light", "moderate", "active", "very_active")))
                binding.spinnerGoal.setSelection(getIndex(profile.fitnessGoal, arrayOf("lose", "maintain", "gain")))
                binding.editTargetWeight.setText(profile.targetWeightKg.toInt().toString())
                
                val bmi = HealthCalculators.calculateBMI(profile.weightKg, profile.heightCm)
                binding.tvBmiDisplay.text = "Your BMI: $bmi"
                binding.tvBmiCategory.text = HealthCalculators.getBMIAdvice(bmi)
            }
        }
    }
    
    private fun saveUserProfile() {
        val gender = binding.spinnerGender.selectedItem.toString().lowercase()
        val height = binding.editHeight.text.toString().toFloatOrNull() ?: 170f
        val weight = binding.editWeight.text.toString().toFloatOrNull() ?: 70f
        val activity = binding.spinnerActivity.selectedItem.toString().lowercase()
        val goal = binding.spinnerGoal.selectedItem.toString().lowercase()
        val target = binding.editTargetWeight.text.toString().toFloatOrNull() ?: weight
        
        val profile = UserProfile(
            name = binding.editName.text.toString().ifEmpty { "User" },
            dateOfBirth = binding.editDateOfBirth.text.toString(),
            gender = gender,
            heightCm = height,
            weightKg = weight,
            activityLevel = activity,
            fitnessGoal = goal,
            targetWeightKg = target
        )
        
        lifecycleScope.launch {
            database.userProfileDao().saveProfile(profile)
            currentUserProfile = profile
            Toast.makeText(this@MainActivity, "Profile saved! ✅", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun loadTodayStats() {
        lifecycleScope.launch {
            val calendar = Calendar.getInstance().apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0) }
            val start = calendar.timeInMillis
            
            val workouts = database.workoutDao().getWorkoutsByDateRange(start, System.currentTimeMillis()).first().size
            val calories = database.foodLogDao().getTotalCalories(start, System.currentTimeMillis()).first() ?: 0
            val latestWeight = database.weightLogDao().getLatestWeight()
            
            binding.tvWorkoutsToday.text = "$workouts"
            binding.tvCaloriesToday.text = "$calories"
            binding.tvLatestWeight.text = latestWeight?.let { "${it.weightKg} kg" } ?: "--"
        }
    }
    
    private fun getIndex(value: String, array: Array<String>): Int {
        return array.indexOf(value.lowercase()).takeIf { it >= 0 } ?: 0
    }
}
