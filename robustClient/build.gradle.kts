plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
    id("org.jetbrains.kotlin.jvm") version "1.5.32"
}

group = "com.robustGames"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()

}

val junitVersion = "5.10.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.robustgames.robustclient")
    mainClass.set("com.robustgames.robustclient.HelloApplication")
}

javafx {
    version = "17.0.6"
    modules = listOf("javafx.controls", "javafx.graphics", "javafx.fxml", "javafx.media")
    }

dependencies {
    compile("com.github.almasb:fxgl:21.1")
    implementation("com.github.almasb:fxgl:17.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "app"
    }
}
