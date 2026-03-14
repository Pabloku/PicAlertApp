# Spec: AI-Based Parental Monitoring of WhatsApp Images

## Overview
An Android application designed as a parental control tool that automatically monitors the images sent and received through WhatsApp folders, including private and group chats. It uses the OpenAI Moderation API with high sensitivity to analyze visual content. If inappropriate material is detected, such as violence or sexual content, the app does not block the image, but immediately sends an email alert to the guardian with the incident details based purely on file analysis.

## User Stories (The "What")

### Story 1: Onboarding and alert setup (Priority: 1 - MVP)
**As a** parent or guardian, **I want** to enter my email address the first time I open the app, **so that** I can receive safety alerts in my personal inbox.

**Acceptance Criteria (How we know it works):**
- Given that the user has just installed the app and opens it for the first time, when the start screen is shown, then the system requests a valid email without asking for account creation or a password.
- Given that the user has entered their email and completed onboarding, when they open the app again in the future, then they go directly to the main screen without authentication.

### Story 2: Real-time detection and alerting (Priority: 1 - MVP)
**As a** parent or guardian, **I want** to receive an automatic email with the details of a suspicious image detected in my child's WhatsApp, **so that** I am informed immediately about possible exposure to dangerous content.

**Acceptance Criteria:**
- Given that background monitoring is active, when a new image is saved in WhatsApp folders and the OpenAI API classifies it as dangerous, then the system sends an email to the parent in real time.
- Given that an alert email is about to be sent, when the message is generated, then it must include: an image thumbnail, the detected category such as violence, and the exact date and time when the file was detected on the device.

### Story 3: Local history management (Priority: 2)
**As a** parent or guardian reviewing the device in person, **I want** to see a record of generated alerts and have the option to clear it, **so that** I can keep the application clean and organize incidents that have already been reviewed.

**Acceptance Criteria:**
- Given that dangerous images have been detected in the past, when the user opens the app main screen, then they can see a basic list with the history of those alerts.
- Given that the user is viewing the history, when they tap the "Delete alerts" button, then the full local record is deleted from the device.

## Edge Cases and Error Scenarios
- **What happens if there is no internet connection when the image arrives or is sent?** Since this is an MVP, the image will not be processed or queued for later; it will be considered not analyzable and no alert will be sent.
- **What happens if the OpenAI API fails, returns an error, or exceeds the rate limit?** The system applies a passive safety fallback and treats the image as safe (OK) to avoid false alarms or blocking the flow.
- **What happens if videos, animated GIFs, or stickers are received?** The system must ignore them completely and only process static image extensions such as JPG and PNG.
- **What happens if the child uninstalls the app or force-stops it?** In this first version there is no uninstall or force-stop protection. The app simply stops monitoring.

## Key Requirements (Must-Haves)
- The system **MUST** monitor only the storage paths used by WhatsApp to save images.
- The system **MUST** send data to the OpenAI API for analysis using high sensitivity across the supported categories, with all categories enabled by default.
- The system **MUST** have a transparent UI presence on the device so the child knows the app is installed.
- The system **MUST NOT** try to identify the sender or recipient of the image, and must limit itself to reporting that the file exists on the device.
- The system **MUST NOT** interfere with, alter, block, or delete the original gallery image. Operation is read-only.

## Success Criteria (The "Why")
- We will consider this a success if the system can detect an image, send it to the API, process the response, and send the alert email within 10 to 15 seconds after the image reaches the device, assuming a stable network.
- The business goal is to provide guardians with a functional and fast-to-build tool that gives visibility into the child's visual digital environment in a non-intrusive way.
