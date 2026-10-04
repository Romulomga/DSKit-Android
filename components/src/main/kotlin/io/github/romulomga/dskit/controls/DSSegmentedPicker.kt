package io.github.romulomga.dskit

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** Uma opção de um `DSSegmentedPicker`: o valor e o rótulo que aparece no segmento. */
@Immutable
public data class DSSegmentedOption<T>(val value: T, val label: String)

/** Como o `DSSegmentedPicker` é desenhado. */
public enum class DSSegmentedStyle {
    /** O controle segmentado nativo (os `SegmentedButton` do Material 3), na cor primária. */
    System,

    /** Pílula: trilho suave com a opção selecionada numa cápsula na cor primária, que desliza entre as opções. */
    Filled
}

/**
 * Seletor segmentado com legenda opcional: o controle nativo (`System`) ou uma pílula cuja opção
 * selecionada fica numa cápsula na cor primária (`Filled`).
 *
 * Use para trocar entre duas a quatro visões ou modos da mesma tela. No `Filled` a cápsula desliza até a
 * opção tocada; com "Remover animações" ela troca por um crossfade. Cada opção é uma aba para o TalkBack,
 * com o estado "selecionado".
 *
 * @param selection valor da opção selecionada.
 * @param onSelectionChange chamado com o valor da opção tocada.
 * @param options as opções, na ordem em que aparecem.
 * @param title legenda opcional acima do controle.
 * @param style `System` (padrão) ou `Filled`.
 * @param enabled desligado, o controle fica esmaecido e não responde.
 */
@Composable
public fun <T> DSSegmentedPicker(
    selection: T,
    onSelectionChange: (T) -> Unit,
    options: List<DSSegmentedOption<T>>,
    modifier: Modifier = Modifier,
    title: String? = null,
    style: DSSegmentedStyle = DSSegmentedStyle.System,
    enabled: Boolean = true
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
        if (title != null) {
            Text(
                title,
                style = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold),
                color = DSTheme.colors.onSurfaceMedium
            )
        }

        when (style) {
            DSSegmentedStyle.System -> SystemPicker(selection, onSelectionChange, options, enabled)
            DSSegmentedStyle.Filled -> FilledPicker(selection, onSelectionChange, options, enabled)
        }
    }
}

