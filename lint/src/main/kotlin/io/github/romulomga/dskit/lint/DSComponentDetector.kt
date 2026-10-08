package io.github.romulomga.dskit.lint

import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UCallExpression

/**
 * Componente do Material 3 usado onde o DSKit tem o seu. É o `DesignSystemDetector` do Now in Android
 * (e o "Material composable has a replacement" do Spark e do Orbit): o app que chama `Button` direto
 * sai do visual do DSKit sem perceber. Código do próprio DSKit ou de um pacote `designsystem` do app
 * fica de fora, porque é lá que o Material vira componente.
 *
 * O `CircularProgressIndicator` fica no `DSTokenDetector` (`DSProgressIndicator`), que já o cobria.
 */
class DSComponentDetector :
    Detector(),
    SourceCodeScanner {
    override fun getApplicableMethodNames(): List<String> = REPLACEMENTS.keys.toList()

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (isExempt(node)) return
        val owner = method.containingClass?.qualifiedName.orEmpty()
        if (!owner.startsWith(MATERIAL3)) return
        val replacement = REPLACEMENTS[method.name] ?: return
        context.report(
            ISSUE,
            node,
            context.getLocation(node),
            "`${method.name}` do Material: use `$replacement`."
        )
    }

    companion object {
        private const val MATERIAL3 = "androidx.compose.material3"

        // Só onde o DSKit tem um equivalente; o resto do Material segue livre até o DSKit cobrir.
        // O lint é um módulo JVM e não enxerga os componentes, então os nomes vão como texto.
        val REPLACEMENTS: Map<String, String> = mapOf(
            "MaterialTheme" to "DSTheme",
            "Button" to "DSPrimaryButton",
            "ElevatedButton" to "DSPrimaryButton",
            "FilledTonalButton" to "DSSecondaryButton",
            "OutlinedButton" to "DSSecondaryButton",
            "IconButton" to "DSIconButton",
            "FilledIconButton" to "DSIconButton",
            "FilledTonalIconButton" to "DSIconButton",
            "OutlinedIconButton" to "DSIconButton",
            "TextField" to "DSTextField",
            "OutlinedTextField" to "DSTextField",
            "FilterChip" to "DSOptionChip",
            "ElevatedFilterChip" to "DSOptionChip",
            "InputChip" to "DSChip",
            "Switch" to "DSToggleRow",
            "RangeSlider" to "DSRangeSlider",
            "SegmentedButton" to "DSSegmentedPicker",
            "SingleChoiceSegmentedButtonRow" to "DSSegmentedPicker",
            "TabRow" to "DSPagedTabs",
            "PrimaryTabRow" to "DSPagedTabs",
            "SecondaryTabRow" to "DSPagedTabs",
            "ScrollableTabRow" to "DSPagedTabs",
            "LinearProgressIndicator" to "DSProgressBar",
            "Card" to "DSCard",
            "ElevatedCard" to "DSCard",
            "OutlinedCard" to "DSCard",
            "Snackbar" to "DSToast"
        )

        val ISSUE: Issue = Issue.create(
            id = "DSComponent",
            briefDescription = "Componente do Material com equivalente no DSKit",
            explanation = "O DSKit tem o próprio componente para isto, com os tokens, o modo escuro, o TalkBack " +
                "e a fonte grande já resolvidos. Usar o do Material direto tira a tela da linguagem visual do app. " +
                "Se o DSKit não cobre o caso, evolua o DSKit em vez de suprimir.",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(DSComponentDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
