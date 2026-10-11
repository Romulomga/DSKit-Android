package io.github.romulomga.dskit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.components.R

/**
 * Botão cheio na cor primária: a ação principal da tela (uma por tela, no máximo). Para a ação que flutua
 * sobre o conteúdo, use [DSExtendedFab].
 *
 * @param icon ícone opcional antes do texto.
 * @param loading troca o conteúdo por um indicador, mantém a largura e ignora toques até terminar.
 * @param destructive pinta na cor de erro ("Excluir conta").
 */
@Composable
public fun DSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    destructive: Boolean = false
) {
    val haptics = rememberDSHaptics()
    val colors = if (destructive) {
        ButtonDefaults.buttonColors(containerColor = DSTheme.colors.error, contentColor = DSTheme.colors.onError)
    } else {
        ButtonDefaults.buttonColors()
    }

    Button(
        onClick = {
            if (loading) return@Button
            haptics.light()
            onClick()
        },
        modifier = modifier.loadingSemantics(loading),
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding(icon)
    ) {
        ButtonContent(text, icon, loading)
    }
}

/**
 * Botão tonal, no contêiner secundário: uma ação importante que não é a principal ("Salvar rascunho"
 * ao lado de "Publicar").
 */
@Composable
public fun DSTonalButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    destructive: Boolean = false
) {
    val haptics = rememberDSHaptics()
    val colors = if (destructive) {
        ButtonDefaults.filledTonalButtonColors(
            containerColor = DSTheme.colors.errorContainer,
            contentColor = DSTheme.colors.onErrorContainer
        )
    } else {
        ButtonDefaults.filledTonalButtonColors()
    }

    FilledTonalButton(
        onClick = {
            if (loading) return@FilledTonalButton
            haptics.light()
            onClick()
        },
        modifier = modifier.loadingSemantics(loading),
        enabled = enabled,
        colors = colors,
        contentPadding = contentPadding(icon)
    ) {
        ButtonContent(text, icon, loading)
    }
}

/** Botão de contorno: ação secundária ao lado da principal ("Cancelar", "Ver detalhes"). */
@Composable
public fun DSOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    loading: Boolean = false,
    destructive: Boolean = false
) {
    val haptics = rememberDSHaptics()
    val colors = if (destructive) {
        ButtonDefaults.outlinedButtonColors(contentColor = DSTheme.colors.error)
    } else {
        ButtonDefaults.outlinedButtonColors()
    }
    val border = if (destructive && enabled) BorderStroke(1.dp, DSTheme.colors.error) else ButtonDefaults.outlinedButtonBorder(enabled)

    OutlinedButton(
        onClick = {
            if (loading) return@OutlinedButton
            haptics.light()
            onClick()
        },
        modifier = modifier.loadingSemantics(loading),
        enabled = enabled,
        colors = colors,
        border = border,
        contentPadding = contentPadding(icon)
    ) {
        ButtonContent(text, icon, loading)
    }
}

/** Botão só de texto: a ação de menor peso (em diálogos, cards, "Esqueci a senha"). */
@Composable
public fun DSTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    destructive: Boolean = false
) {
    val haptics = rememberDSHaptics()
    val colors = if (destructive) {
        ButtonDefaults.textButtonColors(contentColor = DSTheme.colors.error)
    } else {
        ButtonDefaults.textButtonColors()
    }

    TextButton(
        onClick = {
            haptics.light()
            onClick()
        },
        modifier = modifier,
        enabled = enabled,
        colors = colors,
        contentPadding = if (icon == null) ButtonDefaults.TextButtonContentPadding else ButtonDefaults.TextButtonWithIconContentPadding
    ) {
        ButtonContent(text, icon, loading = false)
    }
}

/** Texto e ícone; carregando, ficam invisíveis (guardam a largura) sob o indicador. */
@Composable
private fun ButtonContent(text: String, icon: ImageVector?, loading: Boolean) {
    Box(contentAlignment = Alignment.Center) {
        Row(modifier = Modifier.alpha(if (loading) 0f else 1f), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))

                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            }

            Text(text)
        }

        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(ButtonDefaults.IconSize),
                color = LocalContentColor.current,
                strokeWidth = 2.dp
            )
        }
    }
}

private fun contentPadding(icon: ImageVector?): PaddingValues =
    if (icon == null) ButtonDefaults.ContentPadding else ButtonDefaults.ButtonWithIconContentPadding

@Composable
private fun Modifier.loadingSemantics(loading: Boolean): Modifier {
    if (!loading) return this
    val label = stringResource(R.string.dskit_loading)
    return semantics { stateDescription = label }
}

@DSPreviews
@Composable
private fun DSButtonPreview() {
    DSPreviewContainer("Buttons") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSButton("Publish", onClick = {}, icon = DSIcons.Add)

            DSButton("Publishing", onClick = {}, loading = true)

            DSTonalButton("Save draft", onClick = {})

            DSOutlinedButton("Cancel", onClick = {})

            DSTextButton("Forgot password", onClick = {})

            DSButton("Delete account", onClick = {}, destructive = true)

            DSButton("Disabled", onClick = {}, enabled = false)
        }
    }
}
