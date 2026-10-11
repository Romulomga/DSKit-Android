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
            @JvmInline
            value class Dp(val value: Float)
            val Int.dp: Dp get() = Dp(toFloat())
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

    fun testSpacingOnTheScaleOffersTheToken() {
        lint().files(
            *stubs,
            kotlin(
                """
                package com.example.feature
                import androidx.compose.ui.Modifier
                import androidx.compose.ui.padding
                import androidx.compose.ui.unit.dp

                fun screen() {
                    val a = Modifier.padding(16.dp)
                    val b = Modifier.padding(all = 14.dp)
                }
                """
            ).indented()
        ).allowMissingSdk().testModes(TestMode.DEFAULT).run()
            .expectContains("16.dp é DSSpacing.lg")
            .expectContains("14.dp não está na escala (o mais perto é DSSpacing.md)")
            .expectFixDiffs(
                """
                Fix for src/com/example/feature/test.kt line 7: Trocar por DSSpacing.lg:
                @@ -2 +2
                + import io.github.romulomga.dskit.DSSpacing
                @@ -7 +8
                -     val a = Modifier.padding(16.dp)
                +     val a = Modifier.padding(DSSpacing.lg)
                """
            )
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
