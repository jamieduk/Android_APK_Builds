package com.fitrack.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1, // Single user profile
    val name: String = "",
    val dateOfBirth: String = "",
    val gender: String = "other", // male, female, other
    val heightCm: Float = 170f,
    val weightKg: Float = 70f,
    val activityLevel: String = "moderate", // sedentary, light, moderate, active, very_active
    val fitnessGoal: String = "maintain", // lose, maintain, gain
    val targetWeightKg: Float = 70f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "weight_logs")
data class WeightLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Float,
    val date: Long = System.currentTimeMillis(),
    val notes: String = ""
)

@Entity(tableName = "body_measurements")
data class BodyMeasurement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long = System.currentTimeMillis(),
    val weightKg: Float,
    val bodyFatPercent: Float? = null,
    val chestCm: Float? = null,
    val waistCm: Float? = null,
    val hipsCm: Float? = null,
    val armCm: Float? = null,
    val thighCm: Float? = null,
    val notes: String = ""
)
