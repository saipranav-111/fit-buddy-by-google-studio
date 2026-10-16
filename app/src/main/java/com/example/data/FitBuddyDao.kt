package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.FoodItem
import com.example.model.MealLog
import com.example.model.SavedWorkoutPlan
import com.example.model.WeightLog
import kotlinx.coroutines.flow.Flow

@Dao
interface FitBuddyDao {

    // --- Foods ---
    @Query("SELECT * FROM food_items ORDER BY isPopular DESC, name ASC")
    fun getAllFoods(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' OR category LIKE '%' || :query || '%' ORDER BY isPopular DESC, name ASC")
    fun searchFoods(query: String): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE isPopular = 1 ORDER BY name ASC")
    fun getPopularFoods(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE id = :id LIMIT 1")
    suspend fun getFoodById(id: Long): FoodItem?

    @Query("SELECT COUNT(*) FROM food_items")
    suspend fun getFoodCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoods(foods: List<FoodItem>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: FoodItem): Long

    // --- Meal Logs ---
    @Query("SELECT * FROM meal_logs WHERE date = :date ORDER BY timestamp DESC")
    fun getMealLogsByDate(date: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC, timestamp ASC")
    fun getMealLogsBetweenDates(startDate: String, endDate: String): Flow<List<MealLog>>

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMealLogs(limit: Int = 10): Flow<List<MealLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealLog(mealLog: MealLog): Long

    @Update
    suspend fun updateMealLog(mealLog: MealLog)

    @Query("DELETE FROM meal_logs WHERE id = :id")
    suspend fun deleteMealLog(id: Long)

    @Query("SELECT DISTINCT date FROM meal_logs ORDER BY date DESC")
    fun getAllLoggedDates(): Flow<List<String>>

    // --- Weight Logs ---
    @Query("SELECT * FROM weight_logs ORDER BY date ASC, timestamp ASC")
    fun getAllWeightLogs(): Flow<List<WeightLog>>

    @Query("SELECT * FROM weight_logs ORDER BY date DESC, timestamp DESC LIMIT 1")
    fun getLatestWeightLog(): Flow<WeightLog?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(weightLog: WeightLog): Long

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun deleteWeightLog(id: Long)

    // --- Saved Workouts ---
    @Query("SELECT * FROM saved_workouts ORDER BY createdAt DESC")
    fun getAllSavedWorkouts(): Flow<List<SavedWorkoutPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutPlan(plan: SavedWorkoutPlan): Long

    @Query("DELETE FROM saved_workouts WHERE id = :id")
    suspend fun deleteWorkoutPlan(id: Long)
}
