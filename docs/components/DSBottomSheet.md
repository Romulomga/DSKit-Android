# DSBottomSheet

Painel modal que sobe da base da tela, o `ModalBottomSheet` do Material 3. Serve para escolhas rápidas, um formulário curto, as ações de um item. O arquivo também traz `DSSheetAction`, uma linha de ação (`ListItem` do Material 3) para esses painéis.

## Quando usar

- `DSBottomSheet`: para conteúdo temporário ligado à tela atual. Abre na altura do conteúdo: um painel curto não para no meio da tela. Conteúdo maior que a tela, como em fonte grande, rola. Trata a barra de navegação do sistema.
- `DSSheetAction`: cada ação de um painel de ações (editar, compartilhar, excluir). Ícone e rótulo, a linha inteira tocável. `destructive` pinta na cor de erro.

Para confirmar algo que não dá para desfazer, use o [`DSAlertDialog`](DSAlertDialog.md).

O painel é declarativo: `visible` abre e fecha, com a animação nos dois sentidos. Ao virar `false`, o painel desce antes de sair da tela.

## Uso

```kotlin
var showOptions by remember { mutableStateOf(false) }

DSBottomSheet(visible = showOptions, onDismissRequest = { showOptions = false }, title = "Opções") {
    DSSheetAction("Editar", onClick = { showOptions = false; edit() }, icon = DSIcons.Edit)

    DSSheetAction("Compartilhar", onClick = { showOptions = false; share() }, icon = DSIcons.Share)

    DSSheetAction("Excluir", onClick = { showOptions = false; delete() }, icon = DSIcons.Delete, destructive = true)
}
```

## Parâmetros

`DSBottomSheet`

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `visible` | `Boolean` | obrigatório | Se o painel está aberto. Ao virar `false`, o painel desce antes de sair da tela. |
| `onDismissRequest` | `() -> Unit` | obrigatório | O usuário arrastou para baixo, tocou fora ou usou o voltar: desligue `visible`. |
| `modifier` | `Modifier` | `Modifier` | Modificador do painel. |
| `title` | `String?` | `null` | Título opcional no topo do painel. |
| `scrollable` | `Boolean` | `true` | O painel rola o conteúdo inteiro. Passe `false` quando o conteúdo já rola sozinho (uma `LazyColumn` longa). |
| `onDismissBlocked` | `(() -> Unit)?` | `null` | Com ele, arrastar para baixo e tocar no véu não fecham o painel e chamam este lambda. O voltar continua em `onDismissRequest`. |
| `content` | `@Composable ColumnScope.() -> Unit` | obrigatório | O conteúdo, numa coluna. |

A outra forma troca `visible` por `state: DSBottomSheetState` (de `rememberDSBottomSheetState()`): o painel fica aberto enquanto estiver na composição, e quem o tira chama antes `state.hide()` para ele descer. É a base do `DSBottomSheetSceneStrategy` do módulo opcional [`navigation3`](DSBottomSheetSceneStrategy.md).

## Painel com algo digitado

```kotlin
val guard = rememberDSDiscardGuardState(onExit = { editing = false })
val hasChanges = rememberDSHasChanges(name)

DSDiscardGuard(guard, hasChanges)

DSBottomSheet(
    visible = editing,
    onDismissRequest = guard::requestExit,
    onDismissBlocked = if (hasChanges) guard::requestExit else null,
    title = "Novo cliente"
) { … }
```

Com mudança, o gesto de fechar não desce o painel: abre a pergunta do [`DSDiscardGuard`](DSDiscardGuard.md).

## Comportamento

- Fechado por código (`visible = false` ou `state.hide()`), o painel desce em 250 ms com o véu apagando junto (`DSMotion.exit()`). A saída do Material chamada por código dura um instante e parece um corte.
- O voltar do sistema chama `onDismissRequest` direto, e quem chama decide: fechar ou perguntar antes.

`DSSheetAction`

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `text` | `String` | obrigatório | O rótulo da ação. |
| `onClick` | `() -> Unit` | obrigatório | Chamado no toque. Não fecha o painel sozinho. |
| `modifier` | `Modifier` | `Modifier` | Modificador da linha. |
| `icon` | `ImageVector?` | `null` | Ícone decorativo à esquerda. |
| `destructive` | `Boolean` | `false` | Pinta rótulo e ícone na cor de erro. |
| `enabled` | `Boolean` | `true` | Desabilitada, a linha fica esmaecida e não recebe toque. |

## Acessibilidade

- O título do painel é marcado como `heading()`.
- Cada `DSSheetAction` é um `clickable` com `Role.Button`; o ícone é decorativo.
- A alça é lida "Fechar" pelo TalkBack (string da lib, nas 10 línguas), e tocar nela com o leitor fecha o painel. A do Material é lida "Fechar planilha" em português.
- O painel fecha com o voltar do sistema.

## Screenshots

`overlays_bottom_sheet`:

![](../../components/screenshots/overlays_bottom_sheet_light.png)

Também existe `overlays_bottom_sheet_dark`.

`lists_sheet_actions` (as linhas `DSSheetAction` soltas):

![](../../components/screenshots/lists_sheet_actions_light.png)

Também existem `lists_sheet_actions_dark`, `_font200`, `_rtl` e `_highcontrast`.
