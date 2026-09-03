# MOTIONIQ — Android Movement Intelligence

> **Understand your movement. Not just your steps.**

MOTIONIQ is a modern Android movement intelligence application built with Kotlin, Jetpack Compose, and Material 3. It intelligently leverages smartphone sensors, location services, and health integrations to understand physical movement, track routes, and discover walking and running routes.

---

## Production Release Specifications

| Property | Value |
| :--- | :--- |
| **Application Name** | **MOTIONIQ** |
| **Application ID / Package** | `com.motioniq.app` |
| **Current Release Version** | `1.0.0` |
| **Base Version Code** | `1` |
| **Platform** | Android (minSdk 29, targetSdk 36) |
| **Framework** | Kotlin, Jetpack Compose, Material 3, Navigation 3 |
| **Build Type** | `release` (Release-signed APK only) |
| **Artifact Naming** | `MOTIONIQ-v<versionName>-<versionCode>.apk` |

---

## CI/CD & Automated Release Architecture

This repository uses an automated, enterprise-grade GitHub Actions release pipeline (`.github/workflows/android-release.yml`).

### How Releases Work

1. **Trigger**:
   - Automated: Any push to the `main` branch.
   - Manual: Via `workflow_dispatch` in the GitHub Actions UI (with optional custom version name).

2. **Monotonic Version Code Determination**:
   - The workflow queries existing GitHub Releases for the highest released `versionCode`.
   - The next release is assigned `MAX(highest_code + 1, baseVersionCode)`.
   - **Failure Safety**: If compilation, signing, or verification fails at any stage, no GitHub release is created. This ensures failed builds **never consume or skip version codes**.
   - **Concurrency Protection**: Release jobs are synchronized using GitHub Actions concurrency groups (`release-pipeline`) to prevent race conditions.

3. **Release Build & Quality Gates**:
   - Builds exclusively using `./gradlew assembleRelease`.
   - Never builds or packages `assembleDebug` artifacts.
   - Verifies the APK exists, has valid file size (>1 MB), and confirms zero debug APKs exist.
   - Verifies the signature using `apksigner` / `jarsigner`.
   - Inspects `aapt` metadata to confirm package identity (`com.motioniq.app`), version code, and version name.

4. **Where APK Releases Appear**:
   - Published directly to the repository's [Releases](https://github.com/Pradeep1234a/google-Fit/releases) section.
   - Marked as `--latest` with complete changelog and build metadata.
   - Production APK is attached directly for user download.

---

## Required GitHub Actions Secrets

To enable production release signing, configure the following encrypted repository secrets in **GitHub → Settings → Secrets and variables → Actions**:

| Secret Name | Description | Example / Format |
| :--- | :--- | :--- |
| `ANDROID_KEYSTORE_BASE64` | Base64-encoded production keystore file (`.jks` / `.keystore`) | Base64 string |
| `ANDROID_KEYSTORE_PASSWORD` | Password for the release keystore | Plain text secret |
| `ANDROID_KEY_ALIAS` | Key alias in the release keystore | e.g. `motioniq` |
| `ANDROID_KEY_PASSWORD` | Password for the private key alias | Plain text secret |

> **IMPORTANT**: Keystore files and passwords must **never** be committed to Git. The CI environment securely decodes the keystore into runner temp storage during the build and wipes it immediately afterward.

---

## Creating a Production Keystore

If you do not yet have a production keystore, generate one using `keytool`:

```bash
# Generate the keystore
keytool -genkeypair -v \
  -keystore motioniq-release.jks \
  -alias motioniq \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000 \
  -storetype JKS
```

Encode the keystore to Base64 for the `ANDROID_KEYSTORE_BASE64` secret:

```bash
# On Linux / macOS:
base64 -w 0 motioniq-release.jks > keystore_base64.txt

# On Windows (PowerShell):
[Convert]::ToBase64String([IO.File]::ReadAllBytes("motioniq-release.jks")) | Set-Content "keystore_base64.txt"
```

Copy the content of `keystore_base64.txt` into the GitHub Secret `ANDROID_KEYSTORE_BASE64`.

---

## Local Development & Testing

```bash
# Clean project
./gradlew clean

# Build release APK locally (without signing, or with local signing properties)
./gradlew assembleRelease

# Run unit tests
./gradlew testDebugUnitTest
```
