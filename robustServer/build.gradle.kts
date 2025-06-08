plugins {
    java
    id("jacoco")
    id("com.github.ben-manes.versions") version "0.48.0"
}

group = "com.robustGames"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.almasb.fxgl:fxgl:11.17")
    implementation("com.github.almasb.fxgl:fxgl-network:11.17")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.testfx:testfx-junit5:4.0.16-alpha")
    testImplementation("com.almasb:fxgl-test:11.17")
}

tasks.test {
    useJUnitPlatform()
}
