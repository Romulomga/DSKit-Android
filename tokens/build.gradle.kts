plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
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
    namespace = Configs.Modules.Tokens.namespace
    compileSdk = Configs.Common.compileSdk

    defaultConfig {
        minSdk = Configs.Common.minSdk
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
    api(platform(Dependencies.Compose.bom))
    api(Dependencies.Compose.ui)
    api(Dependencies.Compose.foundation)
    api(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.uiToolingPreview)
    debugImplementation(Dependencies.Compose.uiTooling)

    testImplementation(Dependencies.Test.junit)
}

// `com.github.Romulomga.DSKit-Android:tokens:<versão>` pelo JitPack, ou `./gradlew publishToMavenLocal` para testar.
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
