# DSBottomSheetSceneStrategy

Estratégia de cena do Navigation 3 que mostra num [`DSBottomSheet`](DSBottomSheet.md) a entrada do topo da pilha marcada como painel, por cima das telas de baixo. Fica no módulo opcional `navigation3`, para o app que não usa o Navigation 3 não receber a dependência. O arquivo traz também `DSNavSheets`, que faz o painel descer antes de a entrada sair da pilha.

## Instalação

```kotlin
implementation("com.github.Romulomga.DSKit-Android:navigation3:<versão>")
```

O módulo traz o `components` e o Navigation 3 (`navigation3-runtime` e `navigation3-ui`, a partir da 1.2.0; o app que pede uma versão mais nova fica com a dele).

## Quando usar

Quando uma tela da navegação abre num painel inferior (filtros, um editor curto, as ações de um item com tela própria) e precisa de chave na pilha, resultado e voltar como qualquer outra tela. Para um painel que é só estado da tela atual, use o `DSBottomSheet(visible = …)`.

## Uso

```kotlin
NavDisplay(
    backStack = backStack,
    onBack = navigator::goBack,
    sceneStrategies = listOf(DSBottomSheetSceneStrategy(), DialogSceneStrategy()),
    entryProvider = entryProvider {
        entry<FiltersKey>(metadata = DSBottomSheetSceneStrategy.bottomSheet()) { FiltersScreen() }

        entry<CountryKey>(metadata = DSBottomSheetSceneStrategy.bottomSheet(scrollable = false)) { CountryList() }
    }
)
```

Ponha a estratégia antes das que não sobrepõem. No ponto do app que tira entradas da pilha, passe a remoção pelo `DSNavSheets`:

```kotlin
val scope = rememberCoroutineScope()
val navigator = remember { Navigator(state, beforeRemove = { key, then -> DSNavSheets.hideThen(scope, key, then) }) }
```

## API

| Declaração | Descrição |
| --- | --- |
| `DSBottomSheetSceneStrategy<T>()` | A estratégia, para o `sceneStrategies` do `NavDisplay`. |
| `DSBottomSheetSceneStrategy.bottomSheet(scrollable: Boolean = true)` | O metadado da entrada que abre num painel. `scrollable = false` quando a tela já rola sozinha (ver o `scrollable` do `DSBottomSheet`). |
| `DSBottomSheetSceneStrategy.isBottomSheet(metadata)` | Se o metadado de uma entrada a marca como painel. |
| `DSNavSheets.hideThen(scope, key, then)` | O painel de `key` desce e `then` roda depois. Sem painel aberto para a chave, ou com ele já fechado pelo gesto, `then` roda na hora. Chamado de novo enquanto desce (toque duplo), não faz nada. |

## Comportamento

- O gesto de fechar, o toque no véu, a alça com o TalkBack e o voltar do sistema chamam o `onBack` do `NavDisplay`.
- Tirada da pilha, a entrada sai da composição na hora. Com o `hideThen`, o painel desce antes em 250 ms com o véu apagando junto.
- A cena é uma `OverlayScene`: as telas de baixo continuam compostas e visíveis atrás do véu.
- O conteúdo da entrada desenha o próprio título; a cena não põe título no painel.

## Acessibilidade

- A do `DSBottomSheet`: a alça é lida "Fechar", e tocar nela com o leitor volta na pilha.

## Testes

O módulo não tem screenshot próprio (o painel é o `overlays_bottom_sheet` do `DSBottomSheet`). O `BottomSheetSceneStrategyTest` abre um `NavDisplay` de verdade e confere a cena, o voltar e o `hideThen`.
