package com.fitrack.app.utils

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Comprehensive Health & Fitness Calculators
 */
object HealthCalculators {

    // ==================== BMI Calculator ====================
    
    /**
     * Calculate Body Mass Index
     * @param weightKg Weight in kilograms
     * @param heightCm Height in centimeters
     * @return BMI value
     */
    fun calculateBMI(weightKg: Float, heightCm: Float): Float {
        val heightM = heightCm / 100f
        return (weightKg / (heightM.pow(2))).roundToInt().toFloat()
    }
    
    /**
     * Get BMI category and advice
     */
    fun getBMICategory(bmi: Float): BMICategory {
        return when {
            bmi < 18.5f -> BMICategory.UNDERWEIGHT
            bmi in 18.5f..24.9f -> BMICategory.NORMAL
            bmi in 25f..29.9f -> BMICategory.OVERWEIGHT
            bmi in 30f..34.9f -> BMICategory.OBESE_CLASS_1
            bmi in 35f..39.9f -> BMICategory.OBESE_CLASS_2
            else -> BMICategory.OBESE_CLASS_3
        }
    }
    
    fun getBMICategoryName(bmi: Float): String {
        return when (val category = getBMICategory(bmi)) {
            BMICategory.UNDERWEIGHT -> "Underweight"
            BMICategory.NORMAL -> "Normal weight"
            BMICategory.OVERWEIGHT -> "Overweight"
            BMICategory.OBESE_CLASS_1 -> "Obese (Class I)"
            BMICategory.OBESE_CLASS_2 -> "Obese (Class II)"
            BMICategory.OBESE_CLASS_3 -> "Obese (Class III)"
        }
    }
    
    fun getBMIAdvice(bmi: Float): String {
        return when (val category = getBMICategory(bmi)) {
            BMICategory.UNDERWEIGHT -> 
                "Consider gaining weight through a balanced diet with more protein and healthy fats. Strength training can help build muscle mass."
            BMICategory.NORMAL -> 
                "Great job! Maintain your current lifestyle with regular exercise and a balanced diet. Keep monitoring your weight."
            BMICategory.OVERWEIGHT -> 
                "Consider adopting a healthier diet and increasing physical activity. Aim for 150+ minutes of moderate exercise per week."
            BMICategory.OBESE_CLASS_1, 
            BMICategory.OBESE_CLASS_2, 
            BMICategory.OBESE_CLASS_3 -> 
                "Consult with a healthcare provider for a personalized weight loss plan. Start with small, sustainable changes to diet and activity."
        }
    }
    
    /**
     * Calculate ideal weight range based on height
     */
    fun calculateIdealWeightRange(heightCm: Float): Pair<Float, Float> {
        val heightM = heightCm / 100f
        val minWeight = 18.5f * (heightM.pow(2))
        val maxWeight = 24.9f * (heightM.pow(2))
        return Pair(minWeight.roundToInt().toFloat(), maxWeight.roundToInt().toFloat())
    }

    // ==================== BMR Calculator ====================
    
    /**
     * Calculate Basal Metabolic Rate using Mifflin-St Jeor Equation
     */
    fun calculateBMR(weightKg: Float, heightCm: Float, age: Int, gender: String): Int {
        val baseBMR = (10 * weightKg) + (6.25f * heightCm) - (5 * age)
        return when (gender.lowercase()) {
            "male" -> (baseBMR + 5).roundToInt()
            "female" -> (baseBMR - 161).roundToInt()
            else -> baseBMR.roundToInt() // Average for other/unspecified
        }
    }
    
    /**
     * Calculate TDEE (Total Daily Energy Expenditure)
     * @param activityLevel: sedentary, light, moderate, active, very_active
     */
    fun calculateTDEE(bmr: Int, activityLevel: String): Int {
        val multiplier = when (activityLevel.lowercase()) {
            "sedentary" -> 1.2f      // Little or no exercise
            "light" -> 1.375f        // Light exercise 1-3 days/week
            "moderate" -> 1.55f      // Moderate exercise 3-5 days/week
            "active" -> 1.725f       // Hard exercise 6-7 days/week
            "very_active" -> 1.9f    // Very hard exercise & physical job
            else -> 1.55f
        }
        return (bmr * multiplier).roundToInt()
    }
    
    /**
     * Get recommended daily calories based on goal
     */
    fun getRecommendedCalories(tdee: Int, goal: String): Int {
        return when (goal.lowercase()) {
            "lose" -> tdee - 500      // Deficit for weight loss (~0.5kg/week)
            "maintain" -> tdee        // Maintain current weight
            "gain" -> tdee + 500      // Surplus for muscle gain
            else -> tdee
        }
    }
    
    fun getCalorieGoalDescription(goal: String): String {
        return when (goal.lowercase()) {
            "lose" -> "Calorie deficit for weight loss (-500 cal/day)"
            "maintain" -> "Maintenance calories (stay the same)"
            "gain" -> "Calorie surplus for muscle gain (+500 cal/day)"
            else -> "Balanced nutrition"
        }
    }

    // ==================== Body Fat Percentage ====================
    
    /**
     * Estimate body fat percentage using BMI method (simplified)
     * Note: This is an estimation. DEXA scan is more accurate.
     */
    fun estimateBodyFat(bmi: Float, age: Int, gender: String): Float {
        val bmiFactor = bmi * 1.20f
        val ageFactor = age * 0.23f
        
        return when (gender.lowercase()) {
            "male" -> (bmiFactor + ageFactor - 16.2f).coerceAtLeast(2f)
            "female" -> (bmiFactor + ageFactor - 5.4f).coerceAtLeast(10f)
            else -> (bmiFactor + ageFactor - 10.8f).coerceAtLeast(5f)
        }.roundToInt().toFloat()
    }
    
