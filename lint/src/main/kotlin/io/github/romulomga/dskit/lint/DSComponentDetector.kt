package io.github.romulomga.dskit.lint

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.SourceCodeScanner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

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
    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(UCallExpression::class.java)

    // Pelo nome do código-fonte, não pelo `getApplicableMethodNames`: composable com parâmetro `Color` ou `Dp`
    // (value classes) sai compilado com o nome alterado (`Badge-eopBjH0`) e escaparia.
    override fun createUastHandler(context: JavaContext): UElementHandler = object : UElementHandler() {
        override fun visitCallExpression(node: UCallExpression) {
            val name = node.sourceName() ?: return
            val replacement = REPLACEMENTS[name] ?: return
            if (isExempt(node)) return
            val owner = node.resolve()?.containingClass?.qualifiedName.orEmpty()
            if (!owner.startsWith(MATERIAL3)) return

            context.report(ISSUE, node, context.getLocation(node), "`$name` do Material: use `$replacement`.")
        }
    }

    companion object {
        private const val MATERIAL3 = "androidx.compose.material3"

        // Só onde o DSKit tem um equivalente; o resto do Material segue livre até o DSKit cobrir.
        // O lint é um módulo JVM e não enxerga os componentes, então os nomes vão como texto.
        val REPLACEMENTS: Map<String, String> = mapOf(
            "MaterialTheme" to "DSTheme",
            "Scaffold" to "DSScreen",
            "Button" to "DSButton",
            "ElevatedButton" to "DSButton",
            "FilledTonalButton" to "DSTonalButton",
            "OutlinedButton" to "DSOutlinedButton",
            "TextButton" to "DSTextButton",
            "IconButton" to "DSIconButton",
            "FilledIconButton" to "DSIconButton",
            "FilledTonalIconButton" to "DSIconButton",
            "OutlinedIconButton" to "DSIconButton",
            "FloatingActionButton" to "DSFab",
            "SmallFloatingActionButton" to "DSFab",
            "LargeFloatingActionButton" to "DSFab",
            "ExtendedFloatingActionButton" to "DSExtendedFab",
            "Card" to "DSCard",
            "ElevatedCard" to "DSCard",
            "OutlinedCard" to "DSCard",
            "TextField" to "DSTextField",
            "OutlinedTextField" to "DSTextField",
            "ExposedDropdownMenuBox" to "DSDropdownField (ou DSAutocompleteField, se editável)",
            "Badge" to "DSBadge",
            "BadgedBox" to "DSBadgedBox",
            "TooltipBox" to "DSTooltip",
            "HorizontalDivider" to "DSDivider",
            "VerticalDivider" to "DSVerticalDivider",
            "Slider" to "DSSlider",
            "RangeSlider" to "DSRangeSlider",
            "SegmentedButton" to "DSSegmentedButton",
            "SingleChoiceSegmentedButtonRow" to "DSSegmentedButton",
            "MultiChoiceSegmentedButtonRow" to "DSMultiSegmentedButton",
            "FilterChip" to "DSFilterChip",
            "ElevatedFilterChip" to "DSFilterChip",
            "InputChip" to "DSInputChip",
            "AssistChip" to "DSAssistChip",
            "ElevatedAssistChip" to "DSAssistChip",
            "Checkbox" to "DSCheckbox",
            "TriStateCheckbox" to "DSCheckbox",
            "RadioButton" to "DSRadioListItem",
            "Switch" to "DSSwitchListItem",
            "ListItem" to "DSListItem",
            "TabRow" to "DSTabs",
            "PrimaryTabRow" to "DSTabs",
            "SecondaryTabRow" to "DSTabs",
            "ScrollableTabRow" to "DSTabs",
            "LinearProgressIndicator" to "DSLinearProgress",
            "Snackbar" to "DSSnackbarHost",
            "SnackbarHost" to "DSSnackbarHost",
            "TopAppBar" to "DSTopAppBar",
            "CenterAlignedTopAppBar" to "DSTopAppBar",
            "MediumTopAppBar" to "DSTopAppBar",
            "LargeTopAppBar" to "DSTopAppBar",
            "NavigationBar" to "DSNavigationBar",
            "NavigationRail" to "DSNavigationRail",
            "NavigationSuiteScaffold" to "DSNavigationScaffold",
            "AlertDialog" to "DSAlertDialog",
            "ModalBottomSheet" to "DSBottomSheet",
            "PullToRefreshBox" to "DSScreen(onRefresh = …)"
        )

        val ISSUE: Issue = Issue.create(
            id = "DSComponent",
            briefDescription = "Componente do Material com equivalente no DSKit",
            explanation = "O DSKit embrulha este componente do Material com os padrões do app: vibração, " +
                "acessibilidade, estados (carregando, destrutivo) e a API de uso. Chamar o do Material direto " +
                "espalha esses detalhes pelo app. Se o DSKit não cobre o caso, evolua o DSKit em vez de suprimir.",
            category = Category.CORRECTNESS,
            priority = 7,
            severity = Severity.ERROR,
            implementation = Implementation(DSComponentDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}
