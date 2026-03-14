# Plan: Monitorización Parental de Imágenes en WhatsApp mediante IA

**Spec de Referencia:** Spec de Monitorización Parental de WhatsApp MVP

## Contexto Técnico (Stack)
- **Lenguaje/Framework:** Kotlin, Android, Jetpack Compose para UI, Dagger Hilt para Inyección de Dependencias.
- **Base de Datos:** Room (SQLite).
- **Consideraciones:** Arquitectura monolítica estructurada por funcionalidades (Package by Feature) aplicando Clean Architecture internamente en cada feature. El procesamiento se realizará en segundo plano mediante un Foreground Service.

## Decisiones de Diseño Técnico
- **[Monitorización de Archivos]:** Se implementará un Foreground Service que registre un ContentObserver sobre el almacenamiento para detectar de forma fiable cuando una nueva imagen entra en las carpetas de WhatsApp.
- **[Integración con OpenAI API]:** Se utilizará Retrofit2 para consumir explícitamente la API de Moderation de OpenAI (https://developers.openai.com/api/docs/guides/moderation/). Las imágenes se codificarán en Base64 o se enviarán adecuadamente según la documentación, inyectando el API Key mediante el BuildConfig.
- **[Envío de Correos Electrónicos]:** Se integrará la API REST de Resend (plan gratuito) vía Retrofit para enviar las alertas directamente desde la aplicación sin necesidad de levantar un backend propio.
- **[Almacenamiento Local con Room]:** Se creará una base de datos Room que persistirá las alertas de forma local para alimentar la vista del historial, garantizando que el usuario pueda vaciarla en cualquier momento.
- **[Estructura Package by Feature]:** El proyecto se dividirá en módulos lógicos (features: Onboarding, Monitoring, Alerting, History). Cada feature contendrá sus propias capas data, domain y presentation para maximizar la cohesión.

## Estructura de Archivos Propuesta
```text
/app/src/main/java/com/pabloku/picalertsapp
│
├── /core <-- (Código compartido en toda la app)
│   ├── /di (Módulos Hilt: AppModule, NetworkModule)
│   ├── /network (Configuración base de Retrofit y OkHttp)
│   └── /database (AppDatabase genérica)
│
├── /feature/onboarding <-- FEATURE 1: Gestión inicial
│   ├── /data (Datastore o SharedPreferences para el email)
│   ├── /domain (SaveTutorEmailUseCase, GetTutorEmailUseCase)
│   └── /presentation (OnboardingScreen.kt, OnboardingViewModel.kt)
│
├── /feature/monitoring <-- FEATURE 2: Detección y Análisis IA
│   ├── /data
│   │   └── /remote (OpenAiApi.kt para developers.openai.com/..., Dtos)
│   ├── /domain (AnalyzeImageUseCase.kt)
│   └── /presentation (WhatsappMonitorService.kt con ContentObserver)
│
├── /feature/alerting <-- FEATURE 3: Notificación por Email
│   ├── /data (ResendApi.kt para la API de correos)
│   └── /domain (SendAlertEmailUseCase.kt)
│
└── /feature/history <-- FEATURE 4: Registro y visualización
    ├── /data (AlertDao.kt, AlertEntity.kt)
    ├── /domain (GetAlertHistoryUseCase.kt, ClearHistoryUseCase.kt)
    └── /presentation (HistoryScreen.kt, HistoryViewModel.kt)
```
