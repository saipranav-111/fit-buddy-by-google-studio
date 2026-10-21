package com.example.data

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FitBuddyRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun saveAndGetUserProfile_authenticatedOwner_succeeds() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = FitBuddyFirestoreRepository(firestore)

    val profile = FirestoreUserProfile(
      userId = uid,
      name = "Alice",
      age = 27,
      gender = "FEMALE",
      heightCm = 165.0,
      weightKg = 60.0,
      activityLevel = "MODERATE",
      goal = "FIT",
      targetCalories = 1800
    )

    val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.saveUserProfile(profile) }
    assertTrue(saveResult.isSuccess)

    val fetchResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.getUserProfile(uid) }
    assertTrue(fetchResult.isSuccess)
    val fetched = fetchResult.getOrThrow()
    assertEquals("Alice", fetched?.name)
    assertEquals(1800, fetched?.targetCalories)
  }

  @Test
  fun addMealLog_andObserve_authenticatedOwner_succeeds() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repo = FitBuddyFirestoreRepository(firestore)

    val meal = FirestoreMealLog(
      userId = uid,
      foodName = "Grilled Chicken Breast",
      portionGrams = 150.0,
      calories = 247.5,
      proteinG = 46.5,
      carbsG = 0.0,
      fatG = 5.4,
      mealType = "Lunch",
      date = "2026-10-16"
    )

    val addResult = withTimeout(DEFAULT_TIMEOUT_MS) { repo.addMealLog(meal) }
    assertTrue(addResult.isSuccess)
    val mealId = addResult.getOrThrow()
    assertTrue(mealId.isNotEmpty())

    val logs = withTimeout(FLOW_TIMEOUT_MS) {
      repo.observeMealLogs(uid).first { list -> list.any { it.id == mealId } }
    }
    assertTrue(logs.any { it.foodName == "Grilled Chicken Breast" })
  }

  @Test
  fun getUserProfile_crossUserAccess_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    val aliceRepo = FitBuddyFirestoreRepository(firestore)
    val profile = FirestoreUserProfile(
      userId = aliceUid,
      name = "Alice",
      targetCalories = 2000
    )
    withTimeout(DEFAULT_TIMEOUT_MS) { aliceRepo.saveUserProfile(profile) }

    // Switch to Bob
    signInTestUser(BOB_EMAIL)
    val bobRepo = FitBuddyFirestoreRepository(firestore)

    val result = withTimeout(DEFAULT_TIMEOUT_MS) { bobRepo.getUserProfile(aliceUid) }
    assertTrue(result.isFailure)
    val exception = result.exceptionOrNull()
    assertTrue(exception is FirebaseFirestoreException)
    val fse = exception as FirebaseFirestoreException
    assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fse.code)
  }

  @Test
  fun observeMealLogs_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    val aliceUid = signInTestUser(ALICE_EMAIL)
    auth.signOut()

    val repo = FitBuddyFirestoreRepository(firestore)
    val exception = assertFailsWith<FirebaseFirestoreException> {
      withTimeout(FLOW_TIMEOUT_MS) {
        repo.observeMealLogs(aliceUid).first()
      }
    }
    assertThat(exception.code).isEqualTo(FirebaseFirestoreException.Code.PERMISSION_DENIED)
  }

  private companion object {
    const val ALICE_EMAIL = "alice@fitbuddy.test"
    const val BOB_EMAIL = "bob@fitbuddy.test"
    const val DEFAULT_TIMEOUT_MS = 6000L
    const val FLOW_TIMEOUT_MS = 5000L
  }
}
