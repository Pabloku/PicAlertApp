# Spec: Monitorización Parental de Imágenes en WhatsApp mediante IA

## Descripción General
Una aplicación de Android diseñada como herramienta de control parental que monitoriza automáticamente las imágenes que se envían y reciben a través de las carpetas de WhatsApp (chats privados y grupales). Utiliza la API de Moderation de OpenAI configurada con alta sensibilidad para analizar el contenido visual. Si detecta material inapropiado (violencia, contenido sexual, etc.), la app no bloquea la imagen, pero envía inmediatamente una alerta por correo electrónico al tutor con los detalles del incidente, basándose puramente en el análisis del archivo.

## Historias de Usuario (El "Qué")

### Historia 1: Onboarding y configuración de alertas (Prioridad: 1 - MVP)
**Como** padre o tutor, **Quiero** introducir mi dirección de correo electrónico al abrir la app por primera vez, **Para** poder recibir las alertas de seguridad en mi buzón personal.

**Criterios de Aceptación (Cómo sabremos que funciona):**
- Dado que el usuario acaba de instalar la app y la abre por primera vez, Cuando se muestra la pantalla de inicio, Entonces el sistema solicita un email válido sin pedir creación de cuenta o contraseña.
- Dado que el usuario ha introducido su email y completado el onboarding, Cuando vuelve a abrir la app en el futuro, Entonces accede directamente a la pantalla principal sin requerir autenticación.

### Historia 2: Detección y alerta en tiempo real (Prioridad: 1 - MVP)
**Como** padre o tutor, **Quiero** recibir un email automático con los detalles de una imagen sospechosa detectada en el WhatsApp de mi hijo, **Para** estar informado inmediatamente sobre su exposición a contenido peligroso.

**Criterios de Aceptación:**
- Dado que la monitorización en segundo plano está activa, Cuando se guarda una imagen nueva en las carpetas de WhatsApp y la API de OpenAI la clasifica como peligrosa, Entonces el sistema envía en tiempo real un correo electrónico al padre.
- Dado que se va a enviar la alerta por email, Cuando se genera el mensaje, Entonces este debe incluir obligatoriamente: una miniatura de la imagen, la categoría detectada (ej. violencia) y la fecha/hora exacta en la que se detectó el archivo en el dispositivo.

### Historia 3: Gestión del historial local (Prioridad: 2)
**Como** padre o tutor que revisa el dispositivo físicamente, **Quiero** poder ver un registro de las alertas generadas y tener la opción de vaciarlo, **Para** mantener la aplicación limpia y organizar las incidencias ya revisadas.

**Criterios de Aceptación:**
- Dado que se han detectado imágenes peligrosas en el pasado, Cuando el usuario entra a la pantalla principal de la app, Entonces puede ver un listado básico con el historial de estas alertas.
- Dado que el usuario está viendo el historial, Cuando pulsa el botón de "Eliminar alertas", Entonces se borra todo el registro local del dispositivo.

## Casos Borde y Escenarios de Error
- **¿Qué pasa si no hay conexión a internet cuando llega/se envía la imagen?** Al ser un MVP, la imagen no se procesará ni se encolará para más tarde; se considerará que no se puede analizar y no se enviará alerta.
- **¿Qué pasa si la API de OpenAI falla, devuelve un error o se excede el límite de peticiones?** El sistema aplicará un fallback de seguridad pasiva: considerará la imagen como "segura" (OK) para no generar falsas alarmas ni bloquear el flujo.
- **¿Qué pasa si se reciben vídeos, GIFs animados o stickers?** El sistema debe ignorarlos completamente, limitándose a procesar extensiones de imagen estáticas (JPG, PNG, etc.).
- **¿Qué pasa si el menor decide desinstalar la app o forzar su cierre?** En esta primera versión no existe protección contra desinstalación o cierres. La app simplemente dejará de monitorizar.

## Requisitos Clave (Must-Haves)
- El sistema **DEBE** monitorizar exclusivamente las rutas de almacenamiento que usa WhatsApp para guardar imágenes.
- El sistema **DEBE** enviar los datos a la API de OpenAI para su análisis asumiendo una sensibilidad alta en la detección de las diferentes categorías (todas activas por defecto).
- El sistema **DEBE** tener una UI transparente en el dispositivo, de manera que el menor sepa que la app está instalada.
- El sistema **NO DEBE** intentar identificar al remitente o destinatario de la imagen, limitándose a reportar la existencia del archivo en el dispositivo.
- El sistema **NO DEBE** interferir, alterar, bloquear ni borrar la imagen original de la galería del teléfono (Read-Only).

## Criterios de Éxito (El "Por Qué")
- Sabremos que esto es un éxito si el sistema es capaz de detectar una imagen, enviarla a la API, procesar la respuesta y mandar el email de alerta en menos de 10-15 segundos desde que la imagen entra al dispositivo (suponiendo una red estable).
- El objetivo de negocio es proveer a los tutores de una herramienta funcional, rápida de desarrollar, que les otorgue visibilidad del entorno digital visual del menor de forma no intrusiva.
