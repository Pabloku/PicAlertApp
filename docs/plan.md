# Plan: AI-Based Parental Monitoring of WhatsApp Images

**Reference Spec:** WhatsApp Parental Monitoring MVP Spec

## Technical Context (Stack)
- **Language/Framework:** Kotlin, Android, Jetpack Compose for UI, and Dagger Hilt for dependency injection.
- **Database:** Room (SQLite).
- **Considerations:** A modular monolith structured by feature (Package by Feature), applying Clean Architecture inside each feature. Processing will run in the background through a Foreground Service.

## Technical Design Decisions
- **[File Monitoring]:** A Foreground Service will register a ContentObserver on storage to reliably detect when a new image enters WhatsApp folders.
- **[OpenAI API Integration]:** Retrofit2 will be used to consume the OpenAI Moderation API explicitly (https://developers.openai.com/api/docs/guides/moderation/). Images will be encoded as Base64 or sent in the format required by the documentation, with the API key injected through BuildConfig.
- **[Email Delivery]:** The Resend REST API free tier will be integrated through Retrofit to send alerts directly from the app without introducing a custom backend.
- **[Local Storage with Room]:** A Room database will persist alerts locally to power the history view, while allowing the user to clear it at any time.
- **[Package by Feature Structure]:** The project will be split into logical feature areas: Onboarding, Monitoring, Alerting, and History. Each feature will contain its own data, domain, and presentation layers to maximize cohesion.

## Proposed File Structure
```text
/app/src/main/java/com/pabloku/picalertsapp
│
├── /core <-- (Shared code across the app)
│   ├── /di (Hilt modules: AppModule, NetworkModule)
│   ├── /network (Base Retrofit and OkHttp configuration)
│   └── /database (Generic AppDatabase)
│
├── /feature/onboarding <-- FEATURE 1: Initial setup
│   ├── /data (DataStore or SharedPreferences for the email)
│   ├── /domain (SaveTutorEmailUseCase, GetTutorEmailUseCase)
│   └── /presentation (OnboardingScreen.kt, OnboardingViewModel.kt)
│
├── /feature/monitoring <-- FEATURE 2: Detection and AI analysis
│   ├── /data
│   │   └── /remote (OpenAiApi.kt for developers.openai.com/... and DTOs)
│   ├── /domain (AnalyzeImageUseCase.kt)
│   └── /presentation (WhatsappMonitorService.kt with ContentObserver)
│
├── /feature/alerting <-- FEATURE 3: Email notification
│   ├── /data (ResendApi.kt for the email API)
│   └── /domain (SendAlertEmailUseCase.kt)
│
└── /feature/history <-- FEATURE 4: Record and display
    ├── /data (AlertDao.kt, AlertEntity.kt)
    ├── /domain (GetAlertHistoryUseCase.kt, ClearHistoryUseCase.kt)
    └── /presentation (HistoryScreen.kt, HistoryViewModel.kt)
```
