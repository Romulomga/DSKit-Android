plugins {
    alias(libs.plugins.android.library)
    `maven-publish`
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Módulo opcional: gera o DSBrand a partir de uma cor. Fica fora do components porque traz uma dependência de fora
// do AndroidX (o algoritmo de cor do Material, ver docs/adr/0007); quem não usa não a recebe.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

android {
    namespace = "io.github.romulomga.dskit.seed"
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

dependencies {
    api(project(":tokens"))
    implementation(libs.material.color.utilities)

    testImplementation(libs.junit)
}

// O artefato `com.github.Romulomga.DSKit-Android:seed`: `./gradlew publishToMavenLocal` gera o AAR para testar fora do
// submódulo.
publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "seed"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
