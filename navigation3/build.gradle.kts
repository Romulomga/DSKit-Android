plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Módulo opcional: os painéis inferiores como cena do Navigation 3. Fica fora do components para o app que não usa
// o Navigation 3 não receber a dependência.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

android {
    namespace = "io.github.romulomga.dskit.navigation3"
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
        // "Há uma versão mais nova" depende do dia em que o CI roda (a do Navigation 3 é a menor que os apps usam): subir
        // uma versão do catálogo é decisão de release.
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
    api(libs.androidx.navigation3.runtime)
    api(libs.androidx.navigation3.ui)

    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
}

// O artefato `com.github.Romulomga.DSKit-Android:navigation3`: `./gradlew publishToMavenLocal` gera o AAR para testar
// fora do submódulo.
publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "navigation3"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
