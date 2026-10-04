plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
}

apply {
    from("$rootDir/ktlint.gradle")
}

android {
    namespace = Configs.Modules.Tokens.namespace
    compileSdk = Configs.Common.compileSdk

    defaultConfig {
        minSdk = Configs.Common.minSdk
        buildConfigField("String", "DSKIT_VERSION", "\"${project.property("VERSION_NAME")}\"")
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

dependencies {
    api(platform(Dependencies.Compose.bom))
    api(Dependencies.Compose.ui)
    api(Dependencies.Compose.foundation)
    api(Dependencies.Compose.material3)
    implementation(Dependencies.Compose.uiToolingPreview)
    debugImplementation(Dependencies.Compose.uiTooling)

    testImplementation(Dependencies.Test.junit)
}
