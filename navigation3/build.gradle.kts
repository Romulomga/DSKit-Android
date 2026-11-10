plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
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
    namespace = Configs.Modules.Navigation3.namespace
    compileSdk = Configs.Common.compileSdk

    defaultConfig {
        minSdk = Configs.Common.minSdk
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
        // "Há uma versão mais nova" depende do dia em que o CI roda; a do Navigation 3 é a menor que os apps usam.
        disable += "GradleDependency"
    }
}

kotlin {
    jvmToolchain(Configs.Common.javaVersion)
    explicitApi()
}

composeCompiler {
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("compose-stability.conf"))
}

dependencies {
    api(project(Dependencies.General.components))
    api(Dependencies.AndroidX.navigation3Runtime)
    api(Dependencies.AndroidX.navigation3Ui)

    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.robolectric)
    testImplementation(Dependencies.Compose.uiTestJunit4)
    debugImplementation(Dependencies.Compose.uiTestManifest)
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
