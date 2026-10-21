const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

// 1. Unauthenticated checks
test("Unauthenticated user: cannot read or write user profiles", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).set({
    userId: ALICE_UID,
    name: "Alice",
    targetCalories: 2000,
    createdAt: new Date(),
  }));
});

test("Unauthenticated user: cannot access meal logs", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).collection("meal_logs").get());
});

// 2. Authenticated user ownership checks
test("Authenticated user: can create, read, and update own profile", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const profileRef = aliceDb.collection("users").doc(ALICE_UID);

  await assertSucceeds(profileRef.set({
    userId: ALICE_UID,
    name: "Alice",
    age: 26,
    gender: "FEMALE",
    heightCm: 168,
    weightKg: 62.5,
    activityLevel: "LIGHT",
    goal: "MAINTAIN",
    targetCalories: 1950,
    createdAt: new Date(),
  }));

  await assertSucceeds(profileRef.get());

  await assertSucceeds(profileRef.update({
    weightKg: 62.0,
    updatedAt: new Date(),
  }));
});

test("Cross-user isolation: Bob cannot read or write Alice's profile", async () => {
  // Seed Alice's profile via admin
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      name: "Alice",
      targetCalories: 2000,
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).update({
    name: "Hacked",
  }));
});

// 3. Subcollection checks (Meal Logs, Weight Logs, Workout Plans)
test("Authenticated user: can create and read own meal log", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const mealRef = aliceDb.collection("users").doc(ALICE_UID).collection("meal_logs").doc("meal_1");

  await assertSucceeds(mealRef.set({
    userId: ALICE_UID,
    foodName: "Oatmeal with Blueberries",
    portionGrams: 200,
    calories: 280,
    proteinG: 10,
    carbsG: 48,
    fatG: 5,
    mealType: "BREAKFAST",
    date: "2026-10-16",
    createdAt: new Date(),
  }));

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).collection("meal_logs").get());
});

test("Cross-user isolation: Bob cannot read Alice's meal logs", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("users").doc(ALICE_UID).collection("meal_logs").doc("meal_1").set({
      userId: ALICE_UID,
      foodName: "Salad",
      portionGrams: 150,
      calories: 120,
      mealType: "LUNCH",
      date: "2026-10-16",
      createdAt: new Date(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("meal_logs").doc("meal_1").get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).collection("meal_logs").get());
});

test("Authenticated user: can create and read weight log and workout plan", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const weightRef = aliceDb.collection("users").doc(ALICE_UID).collection("weight_logs").doc("w_1");

  await assertSucceeds(weightRef.set({
    userId: ALICE_UID,
    weightKg: 62.0,
    date: "2026-10-16",
    note: "Feeling great",
    createdAt: new Date(),
  }));

  const planRef = aliceDb.collection("users").doc(ALICE_UID).collection("workout_plans").doc("plan_1");
  await assertSucceeds(planRef.set({
    userId: ALICE_UID,
    goal: "BUILD_MUSCLE",
    splitName: "Upper Lower 4-Day",
    daysJson: "[]",
    createdAt: new Date(),
  }));
});
