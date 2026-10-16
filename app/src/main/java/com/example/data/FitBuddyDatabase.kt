package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.FoodItem
import com.example.model.MealLog
import com.example.model.SavedWorkoutPlan
import com.example.model.WeightLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        FoodItem::class,
        MealLog::class,
        WeightLog::class,
        SavedWorkoutPlan::class
    ],
    version = 1,
    exportSchema = false
)
abstract class FitBuddyDatabase : RoomDatabase() {

    abstract fun fitBuddyDao(): FitBuddyDao

    companion object {
        @Volatile
        private var INSTANCE: FitBuddyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): FitBuddyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FitBuddyDatabase::class.java,
                    "fit_buddy_database"
                )
                    .addCallback(FitBuddyDatabaseCallback(scope))
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class FitBuddyDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialFoods(database.fitBuddyDao())
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // Ensure foods are populated if empty
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        val count = database.fitBuddyDao().getFoodCount()
                        if (count == 0) {
                            populateInitialFoods(database.fitBuddyDao())
                        }
                    }
                }
            }

            suspend fun populateInitialFoods(dao: FitBuddyDao) {
                dao.insertFoods(SeedData.initialFoods)
            }
        }
    }
}
