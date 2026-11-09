package io.github.romulomga.dskit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.romulomga.dskit.components.R

/**
 * A confirmação de uma exclusão, sobre o [DSAlertDialog]: a pergunta, o botão que exclui na cor de erro e o
 * "Cancelar". Aparece enquanto [pending] não for `null`. O gatilho guarda ali o item (ou o id dele) que vai sair;
 * confirmar chama [onDelete] com ele e depois [onDismiss]; cancelar, tocar fora e o voltar chamam só [onDismiss],
 * que deve zerar o [pending].
 *
 * ```
 * var pendingDelete by rememberSaveable { mutableStateOf<String?>(null) }
 *
 * DSConfirmDeleteDialog(
 *     pending = pendingDelete,
 *     title = "Excluir cliente?",
 *     confirmText = "Excluir cliente",
 *     onDelete = viewModel::delete,
 *     onDismiss = { pendingDelete = null }
 * )
 * ```
 *
 * Confirmar vibra o aviso uma vez (o do [DSAlertDialog] destrutivo); quem chama não vibra de novo.
 *
 * @param pending o que será excluído; `null` esconde o diálogo. Guarde num `rememberSaveable` para a pergunta
 *   sobreviver à rotação.
 * @param confirmText nomeia o que sai ("Excluir cliente"), melhor que um "Excluir" solto.
 * @param message o que se perde junto ("Os recibos deste cliente continuam.").
 * @param dismissText por padrão "Cancelar".
 */
@Composable
public fun <T : Any> DSConfirmDeleteDialog(
    pending: T?,
    title: String,
    confirmText: String,
    onDelete: (T) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    dismissText: String = stringResource(R.string.dskit_cancel)
) {
    val item = pending ?: return

    DSAlertDialog(
        title = title,
        confirmText = confirmText,
        onConfirm = {
            onDelete(item)
            onDismiss()
        },
        onDismissRequest = onDismiss,
        modifier = modifier,
        message = message,
        dismissText = dismissText,
        destructive = true
    )
}

/** A mesma confirmação para o "Excluir" de dentro de um editor, que não tem um item em espera: [visible] a mostra. */
@Composable
public fun DSConfirmDeleteDialog(
    visible: Boolean,
    title: String,
    confirmText: String,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    dismissText: String = stringResource(R.string.dskit_cancel)
) {
    DSConfirmDeleteDialog(
        pending = if (visible) Unit else null,
        title = title,
        confirmText = confirmText,
        onDelete = { onDelete() },
        onDismiss = onDismiss,
        modifier = modifier,
        message = message,
        dismissText = dismissText
    )
}

@DSPreviews
@Composable
private fun DSConfirmDeleteDialogPreview() {
    DSPreviewContainer("Confirm delete") {
        DSConfirmDeleteDialog(
            visible = true,
            title = "Delete client?",
            message = "Their receipts stay in your history.",
            confirmText = "Delete client",
            onDelete = {},
            onDismiss = {}
        )
    }
}
