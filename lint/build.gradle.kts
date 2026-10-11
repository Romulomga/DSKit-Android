plugins {
    id("org.jetbrains.kotlin.jvm")
    id("com.android.lint")
}

apply {
    from("$rootDir/ktlint.gradle")
}

// Com o DSKit como submódulo, o app pega estas regras por `lintChecks("com.github.Romulomga.DSKit-Android:lint")`:
// o `lintPublish` do components só vale quando a lib chega como AAR.
group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

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
