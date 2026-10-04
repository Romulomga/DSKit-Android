plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.android.lint")
}

apply {
    from("$rootDir/ktlint.gradle")
}

kotlin {
    jvmToolchain(Configs.Common.javaVersion)
}

dependencies {
    compileOnly(Dependencies.Lint.api)
    compileOnly(Dependencies.Lint.checks)

    testImplementation(Dependencies.Test.junit)
    testImplementation(Dependencies.Lint.api)
    testImplementation(Dependencies.Lint.tests)
}
