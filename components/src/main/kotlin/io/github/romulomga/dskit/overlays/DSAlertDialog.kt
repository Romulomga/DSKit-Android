package io.github.romulomga.dskit

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Diálogo de alerta do Material 3: título, mensagem opcional, a ação de confirmar e, se houver, a de
 * cancelar. Use para confirmar o que não dá para desfazer ou para um erro que pede decisão; para avisos
 * que não pedem resposta, use um snackbar. Para escolher entre várias ações, use um [DSBottomSheet] com
 * [DSSheetAction]s.
 *
 * Quem chama controla a visibilidade: mostre o diálogo enquanto um estado estiver ligado e desligue-o em
 * [onDismissRequest] e depois de [onConfirm].
 *
 * @param destructive pinta a confirmação na cor de erro ("Excluir", "Sair da conta").
 * @param dismissText rótulo de cancelar; `null` deixa só a confirmação.
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
    destructive: Boolean = false
) {
    val haptics = rememberDSHaptics()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        confirmButton = {
            DSTextButton(
                text = confirmText,
                onClick = {
                    if (destructive) haptics.warning()
                    onConfirm()
                },
                destructive = destructive
            )
        },
        dismissButton = dismissText?.let { { DSTextButton(it, onClick = onDismissRequest) } },
        icon = icon?.let { { Icon(it, contentDescription = null) } },
        title = { Text(title) },
        text = message?.let { { Text(it) } }
    )
}

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
