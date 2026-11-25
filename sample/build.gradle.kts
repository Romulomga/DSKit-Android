plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

apply {
    from("$rootDir/ktlint.gradle")
}

android {
    namespace = "io.github.romulomga.dskit.sample"
    compileSdk = 37

    defaultConfig {
        applicationId = "io.github.romulomga.dskit.sample"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = project.property("VERSION_NAME").toString()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        compose = true
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":components"))
    implementation(project(":seed"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)
}
