object Dependencies {
    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val activityCompose = "androidx.activity:activity-compose:${Versions.activityCompose}"
    }

    object Compose {
        const val bom = "androidx.compose:compose-bom:${Versions.composeBom}"
        const val ui = "androidx.compose.ui:ui"
        const val foundation = "androidx.compose.foundation:foundation"
        const val material3 = "androidx.compose.material3:material3"
        const val navigationSuite = "androidx.compose.material3:material3-adaptive-navigation-suite"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
    }

    object Libraries {
        const val materialColorUtilities = "com.materialkolor:material-color-utilities:${Versions.materialColorUtilities}"
    }

    object Lint {
        const val api = "com.android.tools.lint:lint-api:${Versions.lint}"
        const val checks = "com.android.tools.lint:lint-checks:${Versions.lint}"
        const val tests = "com.android.tools.lint:lint-tests:${Versions.lint}"
    }

    object Test {
        const val junit = "junit:junit:${Versions.junit}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
        const val roborazzi = "io.github.takahirom.roborazzi:roborazzi:${Versions.roborazzi}"
        const val roborazziCompose = "io.github.takahirom.roborazzi:roborazzi-compose:${Versions.roborazzi}"
        const val roborazziRule = "io.github.takahirom.roborazzi:roborazzi-junit-rule:${Versions.roborazzi}"
        const val roborazziAccessibility = "io.github.takahirom.roborazzi:roborazzi-accessibility-check:${Versions.roborazzi}"
    }

    object Ktlint {
        const val cli = "com.pinterest.ktlint:ktlint-cli:${Versions.ktlint}"
    }

    object General {
        const val tokens = Configs.Modules.Tokens.name
        const val components = Configs.Modules.Components.name
        const val seed = Configs.Modules.Seed.name
        const val lint = Configs.Modules.Lint.name
    }
}
