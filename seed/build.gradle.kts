plugins {
    id("com.android.library")
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
    namespace = Configs.Modules.Seed.namespace
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

    lint {
        warningsAsErrors = true
        abortOnError = true
    }
}

kotlin {
    jvmToolchain(Configs.Common.javaVersion)
    explicitApi()
}

dependencies {
    api(project(Dependencies.General.tokens))
    implementation(Dependencies.Libraries.materialColorUtilities)

    testImplementation(Dependencies.Test.junit)
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
