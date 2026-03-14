# Tarea: [Historia 2 - Integración] Orquestación

**Fase**: Fase 3 - Historia 2 (MVP)

## Descripción
Orquestar el flujo: `ContentObserver` detecta -> `AnalyzeImageUseCase` -> si es peligrosa -> `GetTutorEmailUseCase` -> `SendAlertEmailUseCase`.

## Criterios de Aceptación
- [ ] El flujo completo se ejecuta en un CoroutineScope adecuado (Dispatchers.IO).
- [ ] No bloquea el hilo principal.