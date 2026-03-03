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
        "burger" to NutritionData(350, 20f, 35f, 17f),
        "pizza" to NutritionData(285, 12f, 36f, 10f),
        "salad" to NutritionData(150, 5f, 12f, 8f),
        "chicken" to NutritionData(165, 31f, 0f, 3.6f),
        "rice" to NutritionData(206, 4.3f, 45f, 0.4f),
        "pasta" to NutritionData(220, 8f, 43f, 1.3f),
        "apple" to NutritionData(95, 0.5f, 25f, 0.3f),
        "banana" to NutritionData(105, 1.3f, 27f, 0.3f),
        "egg" to NutritionData(78, 6f, 0.6f, 0.6f),
        "bread" to NutritionData(265, 9f, 49f, 3.2f),
        "milk" to NutritionData(42, 3.4f, 5f, 1f),
        "coffee" to NutritionData(2, 0.3f, 0f, 0f),
        "water" to NutritionData(0, 0f, 0f, 0f),
        "ice cream" to NutritionData(207, 3.5f, 24f, 11f),
        "chocolate" to NutritionData(546, 4.9f, 59f, 31f),
        "fries" to NutritionData(312, 3.4f, 41f, 15f)
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
