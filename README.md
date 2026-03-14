# PicAlert

## Overview

**PicAlert** is an Android parental monitoring app designed to automatically watch the images that enter and leave WhatsApp storage on a device. Its goal is to provide fast, non-intrusive visibility into potentially sensitive visual content without blocking files, altering conversations, or attempting to identify senders or recipients.

The app watches only the image paths used by WhatsApp, analyzes new static image files through the OpenAI Moderation API configured with high sensitivity, and, when inappropriate content is detected, immediately sends an email alert to the parent or guardian. It also keeps a local incident history for later review on the device.

## Product Goal

PicAlert aims to provide a practical, fast-to-build, and transparent parental supervision tool that helps parents or guardians receive near real-time alerts when images with signs of violence, sexual content, or other sensitive categories are detected.

### Operating Principles

- Exclusive monitoring of WhatsApp image folders.
- File-based analysis only.
- Read-only operation: the app does not block, modify, or delete images.
- Transparent UI presence on the device.
- MVP-first prioritization focused on speed and simplicity.

## MVP Scope

The MVP focuses on three core capabilities:

1. Initial parent email setup without registration or authentication.
2. Background detection and analysis of new static images from WhatsApp.
3. Email alert delivery and local storage of generated alert history.

## Main User Stories

### 1. Onboarding and alert setup

**As a** parent or guardian,  
**I want** to enter my email address the first time I open the app,  
**so that** I can receive security alerts in my personal inbox.

#### Acceptance Criteria

- On first launch, the app requests a valid email address without requiring account creation or password setup.
- After onboarding is completed, future launches open the main screen directly without authentication.

### 2. Real-time detection and alerting

**As a** parent or guardian,  
**I want** to receive an automatic email when a suspicious image is detected in WhatsApp,  
**so that** I can be informed immediately about possible exposure to dangerous content.

#### Acceptance Criteria

- With background monitoring active, a new image saved inside WhatsApp folders is analyzed automatically.
- If OpenAI classifies the image as dangerous, the system sends an email to the parent in real time.
- The alert email must include:
  - An image thumbnail.
  - The detected category.
  - The exact date and time when the file was detected on the device.

### 3. Local history management

**As a** parent or guardian reviewing the device in person,  
**I want** to view a history of generated alerts and be able to clear it,  
**so that** I can keep the app organized after reviewing incidents.

#### Acceptance Criteria

- The main screen shows a basic list of previously generated alerts.
- The user can remove the entire local alert history with a dedicated action.

## Key Functional Requirements

- The app must monitor only the storage paths used by WhatsApp for image files.
- The system must send detected images to the OpenAI Moderation API using high sensitivity with all relevant categories enabled by default.
- The app must remain visible and transparent on the device.
- The system must not attempt to infer sender or recipient identity.
- The app must not interfere with the original file; operation is strictly read-only.

## Edge Cases and MVP Decisions

### No internet connection

If an image arrives while the device is offline, it will not be processed and will not be queued for retry. In this MVP, it is treated as not analyzed and no alert is sent.

### OpenAI failure or rate limiting

If the OpenAI API fails, returns an error, or hits usage limits, the system applies a passive safety fallback and treats the image as safe to avoid false alarms or unintended disruption.

### Videos, GIFs, and stickers

These formats are ignored completely. The MVP scope is limited to static image extensions such as `jpg`, `jpeg`, `png`, and similar formats.

### Uninstall or force stop

This first version does not include uninstall protection or anti-force-stop mechanisms. If the app stops running, monitoring stops as well.

## Success Criteria

- Detect a new image, send it to OpenAI, process the response, and dispatch the alert email within 10 to 15 seconds under normal network conditions.
- Deliver a useful parental monitoring tool with low implementation overhead and no custom backend in the MVP.

## Tech Stack

The application is built on a modern Android stack with a strong focus on maintainability, testability, and implementation speed:

- **Kotlin** as the primary language.
- **Jetpack Compose** for declarative UI.
- **Dagger Hilt** for dependency injection.
- **Room** for local SQLite persistence.
- **Retrofit** for external REST integrations.

### Supporting Technologies

- **OkHttp** for HTTP client configuration and interceptors.
- **Foreground Service** for continuous background monitoring.
- **ContentObserver** to detect relevant storage changes.
- **BuildConfig** for environment-based secret and configuration injection.
- **Resend API** for direct email delivery from the app.

## Architecture

PicAlert follows a logically modular monolith based on **Clean Architecture** and organized with **Package by Feature**.

### Architectural Approach

Each feature encapsulates its own layers:

