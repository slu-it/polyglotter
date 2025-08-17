import io.gitlab.arturbosch.detekt.getSupportedKotlinVersion

plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.spring") version "2.2.0"
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("io.kotest:kotest-bom:5.9.1")
        mavenBom("org.jetbrains.kotlin:kotlin-bom:2.2.0")
        mavenBom("org.jetbrains.kotlinx:kotlinx-coroutines-bom:1.10.2")
        mavenBom("org.springframework.ai:spring-ai-bom:1.0.1")
    }
    dependencies {
        dependency("io.gitlab.arturbosch.detekt:detekt-formatting:1.23.8")
        dependency("io.mockk:mockk-jvm:1.14.5")
        dependency("com.ninja-squad:springmockk:4.0.2")
    }
}

dependencies {
    implementation("org.springframework.ai:spring-ai-starter-model-openai")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("com.ninja-squad:springmockk")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-property")
    testImplementation("io.mockk:mockk-jvm")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting")
}

configurations.detekt {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") {
            useVersion(getSupportedKotlinVersion())
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

detekt {
    buildUponDefaultConfig = true
    config.setFrom("$projectDir/detekt.yml")
}

tasks {
    bootJar {
        archiveFileName = "application.jar"
    }
    withType<Test> {
        useJUnitPlatform()
    }
}
