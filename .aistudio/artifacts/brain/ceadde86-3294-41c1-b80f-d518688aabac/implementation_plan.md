# Connect Fit Buddy to Custom Firebase Project (`fit-buddy-46c65`)

Switch the backend cloud integration and database synchronization from the default generated project to the user's designated Firebase project `fit-buddy-46c65`, ensuring user progress, meal logs, body weight, and workout routines persist seamlessly.

## User Review & Critical Decisions

> [!IMPORTANT]
> The application will be re-provisioned and linked to your specific Firebase project **`fit-buddy-46c65`**. Your Android debug keystore signing certificate (SHA-1 & SHA-256) will be registered with the new project to enable Google Sign-In via Credential Manager, and zero-trust Firestore security rules will be deployed to your database.

- **Confirmed Project ID**: `fit-buddy-46c65`
- **Confirmed Project Number**: `438919608887`
- **Authentication Method**: Google Sign-In via Jetpack `CredentialManager` (`GetSignInWithGoogleOption`)
- **Data Persistence**: Cloud Firestore with multi-tenant user isolation (`/users/{userId}/...`)

---

## 1. Overview & Core Concept

- **What It Does**: Connects the complete Fit Buddy Android app to the user's dedicated Firebase Cloud project (`fit-buddy-46c65`). Users sign in with their Google account to securely back up and access their nutritional targets, meal logs, body weight checkpoints, and AI-generated workout routines across devices.
- **Target Audience**: Fitness enthusiasts and gym-goers who want their nutrition logs, weight tracker, and training routines safely stored in the cloud.
- **Key Value**: Guarantees zero data loss across app reinstalls or device changes by migrating persistence to the user's personal Firebase backend.

---

## 2. User Experience & Visual Design

- **Key User Flows**:
  1. **Authentication Gate**: On app launch, unauthenticated users see the modern Fit Buddy Welcome screen with a prominent "Sign in with Google" button and privacy assurance.
  2. **Automatic Cloud Sync**: Once signed in, existing local progress (or previously stored cloud data) synchronizes bidirectionally with Firestore.
  3. **Seamless Tracking**: Adding meals in Food Log, logging weights in Progress Profile, or saving routines in AI Coach automatically writes to Cloud Firestore.
  4. **Account & Status**: The Profile screen clearly displays the connected Google account, cloud sync badge, and a secure sign-out option.
- **Visual Identity**:
  - *Color Palette*: Emerald Green accent (`#10B981`), deep athletic charcoal surfaces (`#12161A` / `#1B2228`), crisp high-contrast typography.
  - *Components*: Material 3 elevated cards, animated linear progress bars for macros, and interactive chips.

---

## 3. Key Product Decisions & Trade-Offs

- **Dedicated User Project (`fit-buddy-46c65`)**:
  - *Chosen Approach*: Provision and generate updated `google-services.json` targeting the user's explicit project ID.
  - *Why*: Gives the user direct ownership of their database, quotas, and Firebase console logs.
- **Zero-Trust Hardened Security Rules**:
  - *Chosen Approach*: Enforce strict user ownership (`request.auth.uid == userId`) and schema validators (`isValidMealLog`, `isValidUserProfile`, etc.).
  - *Why*: Eliminates cross-user leaks while allowing full real-time reading and writing for the authenticated account owner.

---

## 4. Technical Architecture & Data Strategy

```text
┌─────────────────────────────────────────────────────────────────┐
│                     Fit Buddy Android App                       │
├───────────────────────┬─────────────────────────────────────────┤
│ Jetpack Compose UI    │ Dashboard, Food Log, AI Coach, Profile  │
│ State Management      │ FitBuddyViewModel (StateFlow & Coroutines)│
├───────────────────────┼─────────────────────────────────────────┤
│ Local Cache (Room DB) │ Offline-first DAO for immediate UX      │
│ Cloud Layer (Firestore)│ FitBuddyFirestoreRepository (Cloud Sync)│
└───────────────────────┴────────────────────┬────────────────────┘
                                             │ gRPC / SSL
                                             ▼
┌─────────────────────────────────────────────────────────────────┐
│               Firebase Project: fit-buddy-46c65                 │
├───────────────────────┬─────────────────────────────────────────┤
│ Firebase Auth         │ Google Identity Provider (OAuth 2.0)    │
│ Cloud Firestore       │ /users/{userId}                         │
│                       │   ├── /meal_logs/{mealId}               │
│                       │   ├── /weight_logs/{logId}              │
│                       │   └── /workout_plans/{planId}           │
│ Security Rules        │ firestore.rules (Strict User Isolation) │
└───────────────────────┴─────────────────────────────────────────┘
```

- **Data Models**:
  - `UserProfile`: Height, weight, goal, target calories, and macro breakdown.
  - `MealLog`: Food name, portion grams, calories, protein, carbs, fat, meal category, date.
  - `WeightLog`: Checkpoint weight, date, optional note.
  - `SavedWorkoutPlan`: Goal, split name, summary, and structured exercise days.
- **Execution Steps**:
  1. Call `ProvisionFirebase` with `project_id: "fit-buddy-46c65"`, package name, and extracted debug certificate hashes.
  2. Verify SHA-1 registration in updated `google-services.json`.
  3. Deploy hardened `firestore.rules` to the new project via `DeployRules`.
  4. Compile and assemble the ready-to-run APK.
