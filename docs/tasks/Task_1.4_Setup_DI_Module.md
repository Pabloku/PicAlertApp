# Task: [Setup - DI] AppModule

**Phase**: Phase 1 - Setup / Foundation

## Description
Create the generic injection module in `core/di/AppModule.kt` to provide the application context.

## Acceptance Criteria
- [ ] The module is annotated with `@Module` and `@InstallIn(SingletonComponent::class)`.
- [ ] It provides `@ApplicationContext` correctly.
