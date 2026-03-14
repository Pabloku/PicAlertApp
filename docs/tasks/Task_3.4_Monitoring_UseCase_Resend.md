# Task: [Story 2 - Domain] SendAlertEmailUseCase

**Phase**: Phase 3 - Story 2 (MVP)

## Description
Create `SendAlertEmailUseCase.kt` in `feature/alerting/domain/` so it builds the email content, including the category and thumbnail, and calls Resend.

## Acceptance Criteria
- [ ] It generates a basic HTML template with the alert data.
- [ ] It handles send errors silently so the background flow does not crash.
