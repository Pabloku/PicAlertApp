# Tarea: [Setup - DI] AppModule

**Fase**: Fase 1 - Setup / Fundacional

## Descripción
Crear el módulo de inyección genérico en `core/di/AppModule.kt` para proveer el contexto de la aplicación.

## Criterios de Aceptación
- [ ] El módulo está anotado con `@Module` y `@InstallIn(SingletonComponent::class)`.
- [ ] Provee el `@ApplicationContext` correctamente.