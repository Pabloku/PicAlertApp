# Task: [Story 2 - Integration] Orchestration

**Phase**: Phase 3 - Story 2 (MVP)

## Description
Orchestrate the flow: `ContentObserver` detects -> `AnalyzeImageUseCase` -> if dangerous -> `GetTutorEmailUseCase` -> `SendAlertEmailUseCase`.

## Acceptance Criteria
- [ ] The full flow runs in an appropriate `CoroutineScope` such as `Dispatchers.IO`.
- [ ] It does not block the main thread.
