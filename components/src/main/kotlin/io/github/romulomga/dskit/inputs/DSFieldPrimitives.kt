package io.github.romulomga.dskit

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// MARK: - Rótulo e legenda

/**
 * Título acima de um campo. Use em campos próprios para alinharem com o `DSTextField`. O tamanho vem de
 * `LocalDSFieldLabelStyle`: `Compact` (footnote secundário) ou `Prominent` (subheadline negrito).
 */
@Composable
public fun DSFieldLabel(title: String, modifier: Modifier = Modifier) {
    when (LocalDSFieldLabelStyle.current) {
        DSFieldLabelStyle.Compact -> Text(
            text = title,
            modifier = modifier,
            style = DSTypography.footnote.copy(fontWeight = FontWeight.SemiBold),
            color = DSTheme.colors.onSurfaceMedium
        )

        DSFieldLabelStyle.Prominent -> Text(
            text = title,
            modifier = modifier,
            style = DSTypography.subheadline.copy(fontWeight = FontWeight.Bold),
            color = DSTheme.colors.onSurfaceHigh
        )
    }
}

/**
 * Linha de erro ou de ajuda embaixo de um campo. Com os dois preenchidos, o erro vence. No estilo `Compact` o
 * erro leva ícone; no `Prominent` é subheadline em itálico.
 */
@Composable
public fun DSFieldCaption(modifier: Modifier = Modifier, errorMessage: String? = null, helperText: String? = null) {
    val style = LocalDSFieldLabelStyle.current
    when {
        errorMessage != null -> when (style) {
            DSFieldLabelStyle.Compact -> DSFieldErrorLabel(errorMessage, modifier)

            DSFieldLabelStyle.Prominent -> Text(
                text = errorMessage,
                modifier = modifier,
                style = DSTypography.subheadline.copy(fontStyle = FontStyle.Italic),
                color = DSTheme.colors.errorHigh
            )
        }

        helperText != null -> Text(
            text = helperText,
            modifier = modifier,
            style = DSTypography.footnote.copy(
                fontStyle = if (style == DSFieldLabelStyle.Prominent) FontStyle.Italic else FontStyle.Normal
            ),
            color = DSTheme.colors.onSurfaceMedium
        )
    }
}

/** Erro com ícone, em footnote vermelho: o erro do estilo `Compact` (e do `DSNumberField`). */
@Composable
internal fun DSFieldErrorLabel(message: String, modifier: Modifier = Modifier) {
    val textStyle = DSTypography.footnote
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = DSIcons.ExclamationCircle,
            contentDescription = null,
            tint = DSTheme.colors.errorHigh,
            modifier = Modifier.size(dsInputIconSize(textStyle))
        )

        Text(text = message, style = textStyle, color = DSTheme.colors.errorHigh)
    }
}

// MARK: - Caixa do campo

/**
 * O contêiner de todo campo do DSKit: superfície, cantos arredondados e uma borda que diz o estado
 * (`hairline` → `accent` com foco → `errorHigh` com erro). Monte campos próprios (código, campos pareados,
 * seletores) sobre ela para baterem com o `DSTextField`. Tocar na caixa chama `onClick`, que os campos
 * próprios usam para levar o foco ao campo de dentro.
 *
 * A cor da superfície segue `DSTheme.surfaceLevel`: dentro de um card, a caixa fica em `surfaceElevated`.
 */
@Composable
public fun DSInputBox(
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    isFocused: Boolean = false,
    isInvalid: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) {
    val shape = DSRadius.medium
    val borderColor by animateColorAsState(
        targetValue = DSInputMetrics.borderColor(isFocused = isFocused, isInvalid = isInvalid),
        animationSpec = DSMotion.short(),
        label = "DSInputBox.borderColor"
    )
    val borderWidth by animateDpAsState(
        targetValue = DSInputMetrics.borderWidth(isFocused = isFocused, isInvalid = isInvalid),
        animationSpec = DSMotion.short(),
        label = "DSInputBox.borderWidth"
    )
    val tap = if (onClick != null) {
        Modifier.pointerInput(onClick) { detectTapGestures { onClick() } }
    } else {
        Modifier
    }
    val textStyle = DSTypography.body

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = DSInputMetrics.minHeight)
            .clip(shape)
            .background(DSTheme.colors.surface(DSTheme.surfaceLevel))
            .border(borderWidth, borderColor, shape)
            .then(tap)
            .padding(horizontal = DSSpacing.md, vertical = DSSpacing.sm + DSSpacing.xxs),
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides textStyle,
            LocalContentColor provides DSTheme.colors.onSurfaceHigh
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = DSTheme.colors.onSurfaceHigh,
                    modifier = Modifier
                        .widthIn(min = DSInputMetrics.iconWidth)
                        .size(dsInputIconSize(textStyle))
                )
            }

            content()
        }
    }
}

/** Medidas compartilhadas para todo campo ter a mesma silhueta. */
public object DSInputMetrics {
    /** Altura mínima da caixa. O iOS usa 45 pt; aqui é o alvo de toque do Android, 48 dp. */
    public val minHeight: Dp = DSTouchTarget.minimum
    public val iconWidth: Dp = 20.dp

    /** Área mínima de toque de controles dentro do campo (Material: 48 dp). */
    public val tapTarget: Dp = DSTouchTarget.minimum

    @Composable
    @ReadOnlyComposable
    public fun borderColor(isFocused: Boolean, isInvalid: Boolean): Color = when {
        isInvalid -> DSTheme.colors.errorHigh.copy(alpha = 0.7f)
        isFocused -> DSTheme.colors.accent.copy(alpha = 0.7f)
        else -> DSTheme.colors.hairline
    }

    public fun borderWidth(isFocused: Boolean, isInvalid: Boolean): Dp = if (isFocused || isInvalid) 1.5.dp else 1.dp
}

/** Ícone do tamanho do texto, como um SF Symbol na mesma fonte: escala com a fonte do sistema. */
@Composable
@ReadOnlyComposable
internal fun dsInputIconSize(style: TextStyle): Dp = with(LocalDensity.current) { style.fontSize.toDp() } * 1.2f

// MARK: - Previews

@Composable
private fun DSInputBoxPreviewContent() {
    var text by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
        DSFieldLabel("Custom input")

        DSInputBox(leadingIcon = DSIcons.Lock) {
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.weight(1f),
                textStyle = DSTypography.body.copy(color = DSTheme.colors.onSurfaceHigh),
                cursorBrush = SolidColor(DSTheme.colors.accent)
            )
        }

        DSFieldLabel("Invalid")

        DSInputBox(isInvalid = true) {
            Text("Type", color = DSTheme.colors.onSurfaceMedium)
        }

        DSFieldCaption(errorMessage = "Something is off")
    }
}

@DSPreviews
@Composable
private fun DSInputBoxCompactPreview() {
    DSPreviewContainer("Input box") { DSInputBoxPreviewContent() }
}

@DSPreviews
@Composable
private fun DSInputBoxProminentPreview() {
    DSPreviewContainer("Input box") {
        CompositionLocalProvider(LocalDSFieldLabelStyle provides DSFieldLabelStyle.Prominent) {
            DSInputBoxPreviewContent()
        }
    }
}
