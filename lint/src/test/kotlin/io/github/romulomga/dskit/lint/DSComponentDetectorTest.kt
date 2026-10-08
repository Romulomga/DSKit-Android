package io.github.romulomga.dskit.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class DSComponentDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = DSComponentDetector()

    override fun getIssues(): List<Issue> = listOf(DSComponentDetector.ISSUE)

    private val stubs: Array<TestFile> = arrayOf(
        kotlin(
            """
            package androidx.compose.material3
            fun Button(onClick: () -> Unit) {}
            fun TextField(value: String) {}
            fun Switch(checked: Boolean) {}
            fun MaterialTheme(content: () -> Unit) {}
            fun Scaffold() {}
            """
        ).indented(),
        kotlin(
            """
            package com.example.other
            fun Button(onClick: () -> Unit) {}
            """
        ).indented()
    )

    fun testMaterialComponentsWithADSKitEquivalentAreErrors() {
        lint().files(
            *stubs,
            kotlin(
                """
                package com.example.feature
                import androidx.compose.material3.Button
                import androidx.compose.material3.MaterialTheme
                import androidx.compose.material3.Switch
                import androidx.compose.material3.TextField

                fun screen() {
                    MaterialTheme {}
                    Button(onClick = {})
                    TextField("")
                    Switch(true)
                }
                """
            ).indented()
        ).allowMissingSdk().testModes(TestMode.DEFAULT).run()
            .expectErrorCount(4)
            .expectContains("Button do Material: use DSPrimaryButton.")
            .expectContains("TextField do Material: use DSTextField.")
    }

    fun testMaterialWithoutEquivalentAndOtherPackagesPass() {
        lint().files(
            *stubs,
            kotlin(
                """
                package com.example.feature
                import androidx.compose.material3.Scaffold
                import com.example.other.Button

                fun screen() {
                    Scaffold()
                    Button(onClick = {})
                }
                """
            ).indented()
        ).allowMissingSdk().testModes(TestMode.DEFAULT).run().expectClean()
    }

    fun testTheAppDesignSystemPackageIsExempt() {
        lint().files(
            *stubs,
            kotlin(
                """
                package com.example.designsystem
                import androidx.compose.material3.Button

                fun HomerButton() {
                    Button(onClick = {})
                }
                """
            ).indented()
        ).allowMissingSdk().testModes(TestMode.DEFAULT).run().expectClean()
    }
}
