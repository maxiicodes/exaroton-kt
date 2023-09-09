import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.github.hofmmaxi"
version = "1.0.0"

plugins {
    kotlin("jvm") version libs.versions.kotlin
    kotlin("plugin.serialization") version libs.versions.kotlin
    `java-library`
    `maven-publish`
    signing
}

kotlin {
    jvmToolchain(20)
}

java {
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.contentnegotiation)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.resources)
    implementation(libs.ktor.client.ws)
    implementation(libs.ktor.client.json)
    implementation(libs.kotlinx.collections)
    implementation(libs.ktor.client.resources)
    testImplementation(libs.kotlin.test)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "exaroton"
            from(components["java"])
            pom {
                name.set("Exaroton API")
                description.set("A wrapper for the exaroton api written in kotlin with ktor")
                url.set("https://github.com/hofmmaxi/exaroton-kt")
                licenses {
                    license {
                        name.set("")
                        url.set("")
                    }
                }
                developers {
                    developer {
                        id.set("hofmmaxi")
                        name.set("Maximilian Hofmann")
                        email.set("mh@wildemail.de")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/hofmmaxi/exaroton-kt.git")
                    developerConnection.set("scm:git:git@github.com:hofmmaxi/exaroton-kt.git")
                    url.set("https://github.com/hofmmaxi/exaroton-kt")
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use Kotlin Test framework
            useKotlinTest(libs.versions.kotlin)

            dependencies {
                // Use newer version of JUnit Engine for Kotlin Test
                implementation(libs.ktor.client.mock)
                implementation(libs.jutnit.jupiter.engine)
                implementation(libs.kotlinx.coroutines.test)
            }
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "20"
    targetCompatibility = "17"
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("Implementation-Title" to project.name, "Implementation-Version" to project.version))
    }
}

tasks.withType<Wrapper> {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.3"
}