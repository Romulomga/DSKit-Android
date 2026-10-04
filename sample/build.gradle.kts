plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
}

apply {
    from("$rootDir/ktlint.gradle")
}

android {
    namespace = Configs.Modules.Sample.namespace
    compileSdk = Configs.Common.compileSdk

    defaultConfig {
        applicationId = Configs.Modules.Sample.applicationId
        minSdk = Configs.Common.minSdk
        targetSdk = Configs.Common.targetSdk
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
    jvmToolchain(Configs.Common.javaVersion)
}

dependencies {
    implementation(project(Dependencies.General.components))
    implementation(Dependencies.AndroidX.activityCompose)
    implementation(Dependencies.Compose.uiToolingPreview)
    debugImplementation(Dependencies.Compose.uiTooling)
}
