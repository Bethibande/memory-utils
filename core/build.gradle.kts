plugins {
    `java-library`
    `maven-publish`
    signing
}

group = "com.bethibande.memory"
version = "1.0"

repositories {
    mavenCentral()
}

tasks.test {
    useJUnitPlatform()
}

java {
    withSourcesJar()
    withJavadocJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                name = project.name
                description = project.description

                url = "https://github.com/Bethibande/memory-utils"

                licenses {
                    license {
                        name = "Apache 2"
                        url = "https://raw.githubusercontent.com/Bethibande/memory-utils/refs/heads/master/LICENSE"
                    }
                }

                developers {
                    developer {
                        id = "bethibande"
                        name = "Max Bethmann"
                        email = "contact@bethibande.com"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Bethibande/memory-utils.git"
                    developerConnection = "scm:git:ssh://github.com/Bethibande/memory-utils.git"
                    url = "https://github.com/Bethibande/memory-utils"
                }
            }
        }
    }

    repositories {
        maven {
            name = "Maven-Releases"
            url = uri("https://pckg.bethibande.com/repository/maven-releases/")
            credentials {
                username = providers.gradleProperty("mavenUsername").get()
                password = providers.gradleProperty("mavenPassword").get()
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}