package io.github.romulomga.dskit.lint

import com.android.tools.lint.checks.infrastructure.LintDetectorTest
import com.android.tools.lint.checks.infrastructure.TestFile
import com.android.tools.lint.checks.infrastructure.TestMode
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Issue

class DSTokenDetectorTest : LintDetectorTest() {
    override fun getDetector(): Detector = DSTokenDetector()

    override fun getIssues(): List<Issue> = DSTokenDetector.ISSUES

    private val stubs: Array<TestFile> = arrayOf(
        kotlin(
            """
            package androidx.compose.ui.unit
            class Dp
            val Int.dp: Dp get() = Dp()
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.ui.graphics
            @JvmInline
            value class Color(val value: ULong) {
                companion object { val White = Color(0UL); val Black = Color(0UL) }
            }
            fun Color(color: Long): Color = Color(color.toULong())
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.ui
            import androidx.compose.ui.unit.Dp
            interface Modifier { companion object : Modifier }
            fun Modifier.padding(all: Dp): Modifier = this
            """
        ).indented(),
        kotlin(
            """
            package androidx.compose.material3
            fun CircularProgressIndicator() {}
            """
        ).indented()
    )

    fun testLiteralsOutsideTheDesignSystemAreReported() {
        lint().files(
            *stubs,
            kotlin(
                """
                package com.example.feature
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.padding
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.graphics.Color
                import androidx.compose.material3.CircularProgressIndicator

                fun screen() {
                    val m = Modifier.padding(16.dp)
                    val c = Color(0xFFD0212E)
                    val w = Color.White
                    CircularProgressIndicator()
                }
                """
            ).indented()
        ).allowMissingSdk().testModes(TestMode.DEFAULT).run().expectWarningCount(4)
    }

    fun testTheAppDesignSystemPackageIsExempt() {
        lint().files(
            *stubs,
            kotlin(
                """
                package com.example.designsystem
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.padding
                import androidx.compose.ui.unit.dp
                import androidx.compose.ui.graphics.Color

                fun card() {
                    val m = Modifier.padding(16.dp)
                    val c = Color(0xFFD0212E)
                }
                """
            ).indented()
        ).allowMissingSdk().testModes(TestMode.DEFAULT).run().expectClean()
    }
}
