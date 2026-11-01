package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity

/**
 * Diálogo de alerta do Material 3: título, mensagem opcional, a ação de confirmar e, se houver, a de
 * cancelar. Use para confirmar o que não dá para desfazer ou para um erro que pede decisão; para avisos
 * que não pedem resposta, use um snackbar. Para escolher entre várias ações, use um [DSBottomSheet] com
 * [DSSheetAction]s.
 *
 * Quem chama controla a visibilidade: mostre o diálogo enquanto um estado estiver ligado e desligue-o em
 * [onDismissRequest] e depois de [onConfirm].
 *
 * A mensagem rola se não couber. Os botões ficam lado a lado; com a confirmação em destaque, ou com fonte grande
 * (a partir de 150%), vão empilhados em largura cheia, a confirmação em cima, para nenhum sair cortado.
 *
 * @param destructive pinta a confirmação na cor de erro ("Excluir", "Sair da conta").
 * @param dismissText rótulo de cancelar; `null` deixa só a confirmação.
 * @param onDismiss o que o botão de cancelar faz; por padrão, o mesmo que tocar fora ([onDismissRequest]). Separe
 *   os dois quando tocar fora deve ser a escolha segura e o botão de cancelar não ("Continuar no alerta" confirma
 *   e é o padrão; "Fechar" é o botão de baixo).
 * @param emphasizeConfirm a confirmação vira um botão cheio, a ação principal: use quando ela é a escolha segura.
 * @param icon ícone opcional acima do título, que então fica centralizado.
 */
@Composable
public fun DSAlertDialog(
    title: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    dismissText: String? = null,
    icon: ImageVector? = null,
    destructive: Boolean = false,
    onDismiss: () -> Unit = onDismissRequest,
    emphasizeConfirm: Boolean = false
) {
    val haptics = rememberDSHaptics()
    val stacked = emphasizeConfirm || LocalDensity.current.fontScale >= StackedFontScale
    val confirm: () -> Unit = {
        if (destructive) haptics.warning()
        onConfirm()
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            if (stacked) {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
                    if (emphasizeConfirm) {
                        DSButton(confirmText, onClick = confirm, modifier = Modifier.fillMaxWidth(), destructive = destructive)
                    } else {
                        DSTextButton(confirmText, onClick = confirm, modifier = Modifier.fillMaxWidth(), destructive = destructive)
                    }

                    if (dismissText != null) {
                        DSTextButton(dismissText, onClick = onDismiss, modifier = Modifier.fillMaxWidth())
                    }
                }
            } else {
                DSTextButton(text = confirmText, onClick = confirm, destructive = destructive)
            }
        },
        dismissButton = if (stacked) null else dismissText?.let { { DSTextButton(it, onClick = onDismiss) } },
        icon = icon?.let { { Icon(it, contentDescription = null) } },
        title = { Text(title) },
        text = message?.let { { Text(it, modifier = Modifier.verticalScroll(rememberScrollState())) } }
    )
}

/** A partir desta escala de fonte os botões do diálogo empilham. */
private const val StackedFontScale = 1.5f

@DSPreviews
@Composable
private fun DSAlertDialogPreview() {
    DSPreviewContainer("Alert dialog") {
        DSAlertDialog(
            title = "Delete post?",
            message = "The post leaves the feed and your partners stop seeing it.",
            confirmText = "Delete",
            dismissText = "Cancel",
            destructive = true,
            onConfirm = {},
            onDismissRequest = {}
        )
    }
}

@DSPreviews
@Composable
private fun DSAlertDialogEmphasisPreview() {
    DSPreviewContainer("Alert dialog, safe action emphasized") {
        DSAlertDialog(
            title = "Close the alert?",
            message = "If you haven't sent it yet, nobody knows.",
            confirmText = "Keep the alert",
            dismissText = "I'm fine, close",
            emphasizeConfirm = true,
            onConfirm = {},
            onDismiss = {},
            onDismissRequest = {}
        )
    }
}