    fun getBodyFatCategory(bodyFat: Float, gender: String): String {
        val isMale = gender.lowercase() == "male"
        return when {
            bodyFat < (if (isMale) 6f else 14f) -> "Essential fat"
            bodyFat < (if (isMale) 14f else 21f) -> "Athletic"
            bodyFat < (if (isMale) 18f else 25f) -> "Fitness"
            bodyFat < (if (isMale) 25f else 32f) -> "Average"
            else -> "Above average"
        }
    }

    // ==================== Waist-to-Hip Ratio ====================
    
    /**
     * Calculate Waist-to-Hip Ratio (WHR)
     */
    fun calculateWHR(waistCm: Float, hipsCm: Float): Float {
        return if (hipsCm > 0) (waistCm / hipsCm).roundToInt().toFloat() else 0f
    }
    
    fun getWHRRiskCategory(whr: Float, gender: String): String {
        val isMale = gender.lowercase() == "male"
        return when {
            whr < (if (isMale) 0.9f else 0.8f) -> "Low risk"
            whr < (if (isMale) 1.0f else 0.85f) -> "Moderate risk"
            else -> "High risk"
        }
    }
    
    fun getWHRHealthRisk(whr: Float, gender: String): String {
        val category = getWHRRiskCategory(whr, gender)
        return when (category) {
            "Low risk" -> "Your waist-to-hip ratio indicates low health risk. Keep maintaining a healthy lifestyle!"
            "Moderate risk" -> "Your ratio suggests moderate health risk. Consider improving diet and increasing physical activity."
            "High risk" -> "High waist-to-hip ratio indicates increased risk of cardiovascular disease. Consult a healthcare provider."
            else -> "Unknown"
        }
    }

    // ==================== Additional Calculators ====================
    
    /**
     * Calculate target heart rate zones
     */
    fun calculateTargetHeartRate(age: Int): HeartRateZones {
        val maxHR = 220 - age
        return HeartRateZones(
            warmUp = ((maxHR * 0.5f).roundToInt() to (maxHR * 0.6f).roundToInt()),
            fatBurn = ((maxHR * 0.6f).roundToInt() to (maxHR * 0.7f).roundToInt()),
            cardio = ((maxHR * 0.7f).roundToInt() to (maxHR * 0.8f).roundToInt()),
            peak = ((maxHR * 0.8f).roundToInt() to (maxHR * 0.9f).roundToInt())
        )
    }
    
    /**
     * Calculate water intake recommendation
     */
    fun calculateWaterIntake(weightKg: Float, activityMinutes: Int): Int {
        val baseIntake = weightKg * 35f // 35ml per kg
        val activityBoost = activityMinutes * 12f // Extra 12ml per minute of exercise
        return (baseIntake + activityBoost).roundToInt() / 1000 * 1000 // Round to nearest liter
    }
    
    /**
     * Calculate weekly weight change rate
     */
    fun calculateWeightChangeRate(
        currentWeight: Float,
        targetWeight: Float,
        weeks: Int
    ): Float {
        return ((targetWeight - currentWeight) / weeks).roundToInt().toFloat()
    }
    
    fun getWeightChangeAdvice(rate: Float): String {
        return when {
            rate < -1f -> "Your target weight loss is aggressive (>1kg/week). Consider a more sustainable pace to preserve muscle mass."
            rate in -1f..-0.5f -> "Healthy weight loss rate! You should lose 0.5-1kg per week sustainably."
            rate in -0.5f..0.5f -> "Maintenance mode. Focus on body recomposition through strength training."
            rate in 0.5f..1f -> "Healthy weight gain rate for muscle building with proper training."
            rate > 1f -> "Your target weight gain is aggressive. Ensure you're doing resistance training to build muscle, not just fat."
            else -> "Set a realistic goal and track your progress monthly."
        }
    }
}

// ==================== Data Classes ====================

enum class BMICategory {
    UNDERWEIGHT,
    NORMAL,
    OVERWEIGHT,
    OBESE_CLASS_1,
    OBESE_CLASS_2,
    OBESE_CLASS_3
}

data class HeartRateZones(
    val warmUp: Pair<Int, Int>,
    val fatBurn: Pair<Int, Int>,
    val cardio: Pair<Int, Int>,
    val peak: Pair<Int, Int>
) {
    fun formatZones(): String {
        return """
🟢 Warm Up: ${warmUp.first}-${warmUp.second} bpm
🟡 Fat Burn: ${fatBurn.first}-${fatBurn.second} bpm
🟠 Cardio: ${cardio.first}-${cardio.second} bpm
🔴 Peak: ${peak.first}-${peak.second} bpm
        """.trimIndent()
    }
}

data class HealthReport(
    val bmi: Float,
    val bmiCategory: String,
    val bmiAdvice: String,
    val bmr: Int,
    val tdee: Int,
    val recommendedCalories: Int,
    val bodyFatPercent: Float,
    val bodyFatCategory: String,
    val targetHeartRate: String,
    val dailyWaterIntakeMl: Int,
    val idealWeightRange: Pair<Float, Float>,
    val weightChangeAdvice: String
)
