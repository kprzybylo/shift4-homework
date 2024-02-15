import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.22"
}

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    testRuntimeOnly("org.junit.jupiter:junit-jupiter:5.10.1")
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = "17"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    jar {
        manifest {
            attributes(
                mapOf(
                    "Implementation-Title" to "Cron Expression Parser",
                    "Implementation-Version" to "v0.0.1",
                    "Main-Class" to "eu.AppKt",
                ),
            )
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })
    }
}
