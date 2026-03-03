package com.fitrack.app.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// Nutrition API interface (using a free tier service)
// You can get a free API key at https://edamam.com/ or https://nutrifition.io/
interface NutritionApi {
    @GET("v1/nutrition-data")
    suspend fun getNutritionInfo(
        @Query("ingr") foodDescription: String,
        @Query("app_id") appId: String,
        @Query("app_key") appKey: String
    ): Response<NutritionResponse>
    
    // Alternative: Use a mock response for demo if no API key
    @GET("search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("fields") fields: String = "foodId,label,nutrients"
    ): Response<FoodSearchResponse>
}

data class NutritionResponse(
    val uri: String,
    val calories: Float,
    val totalWeight: Float,
    val dietLabels: List<String>,
    val healthLabels: List<String>,
    val totalNutrients: Nutrients,
    val dailyNutrients: DailyNutrients
)

data class Nutrients(
    val ENERC_KCAL: NutrientInfo?,
    val PROCNT: NutrientInfo?,
    val FAT: NutrientInfo?,
    val CHOCDF: NutrientInfo?,
    val FIBTG: NutrientInfo?
)

data class NutrientInfo(
    val label: String,
    val quantity: Float,
    val unit: String
)

data class DailyNutrients(
    val ENERC_KCAL: DailyNutrientInfo?,
    val PROCNT: DailyNutrientInfo?,
    val FAT: DailyNutrientInfo?,
    val CHOCDF: DailyNutrientInfo?
)

data class DailyNutrientInfo(
    val label: String,
    val quantity: Float,
    val unit: String,
    val percent: Float
)

data class FoodSearchResponse(
    val foods: List<FoodItem>?
)

data class FoodItem(
    val foodId: String,
    val label: String,
    val nutrients: Map<String, Float>
)

// Mock response generator for demo without API key
object MockNutritionService {
    private val foodDatabase = mapOf(
        "burger" to NutritionData(350, 20, 35, 17),
        "pizza" to NutritionData(285, 12, 36, 10),
        "salad" to NutritionData(150, 5, 12, 8),
        "chicken" to NutritionData(165, 31, 0, 3.6),
        "rice" to NutritionData(206, 4.3, 45, 0.4),
        "pasta" to NutritionData(220, 8, 43, 1.3),
        "apple" to NutritionData(95, 0.5, 25, 0.3),
        "banana" to NutritionData(105, 1.3, 27, 0.3),
        "egg" to NutritionData(78, 6, 0.6, 0.6),
        "bread" to NutritionData(265, 9, 49, 3.2),
        "milk" to NutritionData(42, 3.4, 5, 1),
        "coffee" to NutritionData(2, 0.3, 0, 0),
        "water" to NutritionData(0, 0, 0, 0),
        "ice cream" to NutritionData(207, 3.5, 24, 11),
        "chocolate" to NutritionData(546, 4.9, 59, 31),
        "fries" to NutritionData(312, 3.4, 41, 15)
    )
    
    fun estimateCalories(foodDescription: String): NutritionData {
        val lowerDesc = foodDescription.lowercase()
        
        // Match keywords
        for ((keyword, data) in foodDatabase) {
            if (lowerDesc.contains(keyword)) {
                return data
            }
        }
        
        // Default estimate for unknown foods
        return NutritionData(200, 10f, 20f, 8f)
    }
}

data class NutritionData(
    val calories: Int,
    val protein: Float,
    val carbs: Float,
    val fat: Float
)
