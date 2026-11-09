package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.window.DialogProperties
import io.github.romulomga.dskit.components.R

/**
 * Diálogo do Material 3 com um campo de texto: título, mensagem opcional, o [DSTextField] já focado e as duas
 * ações. Use para pedir um texto curto sem sair da tela (dar um nome, criar uma etiqueta); para mais de um
 * campo, use um [DSBottomSheet] ou uma tela.
 *
 * Tocar fora não fecha (um toque ao lado do teclado apagaria o que foi digitado); o voltar e o botão de cancelar
 * chamam [onDismissRequest]. A ação do teclado ("Concluído") confirma, como o botão, e nenhum dos dois faz nada
 * com [confirmEnabled] desligado. Quem chama guarda o texto e a visibilidade, e fecha o diálogo em [onConfirm].
 *
 * @param label rótulo do campo; é também o que o TalkBack lê.
 * @param errorText erro abaixo do campo ("Já existe um efeito com esse nome").
 * @param confirmEnabled desligue enquanto o texto não serve (vazio, repetido).
 * @param dismissText por padrão "Cancelar".
 * @param keyboardOptions o teclado do campo; a ação é sempre `ImeAction.Done`, que confirma.
 * @param fieldTestTag `testTag` do campo, para os testes de UI do app.
 * @param confirmTestTag `testTag` do botão de confirmar.
 * @param dismissTestTag `testTag` do botão de cancelar.
 */
@Composable
public fun DSTextInputDialog(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    message: String? = null,
    placeholder: String? = null,
    errorText: String? = null,
    confirmEnabled: Boolean = true,
    dismissText: String = stringResource(R.string.dskit_cancel),
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    fieldTestTag: String? = null,
    confirmTestTag: String? = null,
    dismissTestTag: String? = null
) {
    val focus = remember { FocusRequester() }

    LaunchedEffect(Unit) { focus.requestFocus() }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DSTextButton(confirmText, onClick = onConfirm, modifier = Modifier.optionalTestTag(confirmTestTag), enabled = confirmEnabled)
        },
        modifier = modifier,
        dismissButton = { DSTextButton(dismissText, onClick = onDismissRequest, modifier = Modifier.optionalTestTag(dismissTestTag)) },
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
                // Só a mensagem rola: em fonte grande, o campo continua à vista.
                if (message != null) {
                    Text(message, modifier = Modifier.weight(1f, fill = false).verticalScroll(rememberScrollState()))
                }

                DSTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = label,
                    modifier = Modifier.fillMaxWidth().focusRequester(focus).optionalTestTag(fieldTestTag),
                    placeholder = placeholder,
                    errorText = errorText,
                    keyboardOptions = keyboardOptions.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { if (confirmEnabled) onConfirm() })
                )
            }
        },
        properties = DialogProperties(dismissOnClickOutside = false)
    )
}

private fun Modifier.optionalTestTag(tag: String?): Modifier = if (tag == null) this else testTag(tag)

@DSPreviews
@Composable
private fun DSTextInputDialogPreview() {
    DSPreviewContainer("Text input dialog") {
        DSTextInputDialog(
            title = "New tag",
            message = "Tags group your posts in the feed.",
            value = "Beach house",
            onValueChange = {},
            label = "Name",
            confirmText = "Add",
            onConfirm = {},
            onDismissRequest = {}
        )
    }
}
