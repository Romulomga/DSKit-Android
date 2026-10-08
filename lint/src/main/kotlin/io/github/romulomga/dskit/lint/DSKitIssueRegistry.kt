package io.github.romulomga.dskit.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.android.tools.lint.detector.api.Issue

/**
 * Regras que viajam com o AAR do DSKit (`lintPublish`): as mesmas `ds_*` do SwiftLint do app iOS, para que
 * layout fora do design system do app use só tokens e componentes.
 */
class DSKitIssueRegistry : IssueRegistry() {
    override val issues: List<Issue> = DSTokenDetector.ISSUES + DSComponentDetector.ISSUE

    override val api: Int = CURRENT_API

    override val minApi: Int = 14

    override val vendor: Vendor = Vendor(
        vendorName = "DSKit Android",
        identifier = "io.github.romulomga.dskit",
        feedbackUrl = "https://github.com/Romulomga/DSKit-Android/issues"
    )
}
