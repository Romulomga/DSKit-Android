package io.github.romulomga.dskit

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Estratégia de cena do Navigation 3 que mostra num [DSBottomSheet] a entrada do topo da pilha marcada com
 * [bottomSheet] no metadado, por cima das telas de baixo. Ponha antes das estratégias que não sobrepõem:
 *
 * ```
 * NavDisplay(
 *     backStack = backStack,
 *     onBack = navigator::goBack,
 *     sceneStrategies = listOf(DSBottomSheetSceneStrategy(), DialogSceneStrategy()),
 *     entryProvider = entryProvider {
 *         entry<FiltersKey>(metadata = DSBottomSheetSceneStrategy.bottomSheet()) { FiltersScreen() }
 *     }
 * )
 * ```
 *
 * O gesto de fechar, o toque no véu e o voltar do sistema chamam o `onBack` do `NavDisplay`. Tirada da pilha, a
 * entrada sai da composição na hora: para o painel descer antes, passe a remoção pelo [DSNavSheets.hideThen].
 */
public class DSBottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val entry = entries.lastOrNull() ?: return null
        val properties = entry.metadata[BottomSheetKey] ?: return null

        return BottomSheetScene(
            key = entry.contentKey,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = entry,
            properties = properties,
            onBack = onBack
        )
    }

    public companion object {
        /**
         * O metadado da entrada que abre num painel.
         *
         * @param scrollable `false` quando a tela já rola sozinha (uma lista preguiçosa, um formulário que sobe
         *   com o teclado); ver o `scrollable` do [DSBottomSheet].
         */
        public fun bottomSheet(scrollable: Boolean = true): Map<String, Any> = metadata {
            put(BottomSheetKey, BottomSheetProperties(scrollable))
        }

        /** Se o metadado de uma entrada a marca como painel. */
        public fun isBottomSheet(metadata: Map<String, Any>): Boolean = metadata[BottomSheetKey] != null
    }
}

private data class BottomSheetProperties(val scrollable: Boolean)

private object BottomSheetKey : NavMetadataKey<BottomSheetProperties>

private data class BottomSheetScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val properties: BottomSheetProperties,
    private val onBack: () -> Unit
) : OverlayScene<T> {
    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable () -> Unit = {
        val state = rememberDSBottomSheetState()

        // O estado do painel fica num registro pela chave: quem vai tirar a entrada da pilha o faz descer antes
        // ([DSNavSheets.hideThen]); tirada direto, a navegação a removia da tela antes de ele descer.
        DisposableEffect(key, state) {
            DSNavSheets.register(key, state)
            onDispose { DSNavSheets.unregister(key, state) }
        }

        DSBottomSheet(state = state, onDismissRequest = onBack, scrollable = properties.scrollable) {
            entry.Content()
        }
    }

    override suspend fun onRemove() {
        DSNavSheets.stateOf(key)?.hide()
    }
}

/**
 * Os painéis abertos pelo [DSBottomSheetSceneStrategy], pela chave da cena. Ligue o [hideThen] ao ponto do app que
 * tira entradas da pilha (o `beforeRemove` do navegador), para o painel descer antes de a entrada sair:
 *
 * ```
 * val scope = rememberCoroutineScope()
 * val navigator = Navigator(state, beforeRemove = { key, then -> DSNavSheets.hideThen(scope, key, then) })
 * ```
 */
public object DSNavSheets {
    private val states = mutableMapOf<Any, DSBottomSheetState>()
    private val hiding = mutableSetOf<Any>()

    /**
     * O painel de [key] desce e [then] roda depois. Sem painel aberto para a chave, ou com ele já fechado pelo
     * gesto, [then] roda na hora. Chamado de novo enquanto desce (um toque duplo), não faz nada.
     *
     * @param scope de composição (`rememberCoroutineScope`), para a animação respeitar "Remover animações".
     * @param key a chave da entrada na pilha, ou a chave de conteúdo dela.
     */
    public fun hideThen(scope: CoroutineScope, key: Any, then: () -> Unit) {
        val sceneKey = states.keys.firstOrNull { it == key || it == contentKeyOf(key) }
        val sheet = sceneKey?.let(states::get)

        if (sheet == null || !sheet.isVisible) {
            then()
            return
        }

        if (!hiding.add(sceneKey)) return

        scope.launch {
            try {
                sheet.hide()
            } finally {
                hiding.remove(sceneKey)
                then()
            }
        }
    }

    internal fun register(key: Any, state: DSBottomSheetState) {
        states[key] = state
    }

    internal fun unregister(key: Any, state: DSBottomSheetState) {
        if (states[key] === state) states.remove(key)
    }

    internal fun stateOf(key: Any): DSBottomSheetState? = states[key]

    /** A chave de conteúdo padrão de uma entrada do Navigation 3 (`defaultContentKey`): "chave:classe". */
    private fun contentKeyOf(key: Any): String = "$key:${key::class}"
}
