path := ./
params := --console=plain

buildIosDotA:
	cmake -S library/src/androidMain -B buildIos \
		-DCMAKE_SYSTEM_NAME=iOS \
		-DCMAKE_OSX_SYSROOT=iphonesimulator \
		-DCMAKE_OSX_ARCHITECTURES=arm64
	cmake --build buildIos
	cp buildIos/libh3kmp.a cinterop/h3/libh3.a

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
