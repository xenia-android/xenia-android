# xenia-android

Android port of [Xenia](https://github.com/xenia-project/xenia) — the Xbox 360 emulator research project.

> **Status:** Early prototype / work in progress.  
> Requires a device with **Vulkan 1.0** (Android 7.0 / API 24+, ARM64 or x86\_64).

[![Android CI](https://github.com/aarvsn/xenia-android/actions/workflows/ci.yml/badge.svg)](https://github.com/aarvsn/xenia-android/actions/workflows/ci.yml)

---

## What's included

Every subsystem from the upstream Xenia source tree is compiled into `libxenia-android.so`:

| Module | What it does |
|--------|-------------|
| `xenia/base` | Threading, memory, filesystem, logging, cvars (POSIX + Android overrides) |
| `xenia/cpu` | PowerPC JIT — HIR compiler, x64 backend, PPC frontend, register allocator |
| `xenia/gpu` | Xenos GPU emulation — command processor, shader translator, texture cache, render targets |
| `xenia/gpu/vulkan` | Vulkan graphics backend (pipeline cache, deferred command buffer, SPIRV shaders) |
| `xenia/gpu/null` | No-op GPU backend (headless / CI) |
| `xenia/apu` | Audio Processing Unit — XMA decoder, ring buffer, FFmpeg integration |
| `xenia/apu/nop` | Silent audio backend (default on Android until OpenSL/AAudio adapter lands) |
| `xenia/hid` | Controller input abstraction |
| `xenia/hid/nop` | No-op input backend (touch/gamepad forwarded via `MotionEvent` JNI) |
| `xenia/kernel` | Xbox 360 kernel HLE — `xboxkrnl.exe`, `xam.xex`, `xbdm.xex` |
| `xenia/vfs` | Virtual filesystem — GDFX disc images, STFS packages, host path |
| `xenia/ui` | Window/surface abstraction, imgui overlay, presenter |
| `xenia/ui/vulkan` | Vulkan instance/device management, VMA, immediate drawer |
| `xenia/emulator` | Top-level `Emulator` class wiring all subsystems together |
| `xenia/config` | TOML configuration file reader/writer |
| `xenia/memory` | Guest physical memory map (128 MB) |
| `xenia/app` | `xenia_main.cc` entry point, `EmulatorWindow` |

Third-party libraries compiled in: `snappy`, `aes_128`, `mspack` (LZX), `dxbc`, `xxhash`, `imgui`, `capstone`, `glslang-SPIRV`.

---

## Repository layout

```
xenia-android/
├── .github/workflows/ci.yml       # CI: build APK (debug+release) + lint
├── .gitmodules                     # xenia-upstream submodule → xenia-project/xenia
├── setup.sh                        # Symlink src/ and third_party/ from submodule
├── app/
│   ├── build.gradle               # AGP 8.2, NDK r26, CMake 3.22.1
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/xenia/android/
│       │   ├── XeniaRuntimeException.java
│       │   ├── emulator/
│       │   │   ├── WindowedAppActivity.java   # JNI bridge base (native methods)
│       │   │   ├── WindowSurfaceView.java
│       │   │   ├── EmulatorActivity.java      # "xenia" windowed app
│       │   │   └── GpuTraceViewerActivity.java
│       │   ├── ui/
│       │   │   ├── MainActivity.java          # Game library browser
│       │   │   ├── GameLibraryAdapter.java
│       │   │   └── SettingsActivity.java
│       │   └── utils/
│       │       ├── GameEntry.java
│       │       ├── GameLibraryManager.java    # SAF directory scanner
│       │       └── PathUtils.java
│       └── cpp/
│           ├── CMakeLists.txt                 # 438 lines — all xenia subsystems
│           └── jni/
│               ├── windowed_app_jni.cc        # Shim → AndroidWindowedAppContext
│               ├── xenia_jni_utils.cc
│               └── xenia_jni_utils.h
├── xenia-upstream/                 # git submodule (xenia-project/xenia)
│   ├── src/xenia/                  # All C++ source (symlinked as src/)
│   └── third_party/               # Vendored deps  (symlinked as third_party/)
├── src/     → xenia-upstream/src/        (symlink created by setup.sh)
└── third_party/ → xenia-upstream/third_party/  (symlink)
```

---

## Building

### Prerequisites

| Tool | Version |
|------|---------|
| Android Studio | Hedgehog 2023.1+ |
| Android NDK | r26 (`26.1.10909125`) |
| CMake | 3.22.1 |
| Gradle | 8.4 |
| JDK | 17 |

### Steps

```bash
# 1. Clone with submodule
git clone https://github.com/xenia-android/xenia-android.git
cd xenia-android
git submodule update --init --recursive --depth 1

# 2. Create src/ and third_party/ symlinks
bash setup.sh

# 3. Build
./gradlew assembleDebug

# APK → app/build/outputs/apk/debug/app-debug.apk
```

Or open in Android Studio and run on a Vulkan-capable device.

### ABI support

| ABI | Status |
|-----|--------|
| `arm64-v8a` | Primary |
| `x86_64` | Supported (emulator / dev boxes) |
| `x86` / `armeabi-v7a` | Not supported (Xenia requires 64-bit) |

---

## CI

GitHub Actions (`.github/workflows/ci.yml`) runs on every push/PR:

- **Build** (matrix: debug + release) — shallow-clones the submodule, runs `setup.sh`, builds APK with NDK r26
- **Lint** — Android lint without C++ compilation, faster parallel job

Artifacts: APK + ProGuard mapping retained 14 days.

---

## Runtime requirements

- Android 7.0+ (API 24), Vulkan 1.0
- ARM64 or x86\_64
- 3–4 GB RAM minimum
- Game files: `.iso` (GDFX disc), `.xex` (executable), `.stfs`/`.zar` (XBLA) — **legal dumps only**

---

## License

Code in this repository: BSD 2-Clause.  
Upstream Xenia source retains its original BSD license headers.
