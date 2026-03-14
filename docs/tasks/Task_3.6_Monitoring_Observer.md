# Tarea: [Historia 2 - Servicio] ContentObserver

**Fase**: Fase 3 - Historia 2 (MVP)

## Descripción
Implementar un `ContentObserver` dentro del `WhatsappMonitorService.kt` que vigile las URIs de la galería y filtre por las rutas de almacenamiento de WhatsApp.

## Criterios de Aceptación
- [ ] Se dispara solo cuando se inserta una nueva imagen.
- [ ] Filtra correctamente los paths de `/WhatsApp/Media/WhatsApp Images/`.