package io.github.romulomga.dskit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.romulomga.dskit.components.R

/**
 * A saída de uma tela com algo digitado: [requestExit] sai direto quando não há mudança e pergunta quando há.
 * Ligue a ele a seta da barra (`DSScreen(onBack = guard::requestExit)`), o botão "Cancelar" e o `onDismissRequest`
 * de um painel; o voltar do sistema já passa por aqui pelo [DSDiscardGuard].
 */
@Stable
public class DSDiscardGuardState internal constructor(private val onExit: State<() -> Unit>, confirming: Boolean = false) {
    internal var hasChanges = false

    /** Se a pergunta de descarte está aberta. */
    public var isConfirming: Boolean by mutableStateOf(confirming)
        internal set

    /** Sai, ou pergunta antes se houver mudança. */
    public fun requestExit() {
        if (hasChanges) isConfirming = true else onExit.value()
    }

    internal fun discard() {
        isConfirming = false
        onExit.value()
    }
}

/**
 * O estado do [DSDiscardGuard] de uma tela. A pergunta aberta sobrevive à recriação da Activity (tema, fonte,
 * idioma, rotação) e à morte do processo.
 *
 * @param onExit o que sair faz (fechar o painel, voltar na pilha). Vale sempre o lambda mais recente.
 */
@Composable
public fun rememberDSDiscardGuardState(onExit: () -> Unit): DSDiscardGuardState {
    val current = rememberUpdatedState(onExit)

    return rememberSaveable(
        saver = Saver(save = { it.isConfirming }, restore = { DSDiscardGuardState(current, confirming = it) })
    ) {
        DSDiscardGuardState(current)
    }
}

/**
 * Guarda de descarte: com [hasChanges], o voltar do sistema e o [DSDiscardGuardState.requestExit] abrem um
 * [DSAlertDialog] perguntando antes de sair; sem mudança, saem direto. Use em toda tela ou painel de edição, para
 * ninguém perder o que digitou por um toque no voltar.
 *
 * Quem diz se há mudança é a tela. Para comparar os campos com o que eles eram quando a tela abriu, use
 * [rememberDSHasChanges]. Para só a pergunta, com a visibilidade por conta de quem chama (um ViewModel), use o
 * [DSDiscardDialog].
 *
 * ```
 * val guard = rememberDSDiscardGuardState(onExit = onBack)
 *
 * DSDiscardGuard(guard, hasChanges = rememberDSHasChanges(name to phone))
 *
 * DSScreen(title = "Editar cliente", onBack = guard::requestExit) { … }
 * ```
 *
 * @param title a pergunta; por padrão "Descartar as alterações?".
 * @param message detalhe opcional abaixo da pergunta ("O que você digitou aqui será perdido.").
 * @param discardText o botão que sai, na cor de erro; por padrão "Descartar".
 * @param keepEditingText o botão que fica; por padrão "Continuar editando". Tocar fora também fica.
 */
@Composable
public fun DSDiscardGuard(
    state: DSDiscardGuardState,
    hasChanges: Boolean,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.dskit_discard_changes_title),
    message: String? = null,
    discardText: String = stringResource(R.string.dskit_discard),
    keepEditingText: String = stringResource(R.string.dskit_keep_editing)
) {
    SideEffect { state.hasChanges = hasChanges }

    BackHandler(enabled = hasChanges) { state.isConfirming = true }

    if (state.isConfirming) {
        DSDiscardDialog(
            onDiscard = state::discard,
            onKeepEditing = { state.isConfirming = false },
            modifier = modifier,
            title = title,
            message = message,
            discardText = discardText,
            keepEditingText = keepEditingText
        )
    }
}

/**
 * Só a pergunta do [DSDiscardGuard], para quem já controla a visibilidade (um ViewModel que decide se há o que
 * perder): mostre enquanto o estado estiver ligado e desligue nos dois callbacks. Descartar vibra o aviso.
 *
 * @param onDiscard o botão de descartar.
 * @param onKeepEditing o botão de continuar, o toque fora e o voltar.
 */
@Composable
public fun DSDiscardDialog(
    onDiscard: () -> Unit,
    onKeepEditing: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(R.string.dskit_discard_changes_title),
    message: String? = null,
    discardText: String = stringResource(R.string.dskit_discard),
    keepEditingText: String = stringResource(R.string.dskit_keep_editing)
) {
    DSAlertDialog(
        title = title,
        confirmText = discardText,
        onConfirm = onDiscard,
        onDismissRequest = onKeepEditing,
        modifier = modifier,
        message = message,
        dismissText = keepEditingText,
        destructive = true
    )
}

/**
 * Diz se [snapshot] mudou desde que a tela abriu, para o `hasChanges` do [DSDiscardGuard]. O retrato de partida
 * é lido depois da primeira composição (os valores que a tela preenche ao abrir não contam como mudança) e fica
 * salvo como o estado dos campos: recriada a Activity ou morto o processo, ele continua sendo o de quando a tela
 * abriu, e não o valor já editado (o que tiraria a pergunta e descartaria a edição sem aviso).
 *
 * @param snapshot um valor com igualdade que muda sempre que um campo editável muda e que caiba num `Bundle`:
 *   números, texto, `java.time`, enums e `Pair`, `Triple`, listas ou conjuntos deles. `null` é um retrato válido.
 */
@Composable
public fun <T> rememberDSHasChanges(snapshot: T): Boolean {
    var pristine by rememberSaveable(stateSaver = holderSaver()) { mutableStateOf<Holder<T>?>(null) }

    LaunchedEffect(Unit) { if (pristine == null) pristine = Holder(snapshot) }

    return pristine.let { it != null && it.value != snapshot }
}

/** Embrulha o retrato: `null` é um retrato válido e não pode se confundir com "ainda não guardado". */
private class Holder<T>(val value: T)

/** Salva o retrato numa lista de um elemento, para o `null` guardado continuar diferente de "nada guardado". */
@Suppress("UNCHECKED_CAST")
private fun <T> holderSaver(): Saver<Holder<T>?, Any> = Saver(
    save = { holder -> holder?.let { arrayListOf(it.value) } },
    restore = { saved -> Holder((saved as List<*>).first() as T) }
)

@DSPreviews
@Composable
private fun DSDiscardGuardPreview() {
    var note by rememberSaveable { mutableStateOf("") }
    val guard = rememberDSDiscardGuardState(onExit = {})

    DSPreviewContainer("Discard guard") {
        DSDiscardGuard(guard, hasChanges = rememberDSHasChanges(note))

        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSTextField(value = note, onValueChange = { note = it }, label = "Note")

            DSTextButton(text = "Cancel", onClick = guard::requestExit)
        }
    }
}

@DSPreviews
@Composable
private fun DSDiscardDialogPreview() {
    DSPreviewContainer("Discard dialog") {
        DSDiscardDialog(onDiscard = {}, onKeepEditing = {}, message = "What you typed here will be lost.")
    }
}
