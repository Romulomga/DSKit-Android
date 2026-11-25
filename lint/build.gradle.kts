plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.android.lint)
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Com o DSKit como submódulo, o app pega estas regras por `lintChecks("com.github.Romulomga.DSKit-Android:lint")`:
// o `lintPublish` do components só vale quando a lib chega como AAR.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.lint.api)
    compileOnly(libs.lint.checks)

    testImplementation(libs.junit)
    testImplementation(libs.lint.api)
    testImplementation(libs.lint.tests)
}
