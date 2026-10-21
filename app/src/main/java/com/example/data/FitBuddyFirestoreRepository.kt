package com.example.data

import android.content.Context
import android.util.Log
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject

enum class OperationType(val value: String) {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    LIST("list"),
    GET("get"),
    WRITE("write"),
}

fun handleFirestoreError(exception: Exception, operationType: OperationType, path: String?): String {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val providerInfoList = currentUser?.providerData?.map { provider ->
        JSONObject().apply {
            put("providerId", provider.providerId)
            put("email", provider.email)
        }
    } ?: emptyList()

    val authInfoJson = JSONObject().apply {
        put("userId", currentUser?.uid)
        put("email", currentUser?.email)
        put("emailVerified", currentUser?.isEmailVerified)
        put("tenantId", currentUser?.tenantId)
        put("providerInfo", JSONArray(providerInfoList))
    }

    val errorInfoJson = JSONObject().apply {
        put("error", exception.message ?: exception.toString())
        put("operationType", operationType.value)
        put("path", path)
        put("authInfo", authInfoJson)
    }

    val jsonString = errorInfoJson.toString()
    Log.e("FirestoreError", "Firestore Error: $jsonString")
    return jsonString
}

class FitBuddyFirestoreRepository(val db: FirebaseFirestore) {

    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private val auth = Firebase.auth

    fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    suspend fun saveUserProfile(profile: FirestoreUserProfile): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val payload = mapOf(
                "userId" to uid,
                "name" to profile.name,
                "age" to profile.age,
                "gender" to profile.gender,
                "heightCm" to profile.heightCm,
                "weightKg" to profile.weightKg,
                "activityLevel" to profile.activityLevel,
                "goal" to profile.goal,
                "targetCalories" to profile.targetCalories,
                "targetProteinG" to profile.targetProteinG,
                "targetCarbsG" to profile.targetCarbsG,
                "targetFatG" to profile.targetFatG,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun getUserProfile(userId: String): Result<FirestoreUserProfile?> {
        val docRef = db.collection("users").document(userId)
        return try {
            val snapshot = docRef.get().await()
            val profile = snapshot.toObject(
                FirestoreUserProfile::class.java,
                DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
            )
            Result.success(profile)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    fun observeMealLogs(userId: String): Flow<List<FirestoreMealLog>> {
        val path = "users/$userId/meal_logs"
        return db.collection("users").document(userId).collection("meal_logs")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    FirestoreMealLog::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun addMealLog(log: FirestoreMealLog): Result<String> {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("meal_logs")
        return try {
            val payload = mapOf(
                "userId" to uid,
                "foodName" to log.foodName,
                "portionGrams" to log.portionGrams,
                "calories" to log.calories,
                "proteinG" to log.proteinG,
                "carbsG" to log.carbsG,
                "fatG" to log.fatG,
                "mealType" to log.mealType,
                "date" to log.date,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val docRef = collectionRef.add(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, collectionRef.path)
            Result.failure(e)
        }
    }

    suspend fun deleteMealLog(mealId: String): Result<Unit> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid).collection("meal_logs").document(mealId)
        return try {
            docRef.delete().await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            Result.failure(e)
        }
    }

    fun observeWeightLogs(userId: String): Flow<List<FirestoreWeightLog>> {
        val path = "users/$userId/weight_logs"
        return db.collection("users").document(userId).collection("weight_logs")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    FirestoreWeightLog::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun addWeightLog(log: FirestoreWeightLog): Result<String> {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("weight_logs")
        return try {
            val payload = mapOf(
                "userId" to uid,
                "weightKg" to log.weightKg,
                "date" to log.date,
                "note" to log.note,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val docRef = collectionRef.add(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, collectionRef.path)
            Result.failure(e)
        }
    }

    fun observeWorkoutPlans(userId: String): Flow<List<FirestoreWorkoutPlan>> {
        val path = "users/$userId/workout_plans"
        return db.collection("users").document(userId).collection("workout_plans")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(
                    FirestoreWorkoutPlan::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            }
            .catch { error ->
                if (error is Exception) handleFirestoreError(error, OperationType.LIST, path)
                throw error
            }
    }

    suspend fun saveWorkoutPlan(plan: FirestoreWorkoutPlan): Result<String> {
        val uid = requireUserId()
        val collectionRef = db.collection("users").document(uid).collection("workout_plans")
        return try {
            val payload = mapOf(
                "userId" to uid,
                "goal" to plan.goal,
                "level" to plan.level,
                "daysPerWeek" to plan.daysPerWeek,
                "splitName" to plan.splitName,
                "planSummary" to plan.planSummary,
                "daysJson" to plan.daysJson,
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            val docRef = collectionRef.add(payload).await()
            Result.success(docRef.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, collectionRef.path)
            Result.failure(e)
        }
    }
}
