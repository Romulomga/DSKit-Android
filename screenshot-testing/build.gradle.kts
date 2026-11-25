plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Módulo de teste: as capturas do DSKit (Roborazzi + Robolectric + o Accessibility Test Framework) para o app usar em
// `testImplementation`, como o `core:screenshot-testing` do Now in Android. Nunca entra no APK.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

android {
    namespace = "io.github.romulomga.dskit.screenshottesting"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
        // "Há uma versão mais nova" depende do dia em que o CI roda: subir uma versão do catálogo é decisão de release.
        disable += listOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable")
    }
}

kotlin {
    jvmToolchain(21)
    explicitApi()
}

composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("compose-stability.conf"))
}

dependencies {
    api(project(":components"))
    implementation(libs.androidx.activity.compose)

    // O que o teste do app escreve junto (`@Test`, `@Config`, `RoborazziOptions`, o `Matcher` das exceções do ATF).
    api(libs.junit)
    api(libs.robolectric)
    api(libs.roborazzi)
    api(libs.roborazzi.compose)
    api(libs.roborazzi.accessibility.check)
    api(libs.compose.ui.test.junit4)

    // A `ComponentActivity` vazia onde a captura desenha: o app põe a mesma linha no `build.gradle.kts` dele.
    debugImplementation(libs.compose.ui.test.manifest)
}

// O artefato `com.github.Romulomga.DSKit-Android:screenshot-testing`: `./gradlew publishToMavenLocal` gera o AAR para
// testar fora do submódulo.
publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "screenshot-testing"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
