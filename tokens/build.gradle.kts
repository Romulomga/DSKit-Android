plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    `maven-publish`
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Grupo e versão das coordenadas Maven: num app que inclui o DSKit como submódulo (`includeBuild`), o Gradle troca
// `com.github.Romulomga.DSKit-Android:<módulo>` por este projeto.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

android {
    namespace = "io.github.romulomga.dskit.tokens"
    compileSdk = 37

    // Como no seed e no components: é este módulo que chama APIs com nível mínimo (contraste do sistema, API 34;
    // cor dinâmica, API 31), e um aviso de lint aqui não pode passar batido.
    lint {
        warningsAsErrors = true
        abortOnError = true
        // "Há uma versão mais nova" depende do dia em que o CI roda: subir uma versão do catálogo é decisão de release.
        disable += listOf("AndroidGradlePluginVersion", "GradleDependency", "NewerVersionAvailable")
    }

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "DSKIT_VERSION", "\"${project.property("VERSION_NAME")}\"")
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
        buildConfig = true
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
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
    // `endorseStrictVersions` como o `platform("…")` de texto fazia: o .module publicado fica igual ao de antes do catálogo.
    api(platform(libs.compose.bom)) { endorseStrictVersions() }
    api(libs.compose.ui)
    api(libs.compose.foundation)
    api(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    testImplementation(libs.junit)
}

// O artefato `com.github.Romulomga.DSKit-Android:tokens`: `./gradlew publishToMavenLocal` gera o AAR para testar fora do
// submódulo.
publishing {
    publications {
        register<MavenPublication>("release") {
            artifactId = "tokens"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
