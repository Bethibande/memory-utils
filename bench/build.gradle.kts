plugins {
    java
}

group = "com.bethibande.memory"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(projects.core)

    implementation("org.openjdk.jmh:jmh-core:1.37")
    annotationProcessor("org.openjdk.jmh:jmh-generator-annprocess:1.37")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.test {
    useJUnitPlatform()
}
