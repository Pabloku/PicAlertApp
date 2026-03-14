# Tarea: [Historia 1 - Navegación] Flujo Inicial

**Fase**: Fase 2 - Historia 1 (MVP)

## Descripción
Modificar el `NavHost` en `MainActivity.kt` para que arranque en `OnboardingScreen` si no hay email guardado, o salte directamente a la pantalla principal si ya existe.

## Criterios de Aceptación
- [ ] Al abrir la app, verifica asíncronamente el DataStore.
- [ ] Redirige correctamente según el estado de configuración.