@Composable
private fun <T> SystemPicker(selection: T, onSelectionChange: (T) -> Unit, options: List<DSSegmentedOption<T>>, enabled: Boolean) {
    val primary = DSTheme.primary

    SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        options.forEachIndexed { index, option ->
            SegmentedButton(
                selected = option.value == selection,
                onClick = { onSelectionChange(option.value) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                enabled = enabled,
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = primary.copy(alpha = SystemActiveFillAlpha),
                    activeContentColor = primary,
                    activeBorderColor = primary,
                    inactiveContainerColor = DSTheme.colors.surface(DSTheme.surfaceLevel),
                    inactiveContentColor = DSTheme.colors.onSurfaceHigh,
                    inactiveBorderColor = primary
                ),
                // Sem o check do Material: rótulos longos precisam da largura toda, como no iOS.
                icon = {},
                label = {
                    Text(
                        option.label,
                        style = DSTypography.subheadline,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}

/**
 * Opções de largura igual num trilho `surface` com contorno fino; a cápsula primária desliza até a
 * selecionada (crossfade com "Remover animações").
 */
@Composable
private fun <T> FilledPicker(selection: T, onSelectionChange: (T) -> Unit, options: List<DSSegmentedOption<T>>, enabled: Boolean) {
    val reduceMotion = DSTheme.reduceMotion
    val selectedIndex = options.indexOfFirst { it.value == selection }.coerceAtLeast(0)
    val position by animateFloatAsState(
        targetValue = selectedIndex.toFloat(),
        animationSpec = DSMotion.medium(),
        label = "DSSegmentedPicker.capsule"
    )

    DSPillTrack(
        count = options.size,
        capsuleProgress = if (reduceMotion) null else ({ position }),
        modifier = Modifier.alpha(if (enabled) 1f else PillDisabledAlpha)
    ) {
        options.forEachIndexed { index, option ->
            DSPillSegment(
                label = option.label,
                isCurrent = index == selectedIndex,
                selected = index == selectedIndex,
                drawsOwnCapsule = reduceMotion,
                enabled = enabled,
                onClick = { onSelectionChange(option.value) }
            )
        }
    }
}

/**
 * Trilho da pílula, compartilhado com o estilo `segmented` do `DSPagedTabs`: fundo `surface`, contorno
 * `hairline` e a cápsula primária posicionada por `capsuleProgress` (índice contínuo; `null` = cada
 * segmento desenha a própria cápsula). A altura é a do alvo de toque, 48 dp.
 */
@Composable
internal fun DSPillTrack(
    count: Int,
    capsuleProgress: (() -> Float)?,
    modifier: Modifier = Modifier,
    semantics: SemanticsPropertyReceiver.() -> Unit = {},
    segments: @Composable RowScope.() -> Unit
) {
    var trackWidth by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current
    val primary = DSTheme.primary

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(DSRadius.capsule)
            .background(DSTheme.colors.surface)
            .border(PillBorderWidth, DSTheme.colors.hairline, DSRadius.capsule)
            .onSizeChanged { trackWidth = it.width }
    ) {
        if (capsuleProgress != null && trackWidth > 0 && count > 0) {
            val segmentWidth = trackWidth / count.toFloat()

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .wrapContentWidth(Alignment.Start, unbounded = true)
                    .offset { IntOffset((capsuleProgress().coerceIn(0f, (count - 1).toFloat()) * segmentWidth).roundToInt(), 0) }
                    .width(with(density) { segmentWidth.toDp() })
                    .fillMaxHeight()
                    .padding(DSSpacing.xxs)
                    .background(primary, DSRadius.capsule)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .selectableGroup()
                .semantics(properties = semantics),
            content = segments
        )
    }
}

/** Um segmento da pílula: rótulo em footnote semibold, claro sobre a cápsula, `onSurfaceMedium` fora dela. */
@Composable
internal fun RowScope.DSPillSegment(
    label: String,
    isCurrent: Boolean,
    selected: Boolean,
    drawsOwnCapsule: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    semantics: SemanticsPropertyReceiver.() -> Unit = {}
) {
    val primary = DSTheme.primary
    val textColor by animateColorAsState(
        targetValue = if (isCurrent) DSTheme.colors.onAccent else DSTheme.colors.onSurfaceMedium,
        animationSpec = DSMotion.short(),
        label = "DSPillSegment.text"
    )
    val capsuleAlpha by animateFloatAsState(
        targetValue = if (drawsOwnCapsule && isCurrent) 1f else 0f,
        animationSpec = DSMotion.short(),
        label = "DSPillSegment.capsule"
    )
    val style = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .heightIn(min = DSTouchTarget.minimum)
            .clip(DSRadius.capsule)
            .selectable(selected = selected, enabled = enabled, role = Role.Tab, onClick = onClick)
            .semantics(properties = semantics)
            .padding(DSSpacing.xxs)
            .background(primary.copy(alpha = primary.alpha * capsuleAlpha), DSRadius.capsule)
            .padding(horizontal = DSSpacing.sm),
        contentAlignment = Alignment.Center
    ) {
        BasicText(
            text = label,
            style = style,
            color = { textColor },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = style.fontSize * PillMinimumScale,
                maxFontSize = style.fontSize,
                stepSize = style.fontSize * PillAutoSizeStep
            )
        )
    }
}

private val PillBorderWidth = 1.dp
private const val PillDisabledAlpha = 0.5f
private const val PillMinimumScale = 0.8f
private const val PillAutoSizeStep = 0.05f
private const val SystemActiveFillAlpha = 0.16f

@DSPreviews
@Composable
private fun DSSegmentedPickerPreview() {
    DSPreviewContainer("Segmented picker") {
        var mode by remember { mutableStateOf("single") }
        var list by remember { mutableStateOf("active") }

        DSSegmentedPicker(
            title = "Mode",
            selection = mode,
            onSelectionChange = { mode = it },
            options = listOf(
                DSSegmentedOption("single", "Single"),
                DSSegmentedOption("list", "List"),
                DSSegmentedOption("teams", "Teams")
            )
        )

        DSSegmentedPicker(
            selection = list,
            onSelectionChange = { list = it },
            options = listOf(DSSegmentedOption("active", "4 Active items"), DSSegmentedOption("inactive", "1 Inactive item")),
            style = DSSegmentedStyle.Filled
        )
    }
}
