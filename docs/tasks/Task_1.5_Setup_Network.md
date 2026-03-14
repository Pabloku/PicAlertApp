# Task: [Setup - Network] NetworkModule

**Phase**: Phase 1 - Setup / Foundation

## Description
Create the base Retrofit configuration and OkHttp client, including logging interceptors when needed, in `core/network/NetworkModule.kt`.

## Acceptance Criteria
- [ ] A Retrofit instance is available through dependency injection.
- [ ] `HttpLoggingInterceptor` is configured for the development environment (`debug`).
