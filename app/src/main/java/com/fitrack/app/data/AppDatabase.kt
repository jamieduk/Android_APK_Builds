package com.fitrack.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workouts")
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "food_logs")
data class FoodLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val foodName: String,
    val calories: Int,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,
    val date: Long = System.currentTimeMillis(),
    val imageUrl: String? = null
)

@Entity(tableName = "daily_goals")
data class DailyGoal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val calorieGoal: Int = 2000,
    val workoutGoalMinutes: Int = 30,
    val waterIntakeMl: Int = 0
)
