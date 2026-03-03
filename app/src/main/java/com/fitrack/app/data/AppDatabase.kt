package com.fitrack.app.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// DAOs
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1")
    fun getProfile(): Flow<UserProfile?>
    
    @Query("SELECT * FROM user_profile WHERE id = 1")
    suspend fun getProfileOnce(): UserProfile?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProfile(profile: UserProfile)
}

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs ORDER BY date DESC")
    fun getAllWeightLogs(): Flow<List<WeightLog>>
    
    @Query("SELECT * FROM weight_logs ORDER BY date DESC LIMIT 1")
    suspend fun getLatestWeight(): WeightLog?
    
    @Query("SELECT AVG(weightKg) FROM weight_logs WHERE date BETWEEN :start AND :end")
    suspend fun getAverageWeight(start: Long, end: Long): Float?
    
    @Insert
    suspend fun insert(weightLog: WeightLog): Long
    
    @Delete
    suspend fun delete(weightLog: WeightLog)
    
    @Query("DELETE FROM weight_logs")
    suspend fun deleteAll()
}

@Dao
interface BodyMeasurementDao {
    @Query("SELECT * FROM body_measurements ORDER BY date DESC")
    fun getAllMeasurements(): Flow<List<BodyMeasurement>>
    
    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT 1")
    suspend fun getLatestMeasurement(): BodyMeasurement?
    
    @Insert
    suspend fun insert(measurement: BodyMeasurement): Long
    
    @Delete
    suspend fun delete(measurement: BodyMeasurement)
}

// Update main database
@Database(
    entities = [
        Workout::class, 
        FoodLog::class,
        UserProfile::class,
        WeightLog::class,
        BodyMeasurement::class
    ], 
    version = 2, 
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun foodLogDao(): FoodLogDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun bodyMeasurementDao(): BodyMeasurementDao
    
    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitrack_database"
                )
                .fallbackToDestructiveMigration() // For simplicity in dev
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// Room Database Entities (moved from Database.kt)
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
