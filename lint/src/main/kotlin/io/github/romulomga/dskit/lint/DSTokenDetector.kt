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
    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UCallExpression::class.java, UQualifiedReferenceExpression::class.java)

    /**
     * Chamadas pelo nome do código-fonte: `padding(16.dp)`, `Text(color = …)` e outras funções com parâmetro `Dp`
     * ou `Color` (value classes) saem compiladas com o nome alterado (`padding-3ABfNKs`), e o
     * `getApplicableMethodNames` do lint não as acharia em código de verdade.
     */
    private fun visitCall(context: JavaContext, node: UCallExpression) {
        val name = node.sourceName() ?: return
        if (name !in CALL_NAMES || isExempt(node)) return
        val method = node.resolve() ?: return
        val args = node.valueArguments.map { it.sourcePsi?.text?.trim().orEmpty() }

        when (name) {
            "Color" -> if (args.size == 1 && HEX_LITERAL.matches(args[0])) {
                report(context, RAW_COLOR, node, "Cor literal: use `DSTheme.colors` ou uma cor de marca do app.")
            }

            "padding", "spacedBy" -> if (args.any { DP_LITERAL.containsMatchIn(it) }) {
                reportSpacing(context, node)
            }

            "shadow" -> if (args.any { DP_LITERAL.containsMatchIn(it) }) {
                report(
                    context,
                    SHADOW_LITERAL,
                    node,
                    "Sombra literal: use a elevação dos componentes do Material (`DSCard`, `DSFab`, menus)."
                )
            }

            "tween", "spring" -> if (isComposeAnimation(method) && args.isNotEmpty()) {
                reportAnimation(context, node, name, args)
            }

            "CircularProgressIndicator" -> if (isMaterial(method) && args.isEmpty()) {
                report(
                    context,
                    PROGRESS_INDICATOR,
                    node,
                    "Indicador solto: use `DSLoadingState`, `DSLoadingOverlay` ou `Modifier.dsSkeleton`."
                )
            }

            "TextStyle", "Text" -> if (args.any { SP_LITERAL.containsMatchIn(it) } || node.hasNamedFontArgument()) {
                report(context, FONT_LITERAL, node, "Fonte literal: use `DSTheme.typography` (escala com a fonte do sistema).")
            }
        }
    }

    override fun createUastHandler(context: JavaContext) = object : com.android.tools.lint.client.api.UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) = visitCall(context, node)

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

    /**
     * Diz o token de cada `N.dp` e, quando o valor é um passo da escala, oferece a troca (o "quick fix" do
     * Android Studio). Fora da escala, sugere o passo mais próximo, sem troca automática: o layout mudaria.
     */
    private fun reportSpacing(context: JavaContext, node: UCallExpression) {
        val literals = node.valueArguments.mapNotNull { arg ->
            val text = arg.sourcePsi?.text?.trim() ?: return@mapNotNull null
            val value = DP_VALUE.find(text)?.groupValues?.get(1)?.toFloatOrNull() ?: return@mapNotNull null
            Triple(arg, text, value)
        }

        val exact = literals.mapNotNull { (arg, text, value) -> SPACING_TOKENS[value]?.let { Triple(arg, text, it) } }
        val fix = if (exact.isEmpty()) {
            null
        } else {
            val fixes = exact.map { (arg, text, token) ->
                fix()
                    .name("Trocar por DSSpacing.$token")
                    .replace()
                    .range(context.getLocation(arg))
                    .text(text)
                    .with("io.github.romulomga.dskit.DSSpacing.$token")
                    .shortenNames()
                    .build()
            }
            if (fixes.size == 1) fixes.single() else fix().name("Trocar pelos tokens de DSSpacing").composite(*fixes.toTypedArray())
        }

        val hint = literals.joinToString { (_, text, value) ->
            SPACING_TOKENS[value]?.let { "`$text` é `DSSpacing.$it`" }
                ?: "`$text` não está na escala (o mais perto é `DSSpacing.${nearestSpacing(value)}`)"
        }

        context.report(SPACING_LITERAL, node, context.getLocation(node), "Espaçamento literal: $hint.", fix)
    }

    /** `tween(150)`, `tween(300)` e `tween(500)` sem curva viram `DSMotion.short()`, `medium()` e `long()`. */
    private fun reportAnimation(context: JavaContext, node: UCallExpression, name: String, args: List<String>) {
        val duration = args.singleOrNull()?.removePrefix("durationMillis")?.trimStart(' ', '=')?.toIntOrNull()
        val token = if (name == "tween") MOTION_TOKENS[duration] else null

        val fix = token?.let {
            fix()
                .name("Trocar por DSMotion.$it()")
                .replace()
                .all()
                .with("io.github.romulomga.dskit.DSMotion.$it()")
                .shortenNames()
                .build()
        }
        val message = if (token != null) {
            "Animação literal: `tween($duration)` é `DSMotion.$token()`."
        } else {
            "Animação literal: use `DSMotion.short()` (150 ms), `medium()` (300 ms) ou `long()` (500 ms)."
        }

        context.report(ANIMATION_LITERAL, node, context.getLocation(node), message, fix)
    }

    private fun nearestSpacing(value: Float): String = SPACING_TOKENS.minBy { (dp, _) -> kotlin.math.abs(dp - value) }.value

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
        private val CALL_NAMES =
            setOf("Color", "padding", "spacedBy", "shadow", "tween", "spring", "CircularProgressIndicator", "TextStyle", "Text")

        private val HEX_LITERAL = Regex("""0x[0-9A-Fa-f]{6,8}L?""")
        private val DP_LITERAL = Regex("""(?<![\w.])\d+(\.\d+)?f?\.dp\b""")
        private val DP_VALUE = Regex("""^(?:\w+\s*=\s*)?(\d+(?:\.\d+)?)f?\.dp$""")

        /** A escala de `DSSpacing` (módulo `tokens`); mude junto quando a escala mudar. */
        private val SPACING_TOKENS = mapOf(2f to "xxs", 4f to "xs", 8f to "sm", 12f to "md", 16f to "lg", 24f to "xl", 32f to "xxl")

        /** As durações de `DSMotion` (módulo `tokens`). */
        private val MOTION_TOKENS = mapOf(150 to "short", 300 to "medium", 500 to "long")

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
            "Cores vêm de `DSTheme.colors` e `DSTheme.status` (claro, escuro e Material You). Uma cor literal não muda com o tema."
        )
        val SPACING_LITERAL = issue(
            "DSSpacingLiteral",
            "Espaçamento literal",
            "Espaçamentos vêm da escala `DSSpacing` (xxs…xxl), para o app inteiro manter o mesmo ritmo."
        )
        val SHADOW_LITERAL = issue(
            "DSShadowLiteral",
            "Sombra literal",
            "Sombra vem da elevação dos componentes do Material, que muda o tom da superfície no tema escuro."
        )
        val ANIMATION_LITERAL = issue(
            "DSAnimationLiteral",
            "Animação literal",
            "Durações e curvas vêm de `DSMotion`, a especificação de movimento do Material 3."
        )
        val PROGRESS_INDICATOR = issue(
            "DSProgressIndicator",
            "Indicador de progresso solto",
            "Carregamento usa `DSLoadingState`, `DSLoadingOverlay` ou o esqueleto do DSKit."
        )
        val FONT_LITERAL = issue(
            "DSFontLiteral",
            "Fonte fora dos tokens do DSKit",
            "Texto usa `DSTheme.typography`, a escala do Material com a família do app, que escala com a fonte do sistema."
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

/** O nome da função como está no código-fonte, sem o sufixo que o Kotlin põe nas que recebem value class. */
internal fun UCallExpression.sourceName(): String? = (methodIdentifier?.name ?: methodName)?.substringBefore('-')

/** Código do próprio DSKit ou de um pacote `designsystem` do app: é lá que tokens e Material viram componentes. */
internal fun isExempt(node: UElement): Boolean {
    val pkg = node.getContainingUFile()?.packageName.orEmpty()
    return pkg.startsWith("io.github.romulomga.dskit") || pkg.split('.').any { it == "designsystem" }
}
