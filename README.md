# H3-KMP library ![Last Version](https://img.shields.io/maven-central/v/io.github.phansier.h3/library)


A library to convert Uber's H3 geo-index to LatLng vertices and back for Kotlin Multiplatform: iOS and Android

## Experimental

- Android is ready to use, API may change
- iOS - Work in progress, need help/feedback for convenient publishing

## Instalation
> [!NOTE]
> Replace "0.0.2" with ![](https://img.shields.io/maven-central/v/io.github.phansier.h3/library?label=latest%20version)

### Gradle KMP

```kotlin
kotlin {
   sourceSets {
      commonMain.dependencies {
            implementation("io.github.phansier.h3:library:0.0.2")
            /*
            # or using version catalog:
            # libs.version.toml
            [versions]
            h3 = "0.0.2"
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
    implementation("io.github.phansier.h3:library:0.0.2")
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

### Inspired by

- [abc-kmm-h3](https://github.com/line/abc-kmm-h3) - not updated 4 years
- [h3-java](https://github.com/uber/h3-java) - [was not supported](https://github.com/uber/h3-java/issues/160) and easy to use in Android when works started
- [h3-go](https://github.com/uber/h3-go) - includes C lib as sources - same approach used here
