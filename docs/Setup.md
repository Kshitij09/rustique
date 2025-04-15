# Setup

Project setup is primarily based on [Compiling Rust libraries for Android apps: a deep dive](https://gendignoux.com/blog/2022/10/24/rust-library-android.html) blog post

## Prerequisites

* Java 11 (min)
* Android SDK
* Android Studio
* Rust

## Rust tooling

### Install android targets

```bash
rustup target add \ 
    aarch64-linux-android \
    armv7-linux-androideabi \
    i686-linux-android \
    x86_64-linux-android \
```

### Update `.cargo/config.toml`

_Note: Paths shown below are example paths, kindly update them as per your environment_
```
[target.aarch64-linux-android]
linker = "${NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/aarch64-linux-android30-clang"

[target.armv7-linux-androideabi]
linker = "${NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/armv7a-linux-androideabi30-clang"

[target.i686-linux-android]
linker = "${NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/i686-linux-android30-clang"

[target.x86_64-linux-android]
linker = "${NDK_HOME}/toolchains/llvm/prebuilt/linux-x86_64/bin/x86_64-linux-android30-clang"
```

# Build

## Rust Library

Build rust library project for different android targets using the following commands
```bash
cargo build --target aarch64-linux-android --release
cargo build --target armv7-linux-androideabi --release
cargo build --target i686-linux-android --release
cargo build --target x86_64-linux-android --release
```

## Android App

You can use either of the following approaches

### Using Android Studio

Open project in the Android Studio and hit play button to deploy the app on your target device

### Using gradlew

You can use gradlew script to generate debug or releases apks
```bash
./gradlew :app:assembleDebug # debug
./gradlew :app:assembleRelease # release
```
This will generate apk files under `app/build/outputs/apk` directory