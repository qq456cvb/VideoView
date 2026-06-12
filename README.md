# VideoView

An Android **video-portal app** (tablet, landscape) built around an embedded **VLC** player: users log in, browse TV/radio channel lists, watch streams, download videos for offline viewing, and discuss them through a rich-text comment system.

This is a legacy multi-module Gradle project (compileSdk 22, circa 2015) kept as an archive; expect to update Gradle/SDK versions before it builds in a modern Android Studio.

## Modules

| Module | Role |
| --- | --- |
| `app/` | The main application: login (`LoginActivity`), the landscape main screen (`MainActivity`) composed of fragments — channel lists (TV / radio tabs, regional channels), the VLC-backed `VideoFragment`, comments, user profile (videos, images, reviews), and a background `DownloadService` for offline video. Networking via Volley and android-async-http. |
| `libvlc/` | The VLC-for-Android Java bindings and JNI sources (`libvlcjni*.c`), with the prebuilt `armeabi-v7a` native libraries (`libvlcjni.so`, iOMX variants) shipped under `app/src/main/jniLibs`. |
| `comment/` | Reusable comment-list fragment. |
| `richeditor/` | WebView-based rich text editor used for composing comments. |
| `sample/` | The comment-panel UI: editor dialogs, text mode, and picture/video/Word-document upload flows. |

## Build

1. Open the project in Android Studio (it predates AndroidX — Gradle, build tools 22.0.1 and the support-library dependencies will need bumping on a current setup).
2. Build and install the `app` module; the launcher activity is the login screen.
3. Playback targets ARMv7 devices only, since the bundled VLC `.so` binaries are `armeabi-v7a`.

Note that the app talks to a (now defunct) campus backend for accounts, channel metadata and comment storage, so it cannot be run end-to-end anymore; the code remains useful as a reference for embedding libVLC in an Android app and for the fragment-based tablet UI.

## Acknowledgement

The `libvlc` module is derived from the official [VLC for Android](https://code.videolan.org/videolan/vlc-android) project (LGPL); see the headers in `libvlc/src` for the original copyrights.
