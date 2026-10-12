plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    `maven-publish`
    id("io.github.takahirom.roborazzi")
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Grupo e versão das coordenadas Maven: num app que inclui o DSKit como submódulo (`includeBuild`), o Gradle troca
// `com.github.Romulomga.DSKit-Android:<módulo>` por este projeto.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

android {
    namespace = Configs.Modules.Components.namespace
    compileSdk = Configs.Common.compileSdk

    defaultConfig {
        minSdk = Configs.Common.minSdk
        consumerProguardFiles("consumer-rules.pro")
    }

    // O que sai no Maven: só o release, com o código-fonte (a IDE mostra o KDoc de quem usa).
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
    }
}

kotlin {
    jvmToolchain(Configs.Common.javaVersion)
    explicitApi()
}

composeCompiler {
    // Tipos de fora do módulo que o DSKit trata como estáveis (ver o arquivo).
    stabilityConfigurationFiles.add(rootProject.layout.projectDirectory.file("compose-stability.conf"))

    // Relatório de estabilidade e de pulos: `./gradlew assembleRelease -Pdskit.composeReports=true`,
    // saída em `build/compose_compiler/`.
    if (providers.gradleProperty("dskit.composeReports").orNull == "true") {
        reportsDestination.set(layout.buildDirectory.dir("compose_compiler"))
        metricsDestination.set(layout.buildDirectory.dir("compose_compiler"))
    }
}

dependencies {
    api(project(Dependencies.General.tokens))
    implementation(Dependencies.AndroidX.coreKtx)
    implementation(Dependencies.Compose.navigationSuite)
    api(Dependencies.Compose.uiToolingPreview)
    debugImplementation(Dependencies.Compose.uiTooling)

    // As regras de lint do DSKit viajam com o AAR e valem para quem usa a lib.
    lintPublish(project(Dependencies.General.lint)) { isTransitive = false }

    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.robolectric)
    testImplementation(Dependencies.Test.roborazzi)
    testImplementation(Dependencies.Test.roborazziCompose)
    testImplementation(Dependencies.Test.roborazziRule)
    testImplementation(Dependencies.Test.roborazziAccessibility)
    testImplementation(Dependencies.Compose.uiTestJunit4)
    debugImplementation(Dependencies.Compose.uiTestManifest)
}

// O artefato `com.github.Romulomga.DSKit-Android:components`: `./gradlew publishToMavenLocal` gera o AAR para testar fora do
// submódulo.
publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "components"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
