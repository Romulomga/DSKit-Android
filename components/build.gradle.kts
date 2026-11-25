plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
    alias(libs.plugins.roborazzi)
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Grupo e versão das coordenadas Maven: num app que inclui o DSKit como submódulo (`includeBuild`), o Gradle troca
// `com.github.Romulomga.DSKit-Android:<módulo>` por este projeto.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

android {
    namespace = "io.github.romulomga.dskit.components"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
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
        // "Há uma versão mais nova" depende do dia em que o CI roda: subir uma versão do catálogo é decisão de release.
        disable += listOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable")
    }
}

kotlin {
    jvmToolchain(21)
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
    api(project(":tokens"))
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.material3.adaptive.navigation.suite)
    api(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    // As regras de lint do DSKit viajam com o AAR e valem para quem usa a lib.
    lintPublish(project(":lint")) { isTransitive = false }

    testImplementation(libs.junit)
    testImplementation(project(":seed"))
    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.roborazzi.accessibility.check)
    testImplementation(libs.compose.ui.test.junit4)
    debugImplementation(libs.compose.ui.test.manifest)
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
