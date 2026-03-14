# Repository Guidelines

## Project Structure & Module Organization
`PicAlertsApp` is a single-module Android app rooted at `app/`. Kotlin sources live in `app/src/main/java/com/pabloku/picalertsapp`, UI resources in `app/src/main/res`, local unit tests in `app/src/test`, and instrumentation tests in `app/src/androidTest`. Product and implementation planning lives under `docs/`, especially `docs/spec.md`, `docs/plan.md`, and `docs/tasks/`.

The current codebase is still close to the Android template, but the intended direction is feature-based packaging with `core/` plus `feature/<name>/{data,domain,presentation}`. Keep new code aligned with that structure instead of adding broad packages such as `utils` or `helpers`.

## Build, Test, and Development Commands
Use the Gradle wrapper from the repository root:

- `./gradlew assembleDebug` or `gradlew.bat assembleDebug`: builds the debug APK.
- `./gradlew testDebugUnitTest`: runs JVM unit tests in `app/src/test`.
- `./gradlew connectedDebugAndroidTest`: runs device/emulator instrumentation tests.
- `./gradlew lint`: runs Android lint checks before review.

Use Android Studio for emulator-based work; the project targets SDK 36 and Java 11 bytecode.

## Coding Style & Naming Conventions
Write Kotlin with 4-space indentation and default Kotlin style. Use `PascalCase` for classes and composables, `camelCase` for functions and properties, and `UPPER_SNAKE_CASE` for constants. Name screens and view models by feature, for example `OnboardingScreen` and `HistoryViewModel`.

Prefer small, explicit files within feature packages. Keep Compose UI stateless where practical and push business logic into domain/use-case classes once those layers are added.

## Testing Guidelines
Use JUnit 4 for local tests and AndroidX test tooling for instrumentation tests. Name test files after the subject under test, such as `SaveTutorEmailUseCaseTest`, and name test methods with behavior-focused `when_then` or `expectedResult` phrasing.

Add unit tests for new domain logic and repository behavior. Add instrumentation or Compose UI tests when changing navigation, services, or user-visible flows.

## Commit & Pull Request Guidelines
Recent history uses uppercase prefixes like `CHORE: add ...`. Follow `TYPE: short imperative summary`, where `TYPE` is typically `FEAT`, `FIX`, `CHORE`, or `DOCS`.

Pull requests should include a concise description, linked task or issue, test evidence (`testDebugUnitTest`, emulator run, or lint), and screenshots for UI changes. Keep PRs narrowly scoped so reviewers can validate one feature or refactor at a time.

## Security & Configuration Tips
Do not commit API keys or parent email data. Keep secrets out of source code and prefer `BuildConfig`, `local.properties`, or other local-only configuration paths already described in `README.md`.
