package com.example.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class FirestoreUserProfile(
    @DocumentId val id: String = "",
    val userId: String = "",
    val name: String = "",
    val age: Int = 25,
    val gender: String = "MALE",
    val heightCm: Double = 175.0,
    val weightKg: Double = 70.0,
    val activityLevel: String = "MODERATE",
    val goal: String = "MAINTAIN",
    val targetCalories: Int = 2200,
    val targetProteinG: Double = 140.0,
    val targetCarbsG: Double = 250.0,
    val targetFatG: Double = 70.0,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreMealLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val foodName: String = "",
    val portionGrams: Double = 100.0,
    val calories: Double = 0.0,
    val proteinG: Double = 0.0,
    val carbsG: Double = 0.0,
    val fatG: Double = 0.0,
    val mealType: String = "Breakfast",
    val date: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreWeightLog(
    @DocumentId val id: String = "",
    val userId: String = "",
    val weightKg: Double = 70.0,
    val date: String = "",
    val note: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class FirestoreWorkoutPlan(
    @DocumentId val id: String = "",
    val userId: String = "",
    val goal: String = "",
    val level: String = "INTERMEDIATE",
    val daysPerWeek: Int = 4,
    val splitName: String = "",
    val planSummary: String = "",
    val daysJson: String = "[]",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
