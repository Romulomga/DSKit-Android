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
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.getContainingUFile

/**
 * Um detector para as sete regras de token. Código dentro do próprio DSKit ou de um pacote `designsystem`
 * do app fica de fora: é lá que os tokens viram componentes.
 */
class DSTokenDetector :
    Detector(),
    SourceCodeScanner {
    override fun getApplicableMethodNames(): List<String> = listOf(
        "Color", "padding", "spacedBy", "shadow", "tween", "spring", "CircularProgressIndicator", "TextStyle", "Text"
    )

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UQualifiedReferenceExpression::class.java)

    override fun visitMethodCall(context: JavaContext, node: UCallExpression, method: PsiMethod) {
        if (isExempt(node)) return
        val args = node.valueArguments.map { it.sourcePsi?.text?.trim().orEmpty() }
        when (method.name) {
            "Color" -> if (args.size == 1 && HEX_LITERAL.matches(args[0])) {
                report(context, RAW_COLOR, node, "Cor literal: use `DSTheme.colors` ou uma cor de marca do app.")
            }

            "padding", "spacedBy" -> if (args.any { DP_LITERAL.containsMatchIn(it) }) {
                report(context, SPACING_LITERAL, node, "Espaçamento literal: use `DSSpacing`.")
            }

            "shadow" -> if (args.any { DP_LITERAL.containsMatchIn(it) }) {
                report(context, SHADOW_LITERAL, node, "Sombra literal: use `Modifier.dsShadow(DSShadow.subtle | card | floating)`.")
            }

            "tween", "spring" -> if (isComposeAnimation(method) && args.isNotEmpty()) {
                report(context, ANIMATION_LITERAL, node, "Animação literal: use `DSMotion.short()`, `snappy()`, `spring()`…")
            }

            "CircularProgressIndicator" -> if (isMaterial(method) && args.isEmpty()) {
                report(context, PROGRESS_INDICATOR, node, "Indicador solto: use `DSLoadingState` ou um componente de carregamento do app.")
            }

            "TextStyle", "Text" -> if (args.any { SP_LITERAL.containsMatchIn(it) } || node.hasNamedFontArgument()) {
                report(context, FONT_LITERAL, node, "Fonte literal: use `DSTypography` (escala com a fonte do sistema).")
            }
        }
    }

    override fun createUastHandler(context: JavaContext) = object : com.android.tools.lint.client.api.UElementHandler() {
        override fun visitQualifiedReferenceExpression(node: UQualifiedReferenceExpression) {
            if (isExempt(node)) return
            val text = node.sourcePsi?.text ?: return
            when {
                RAW_COLOR_CONSTANT.matches(text) ->
                    report(context, RAW_COLOR, node, "Cor fixa `$text`: use `DSTheme.colors` (muda no modo escuro).")

                MATERIAL_ICON.matches(text) && node.uastParent !is UQualifiedReferenceExpression ->
                    report(context, MATERIAL_ICON_LITERAL, node, "Ícone solto: use o enum de ícones do app (ou `DSIcons`).")
            }
        }
    }

    private fun UCallExpression.hasNamedFontArgument(): Boolean {
        val text = sourcePsi?.text ?: return false
        return FONT_NAMED_ARG.containsMatchIn(text)
    }

    private fun isComposeAnimation(method: PsiMethod): Boolean =
        method.containingClass?.qualifiedName?.startsWith("androidx.compose.animation.core") == true

    private fun isMaterial(method: PsiMethod): Boolean =
        method.containingClass?.qualifiedName?.startsWith("androidx.compose.material") == true

    private fun report(context: JavaContext, issue: Issue, node: UElement, message: String) {
        context.report(issue, node, context.getLocation(node), message)
    }

    companion object {
        private val HEX_LITERAL = Regex("""0x[0-9A-Fa-f]{6,8}L?""")
        private val DP_LITERAL = Regex("""(?<![\w.])\d+(\.\d+)?f?\.dp\b""")
        private val SP_LITERAL = Regex("""(?<![\w.])\d+(\.\d+)?f?\.sp\b""")
        private val FONT_NAMED_ARG = Regex("""\bfontFamily\s*=\s*FontFamily\.""")
        private val RAW_COLOR_CONSTANT = Regex("""Color\.(White|Black|Gray|DarkGray|LightGray|Red|Green|Blue|Yellow)""")
        private val MATERIAL_ICON = Regex("""Icons\.(Default|Filled|Outlined|Rounded|Sharp|TwoTone|AutoMirrored)(\.\w+)*""")

        private val IMPLEMENTATION = Implementation(DSTokenDetector::class.java, Scope.JAVA_FILE_SCOPE)

        private fun issue(id: String, brief: String, explanation: String) = Issue.create(
            id = id,
            briefDescription = brief,
            explanation = explanation,
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )

        val RAW_COLOR = issue(
            "DSRawColor",
            "Cor fora dos tokens do DSKit",
            "Cores vêm de `DSTheme.colors` (claro e escuro) ou das cores de marca passadas ao `DSTheme`. Uma cor literal não muda no modo escuro."
        )
        val SPACING_LITERAL = issue(
            "DSSpacingLiteral",
            "Espaçamento literal",
            "Espaçamentos vêm da escala `DSSpacing` (xxs…xxl), para o app inteiro manter o mesmo ritmo."
        )
        val SHADOW_LITERAL = issue(
            "DSShadowLiteral",
            "Sombra literal",
            "Sombras vêm dos presets `DSShadow` aplicados com `Modifier.dsShadow`."
        )
        val ANIMATION_LITERAL = issue(
            "DSAnimationLiteral",
            "Animação literal",
            "Durações e molas vêm de `DSMotion`, e o componente respeita \"Remover animações\" (`DSTheme.reduceMotion`)."
        )
        val PROGRESS_INDICATOR = issue(
            "DSProgressIndicator",
            "Indicador de progresso solto",
            "Carregamento usa `DSLoadingState` ou um componente de carregamento do design system do app."
        )
        val FONT_LITERAL = issue(
            "DSFontLiteral",
            "Fonte fora dos tokens do DSKit",
            "Texto usa `DSTypography`, que escala com a fonte do sistema e aplica a família do app."
        )
        val MATERIAL_ICON_LITERAL = issue(
            "DSMaterialIcon",
            "Ícone do Material solto",
            "Ícones passam por um enum do app (ou `DSIcons`), para a mesma ação ter o mesmo ícone no app inteiro."
        )

        val ISSUES: List<Issue> = listOf(
            RAW_COLOR,
            SPACING_LITERAL,
            SHADOW_LITERAL,
            ANIMATION_LITERAL,
            PROGRESS_INDICATOR,
            FONT_LITERAL,
            MATERIAL_ICON_LITERAL
        )
    }
}

/** Código do próprio DSKit ou de um pacote `designsystem` do app: é lá que tokens e Material viram componentes. */
internal fun isExempt(node: UElement): Boolean {
    val pkg = node.getContainingUFile()?.packageName.orEmpty()
    return pkg.startsWith("io.github.romulomga.dskit") || pkg.split('.').any { it == "designsystem" }
}
