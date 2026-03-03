package com.fitrack.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.fitrack.app.api.MockNutritionService
import com.fitrack.app.databinding.ActivityMainBinding
import com.fitrack.app.data.AppDatabase
import com.fitrack.app.data.FoodLog
import com.fitrack.app.data.Workout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: AppDatabase
    
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
        loadTodayStats()
        checkPermissions()
    }
    
    private fun setupUI() {
        // Tab switching
        binding.tabFitness.setOnClickListener {
            binding.tabFitness.isSelected = true
            binding.tabNutrition.isSelected = false
            binding.sectionFitness.visibility = View.VISIBLE
            binding.sectionNutrition.visibility = View.GONE
        }
        
        binding.tabNutrition.setOnClickListener {
            binding.tabFitness.isSelected = false
            binding.tabNutrition.isSelected = true
            binding.sectionFitness.visibility = View.GONE
            binding.sectionNutrition.visibility = View.VISIBLE
        }
        
        // Add workout
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
        
        // Analyze food
        binding.btnAnalyzeFood.setOnClickListener {
            val foodDescription = binding.editFoodDescription.text.toString()
            if (foodDescription.isNotEmpty()) {
                val nutrition = MockNutritionService.estimateCalories(foodDescription)
                displayNutritionInfo(nutrition)
            } else {
                Toast.makeText(this, "Enter food name or take a photo", Toast.LENGTH_SHORT).show()
            }
        }
        
        // Log food
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
        
        // Take photo (placeholder - would need camera implementation)
        binding.btnTakePhoto.setOnClickListener {
            Toast.makeText(this, "Camera feature coming soon! For now, describe your food.", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun loadTodayStats() {
        lifecycleScope.launch {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val today = dateFormat.format(Date())
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
            
            binding.tvWorkoutsToday.text = "$workoutsToday"
            binding.tvCaloriesToday.text = "$totalCalories"
        }
    }
    
    private fun logWorkout(name: String, duration: Int, calories: Int) {
        lifecycleScope.launch {
            val workout = Workout(name = name, durationMinutes = duration, caloriesBurned = calories)
            database.workoutDao().insert(workout)
            loadTodayStats()
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
}
