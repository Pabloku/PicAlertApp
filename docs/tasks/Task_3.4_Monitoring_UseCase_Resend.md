# Tarea: [Historia 2 - Dominio] SendAlertEmailUseCase

**Fase**: Fase 3 - Historia 2 (MVP)

## Descripción
Crear el caso de uso `SendAlertEmailUseCase.kt` en `feature/alerting/domain/` que estructure el correo (incluyendo la categoría y miniatura) y llame a Resend.

## Criterios de Aceptación
- [ ] Genera un template HTML básico con los datos de la alerta.
- [ ] Maneja errores de envío de forma silenciosa (para no crashear el background).