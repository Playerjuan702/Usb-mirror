# USB Mirror Prototype — diagnostic starter

This is a buildable Android/Kotlin diagnostic starter, not a completed screen-mirroring app. It checks USB host/accessory feature flags and reports USB peripheral interfaces visible to Android, and it can request screen-capture permission.

## Build

Requires JDK 17, Android SDK platform 35, build-tools 35.0.0, and Gradle 8.11.1.

```bash
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`.

## Phone-only cloud build

Upload this folder as a GitHub repository, then open **Actions → Build diagnostic APK → Run workflow**. Download the `usb-mirror-diagnostic-apk` artifact from the completed run and extract `app-debug.apk`.

## Important limitation

This diagnostic app does not transmit or display video. Ordinary Android phones do not universally expose a generic app-accessible USB endpoint to another phone over a USB-C cable. The transport must be confirmed on the actual receiver before a reliable USB-only video implementation can be completed. Screen capture permission alone is not screen streaming.