- **presentation**: Compose screens, `ViewModel`, UI state, and event handling.
- **domain**: business use cases and contracts.
- **data**: data sources, repository implementations, DTOs, DAOs, and remote services.

This structure improves feature cohesion, reduces cross-cutting coupling, and keeps the codebase easier to scale without falling into generic package sprawl.

### Why this structure

- Business rules stay isolated from UI and framework concerns.
- External integrations remain contained inside `data`.
- Each feature can evolve with lower impact on the rest of the codebase.
- Repository navigation and maintenance stay clear for small engineering teams.

## Proposed Project Structure

```text
/app/src/main/java/com/tuusuario/parentalcontrol
│
├── /core
│   ├── /di
│   │   ├── AppModule.kt
│   │   └── NetworkModule.kt
│   ├── /network
│   │   ├── RetrofitFactory.kt
│   │   └── NetworkConstants.kt
│   └── /database
│       └── AppDatabase.kt
│
├── /feature/onboarding
│   ├── /data
│   │   └── TutorPreferencesDataSource.kt
│   ├── /domain
│   │   ├── GetTutorEmailUseCase.kt
│   │   └── SaveTutorEmailUseCase.kt
│   └── /presentation
│       ├── OnboardingScreen.kt
│       └── OnboardingViewModel.kt
│
├── /feature/monitoring
│   ├── /data
│   │   └── /remote
│   │       ├── OpenAiApi.kt
│   │       └── ModerationDtos.kt
│   ├── /domain
│   │   └── AnalyzeImageUseCase.kt
│   └── /presentation
│       └── WhatsappMonitorService.kt
│
├── /feature/alerting
│   ├── /data
│   │   └── ResendApi.kt
│   └── /domain
│       └── SendAlertEmailUseCase.kt
│
└── /feature/history
    ├── /data
    │   ├── AlertDao.kt
    │   └── AlertEntity.kt
    ├── /domain
    │   ├── ClearHistoryUseCase.kt
    │   └── GetAlertHistoryUseCase.kt
    └── /presentation
        ├── HistoryScreen.kt
        └── HistoryViewModel.kt
```

## Technical Design by Feature

### Feature: Onboarding

Responsible for capturing and persisting the guardian email address. The MVP does not include authentication or user identity management. The goal is to minimize friction and configure the alert channel on first launch.

### Feature: Monitoring

Implemented through a **Foreground Service** combined with storage observation. It detects new images inside WhatsApp paths and triggers the analysis flow. It is limited to supported file types and never acts on the original content.

### Feature: Alerting

Sends email notifications with the relevant incident details through **Resend** via Retrofit. The email must include a thumbnail, detected category, and the exact detection timestamp.

### Feature: History

Stores alerts locally using Room. It supports reviewing previous incidents and clearing the full history from the UI.

## High-Level Functional Flow

1. The user installs the app and enters the guardian email during onboarding.
2. A `ForegroundService` starts monitoring WhatsApp image folders.
3. When a new image is detected, the app validates the file type and path.
4. The image is sent to OpenAI Moderation for classification.
5. If the content exceeds the configured threshold:
   - A local alert is generated.
   - The incident is persisted in Room.
   - An email is sent through Resend.
6. The guardian can review or clear the alert history from the app.

## External Integrations

### OpenAI Moderation API

Used to analyze images with a conservative, high-sensitivity configuration. The integration is expected to use Retrofit and dedicated DTO models so the external contract remains isolated from the domain layer.

### Resend API

Used as the email provider for the MVP, avoiding the need to build and operate a custom backend. This keeps delivery fast and reduces initial operational complexity.

## Security and Privacy

- The app is designed for file analysis, not conversation analysis.
- It does not attempt to extract social context from detected content.
- Processing is strictly read-only with respect to local images.
- API keys must be kept out of source code, ideally through `BuildConfig` and environment-based configuration.

## MVP Limitations

- No offline retry queue or deferred processing pipeline.
- No web dashboard or backend intermediary.
- No protection against force stop or uninstall.
- No support for videos, GIFs, or stickers.
- Alert history is stored locally on the device only.

## Post-MVP Roadmap

- Resilient queue for offline analysis and retries.
- Advanced category and sensitivity controls.
- Support for multiple guardians or multiple email recipients.
- Incident dashboard with remote synchronization.
- Better hardening against battery restrictions and device restarts.
- Wider coverage for image sources beyond WhatsApp.

## Repository Bootstrap

This repository starts by documenting the product vision, MVP scope, and intended technical structure. The natural next step is to scaffold the Android project with base dependencies, Hilt, Room, Retrofit, and initial Compose navigation.

## Current Status

Repository initialized with the base project documentation for **PicAlert**.
