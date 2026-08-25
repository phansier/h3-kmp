path := ./
params := --console=plain

# One libh3.a per Kotlin/Native target. A static archive records the platform its
# objects were built for, so a simulator archive cannot be linked into a device app.
# Directory names match the Kotlin target names, which build.gradle.kts feeds to
# cinterop as -libraryPath.
buildIosDotA:
	$(MAKE) iosDotA target=iosArm64            sysroot=iphoneos        arch=arm64
	$(MAKE) iosDotA target=iosSimulatorArm64   sysroot=iphonesimulator arch=arm64
	$(MAKE) iosDotA target=iosX64              sysroot=iphonesimulator arch=x86_64

# Matches the floor Kotlin/Native itself compiles iOS objects for. Without it CMake
# stamps the current SDK version, which makes the linker warn in any app with a
# lower deployment target.
iosMinVersion := 12.0

iosDotA:
	rm -rf build/$(target)
	cmake -S cinterop/h3/cmake -B build/$(target) \
		-DCMAKE_SYSTEM_NAME=iOS \
		-DCMAKE_OSX_SYSROOT=$(sysroot) \
		-DCMAKE_OSX_ARCHITECTURES=$(arch) \
		-DCMAKE_OSX_DEPLOYMENT_TARGET=$(iosMinVersion)
	cmake --build build/$(target)
	mkdir -p cinterop/h3/$(target)
	cp build/$(target)/libh3kmp.a cinterop/h3/$(target)/libh3.a

copyHeader:
	cp library/src/androidMain/cpp/h3lib/include/h3api.h cinterop/h3/headers/h3api.h


mavenCentralPublishAndroid:
	./gradlew :androidLibrary:publishAndReleaseToMavenCentral --no-configuration-cache

mavenCentralPublishLib:
	./gradlew :library:publishAndReleaseToMavenCentral --no-configuration-cache


mavenCentralPublish: mavenCentralPublishAndroid mavenCentralPublishLib

mavenLocalPublish:
	./gradlew publishToMavenLocal

runWasmSample:
	./gradlew :commonSample:wasmJsBrowserDevelopmentRun --continuous

buildWasm:
	./gradlew :commonSample:wasmJsBrowserDistribution $(params)