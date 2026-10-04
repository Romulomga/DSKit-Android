package io.github.romulomga.dskit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import io.github.romulomga.dskit.components.R

/** Ícones do botão de revelar a senha. Padrão: olho e olho cortado (`DSIcons.Eye` / `DSIcons.EyeSlash`). */
@Immutable
public class DSSecureToggleIcons(public val show: ImageVector, public val hide: ImageVector) {
    public companion object {
        /** O par padrão, o `.system` do DSKit iOS. */
        public val Default: DSSecureToggleIcons by lazy { DSSecureToggleIcons(show = DSIcons.Eye, hide = DSIcons.EyeSlash) }
    }
}

/**
 * Campo de texto do DSKit: título opcional, ícone à esquerda, senha com botão de revelar, legenda de ajuda ou
 * erro e botão de limpar. Teclado, tipo de conteúdo e ação do "Enter" vão por `keyboardOptions` e
 * `keyboardActions` (o `onSubmit` do iOS é o `onDone`/`onNext`…). O foco pode ser comandado de fora por
 * `focusRequester`, e `onFocusChange` avisa quando o campo ganha ou perde o foco (o `onEditingChanged`).
 */
@Composable
public fun DSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    placeholder: String = "",
    errorMessage: String? = null,
    helperText: String? = null,
    isInvalid: Boolean = false,
    showsClearButton: Boolean = false,
    isSecure: Boolean = false,
    secureToggleIcons: DSSecureToggleIcons = DSSecureToggleIcons.Default,
    leadingIcon: ImageVector? = null,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
    onFocusChange: ((Boolean) -> Unit)? = null
) {
    val haptics = rememberDSHaptics()
    val requester = focusRequester ?: remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }
    var isRevealed by rememberSaveable { mutableStateOf(false) }
    val hasError = isInvalid || errorMessage != null
    val textColor = if (enabled) DSTheme.colors.onSurfaceHigh else DSTheme.colors.onSurfaceMedium
    val resolvedKeyboard = if (isSecure && keyboardOptions.keyboardType in listOf(KeyboardType.Unspecified, KeyboardType.Text)) {
        keyboardOptions.copy(keyboardType = KeyboardType.Password)
    } else {
        keyboardOptions
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
        if (title != null) {
            DSFieldLabel(title, modifier = Modifier.clearAndSetSemantics {})
        }

        DSInputBox(
            leadingIcon = leadingIcon,
            isFocused = isFocused,
            isInvalid = hasError,
            onClick = if (enabled) ({ requester.requestFocus() }) else null
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(requester)
                    .onFocusChanged { state ->
                        if (state.isFocused != isFocused) {
                            isFocused = state.isFocused
                            onFocusChange?.invoke(state.isFocused)
                        }
                    }
                    .semantics {
                        if (title != null) contentDescription = title
                        if (errorMessage != null) error(errorMessage)
                    },
                enabled = enabled,
                singleLine = true,
                textStyle = DSTypography.body.copy(color = textColor),
                cursorBrush = SolidColor(DSTheme.colors.accent),
                visualTransformation = if (isSecure && !isRevealed) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = resolvedKeyboard,
                keyboardActions = keyboardActions,
                decorationBox = { innerField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty() && placeholder.isNotEmpty()) {
                            Text(
                                text = placeholder,
                                style = DSTypography.body,
                                color = DSTheme.colors.onSurfaceMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        innerField()
                    }
                }
            )

            if (isSecure) {
                DSFieldIconButton(
                    icon = if (isRevealed) secureToggleIcons.hide else secureToggleIcons.show,
                    contentDescription = stringResource(
                        if (isRevealed) R.string.dskit_hide_password else R.string.dskit_show_password
                    ),
                    enabled = enabled,
                    fixedWidth = true
                ) {
                    isRevealed = !isRevealed
                    haptics.light()
                }
            } else if (showsClearButton && enabled && value.isNotEmpty()) {
                DSFieldIconButton(
                    icon = DSIcons.XCircle,
                    contentDescription = stringResource(R.string.dskit_clear),
                    tertiary = true
                ) {
                    onValueChange("")
                    haptics.light()
                }
            }
        }

        DSFieldCaption(errorMessage = errorMessage, helperText = helperText)
    }
}

/**
 * Botão só com ícone dentro do campo (revelar senha, limpar). A altura é a do ícone, para não deixar o campo
 * mais alto que os outros; o Compose estende a área de toque até 48 dp sozinho.
 */
@Composable
private fun DSFieldIconButton(
    icon: ImageVector,
    contentDescription: String,
    enabled: Boolean = true,
    fixedWidth: Boolean = false,
    tertiary: Boolean = false,
    onClick: () -> Unit
) {
    val iconSize = dsInputIconSize(DSTypography.body)
    val interaction = remember { MutableInteractionSource() }
    val sizeModifier = if (fixedWidth) Modifier.width(DSInputMetrics.tapTarget).height(iconSize) else Modifier.size(iconSize)

    Box(
        modifier = sizeModifier
            .clickable(
                interactionSource = interaction,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick
            )
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (tertiary) DSTheme.colors.onSurfaceLight else DSTheme.colors.onSurfaceMedium,
            modifier = Modifier.size(iconSize)
        )
    }
}

// MARK: - Previews

@Composable
private fun DSTextFieldPreviewContent() {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("bad@") }
    var password by remember { mutableStateOf("secret") }

    Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
        DSTextField(value = name, onValueChange = { name = it }, title = "Name", placeholder = "Your name", showsClearButton = true)

        DSTextField(
            value = email,
            onValueChange = { email = it },
            title = "Email",
            placeholder = "you@example.com",
            errorMessage = "Invalid email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        DSTextField(
            value = password,
            onValueChange = { password = it },
            title = "Password",
            placeholder = "Password",
            helperText = "At least 4 characters",
            isSecure = true,
            leadingIcon = DSIcons.Lock
        )
    }
}

@DSPreviews
@Composable
private fun DSTextFieldCompactPreview() {
    DSPreviewContainer("Text field") { DSTextFieldPreviewContent() }
}

@DSPreviews
@Composable
private fun DSTextFieldProminentPreview() {
    DSPreviewContainer("Text field") {
        CompositionLocalProvider(LocalDSFieldLabelStyle provides DSFieldLabelStyle.Prominent) {
            DSTextFieldPreviewContent()
        }
    }
}
