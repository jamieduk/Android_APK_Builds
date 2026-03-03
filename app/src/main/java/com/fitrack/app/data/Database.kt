package com.fitrack.app.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// Data Models
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

// DAOs
@Dao
interface WorkoutDao {
    @Query("SELECT * FROM workouts ORDER BY date DESC")
    fun getAllWorkouts(): Flow<List<Workout>>
    
    @Query("SELECT * FROM workouts WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getWorkoutsByDateRange(start: Long, end: Long): Flow<List<Workout>>
    
    @Insert
    suspend fun insert(workout: Workout): Long
    
    @Delete
    suspend fun delete(workout: Workout)
}

@Dao
interface FoodLogDao {
    @Query("SELECT * FROM food_logs ORDER BY date DESC")
    fun getAllFoodLogs(): Flow<List<FoodLog>>
    
    @Query("SELECT * FROM food_logs WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getFoodLogsByDateRange(start: Long, end: Long): Flow<List<FoodLog>>
    
    @Query("SELECT SUM(calories) FROM food_logs WHERE date BETWEEN :start AND :end")
    fun getTotalCalories(start: Long, end: Long): Flow<Int?>
    
    @Insert
    suspend fun insert(foodLog: FoodLog): Long
    
    @Delete
    suspend fun delete(foodLog: FoodLog)
}

// Database
@Database(entities = [Workout::class, FoodLog::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun foodLogDao(): FoodLogDao
    
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitrack_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
