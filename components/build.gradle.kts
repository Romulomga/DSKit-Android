plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("io.github.takahirom.roborazzi")
}

apply {
    from("$rootDir/ktlint.gradle")
}

android {
    namespace = Configs.Modules.Components.namespace
    compileSdk = Configs.Common.compileSdk

    defaultConfig {
        minSdk = Configs.Common.minSdk
        consumerProguardFiles("consumer-rules.pro")
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

dependencies {
    api(project(Dependencies.General.tokens))
    implementation(Dependencies.AndroidX.coreKtx)
    api(Dependencies.Compose.uiToolingPreview)
    debugImplementation(Dependencies.Compose.uiTooling)

    // As regras de lint do DSKit viajam com o AAR e valem para quem usa a lib.
    lintPublish(project(Dependencies.General.lint)) { isTransitive = false }

    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Test.robolectric)
    testImplementation(Dependencies.Test.roborazzi)
    testImplementation(Dependencies.Test.roborazziCompose)
    testImplementation(Dependencies.Test.roborazziRule)
    testImplementation(Dependencies.Compose.uiTestJunit4)
    debugImplementation(Dependencies.Compose.uiTestManifest)
}
