# Task: [Story 1 - Navigation] Initial Flow

**Phase**: Phase 2 - Story 1 (MVP)

## Description
Modify the `NavHost` in `MainActivity.kt` so it starts at `OnboardingScreen` when no email is stored, or jumps directly to the main screen when one already exists.

## Acceptance Criteria
- [ ] When the app opens, it checks DataStore asynchronously.
- [ ] It redirects correctly based on the setup state.
