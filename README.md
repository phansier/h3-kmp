# H3-KMP library ![Last Version](https://img.shields.io/maven-central/v/io.github.phansier.h3/library)


A library to convert Uber's H3 geo-index to LatLng vertices and back for Kotlin Multiplatform: iOS, Android, and Wasm/JS (browser + Node).

## Experimental

- Android, iOS, wasmJs are ready to use, API may change
- wasmJs variant bridges to the [`h3-js`](https://www.npmjs.com/package/h3-js) npm package via JS interop (Kotlin/Wasm has no cinterop, so the in-tree C sources used for iOS/Android cannot be reused directly).

## Instalation
> [!NOTE]
> Replace "0.0.8" with ![](https://img.shields.io/maven-central/v/io.github.phansier.h3/library?label=latest%20version)

### Gradle KMP

```kotlin
kotlin {
   sourceSets {
      commonMain.dependencies {
            implementation("io.github.phansier.h3:library:0.0.8")
            /*
            # or using version catalog:
            # libs.version.toml
            [versions]
            h3 = "0.0.8"
            [libraries]
            h3 = { module = "io.github.phansier.h3:library", version.ref = "h3" }
            */
            // implementation(libs.h3)
      }
   }
}
```

### Gradle Android

```kotlin
dependencies {
    implementation("io.github.phansier.h3:library:0.0.8")
    // implementation(libs.h3)
}
```

## Usage
```kotlin
import com.beriukhov.h3.H3
import com.beriukhov.h3.LatLng as H3LatLng

// https://h3geo.org/#hex=084754a9ffffffff
val polygon: List<H3LatLng> = H3.vertices("084754a9ffffffff")
// res - Resolution, 0 <= res <= 15
val h3Index: String = geoToH3(H3LatLng(0.0, 0.0), res = 4).toHexString()
```

### Run Sample App

- Android: `open project in Android Studio and run the sample app`
- iOS: `open 'sample/iosApp/iosApp.xcodeproj' in Xcode and run the sample app`

### Publish to MavenLocal

1) Run `./gradlew :library:publishToMavenLocal`
2) Open `~/.m2/repository/io/github/phansier/h3/`

### Updating the H3 C sources

The H3 C library is vendored as sources under `androidLibrary/src/main/cpp/h3lib/`, shared by the Android
(NDK) and iOS (cinterop) targets. Current version:
[v4.2.1](https://github.com/uber/h3/releases/tag/v4.2.1).

To bump it, on macOS with `cmake` installed (it rebuilds the committed `cinterop/h3/<target>/libh3.a`):

```bash
echo v4.5.0 > H3_VERSION
make updateH3
```

`.github/workflows/update-h3.yml` checks monthly for a new H3 release and opens the bump PR by itself.

### Inspired by

- [abc-kmm-h3](https://github.com/line/abc-kmm-h3) - not updated 4 years
- [h3-java](https://github.com/uber/h3-java) - [was not supported](https://github.com/uber/h3-java/issues/160) and easy to use in Android when works started
- [h3-go](https://github.com/uber/h3-go) - includes C lib as sources - same approach used here
