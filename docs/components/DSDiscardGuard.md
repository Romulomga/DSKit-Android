# DSDiscardGuard

Guarda de descarte: pergunta antes de sair de uma tela ou painel com algo digitado. É um [`DSAlertDialog`](DSAlertDialog.md) destrutivo ligado ao voltar do sistema (`BackHandler`) e a um estado que a tela usa na seta da barra, no "Cancelar" e no `onDismissRequest` de um painel. O arquivo traz também `rememberDSHasChanges`, que compara os campos com o que eles eram quando a tela abriu, e `DSDiscardDialog`, só a pergunta.

## Quando usar

- `DSDiscardGuard`: em toda tela ou painel de edição. Sem mudança, sair fecha direto; com mudança, pergunta "Descartar as alterações?".
- `rememberDSHasChanges(snapshot)`: quando a tela não tem um "mudou?" pronto. Passe um valor com igualdade que reúna os campos editáveis.
- `DSDiscardDialog`: quando a visibilidade da pergunta já mora em outro lugar (um ViewModel que decide se há o que perder).

Para confirmar uma exclusão, use o [`DSConfirmDeleteDialog`](DSConfirmDeleteDialog.md).

## Uso

```kotlin
val guard = rememberDSDiscardGuardState(onExit = onBack)

DSDiscardGuard(guard, hasChanges = rememberDSHasChanges(Triple(name, phone, notes)))

DSScreen(title = "Editar cliente", onBack = guard::requestExit) { padding ->
    DSScreenColumn(padding) {
        DSTextField(value = name, onValueChange = { name = it }, label = "Nome")

        DSTextButton("Cancelar", onClick = guard::requestExit)
    }
}
```

Num painel, o gesto de fechar também passa pela guarda:

```kotlin
DSBottomSheet(
    visible = editing,
    onDismissRequest = guard::requestExit,
    onDismissBlocked = if (hasChanges) guard::requestExit else null
) { … }
```

Só a pergunta, com o estado por conta de quem chama:

```kotlin
if (state.confirmsDiscard) {
    DSDiscardDialog(onDiscard = viewModel::discard, onKeepEditing = viewModel::dismissDiscard)
}
```

## Parâmetros

`rememberDSDiscardGuardState`

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `onExit` | `() -> Unit` | obrigatório | O que sair faz (fechar o painel, voltar na pilha). Vale sempre o lambda mais recente. |

Devolve um `DSDiscardGuardState`: `requestExit()` sai ou pergunta; `isConfirming` diz se a pergunta está aberta.

`DSDiscardGuard`

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `state` | `DSDiscardGuardState` | obrigatório | O estado da guarda. |
| `hasChanges` | `Boolean` | obrigatório | Se há algo a perder. Com `true`, o voltar do sistema pergunta. |
| `modifier` | `Modifier` | `Modifier` | Modificador do diálogo. |
| `title` | `String` | "Descartar as alterações?" | A pergunta. |
| `message` | `String?` | `null` | Detalhe abaixo da pergunta. |
| `discardText` | `String` | "Descartar" | O botão que sai, na cor de erro. |
| `keepEditingText` | `String` | "Continuar editando" | O botão que fica. Tocar fora também fica. |

`rememberDSHasChanges`

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `snapshot` | `T` | obrigatório | Um valor com igualdade que muda quando um campo muda e que caiba num `Bundle` (números, texto, `java.time`, enums e `Pair`, `Triple`, listas ou conjuntos deles). |

`DSDiscardDialog`

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `onDiscard` | `() -> Unit` | obrigatório | O botão de descartar. |
| `onKeepEditing` | `() -> Unit` | obrigatório | O botão de continuar, o toque fora e o voltar. |
| `modifier`, `title`, `message`, `discardText`, `keepEditingText` | | | Como no `DSDiscardGuard`. |

## Comportamento

- A pergunta aberta fica num `rememberSaveable`: sobrevive à troca de tema, fonte, idioma, à rotação e à morte do processo.
- O retrato de partida do `rememberDSHasChanges` é lido depois da primeira composição (os valores que a tela preenche ao abrir não contam) e também fica salvo. Recriada a Activity, ele continua sendo o de quando a tela abriu, e não o valor já editado, o que tiraria a pergunta e descartaria a edição sem aviso.
- Descartar vibra o aviso uma vez (o do `DSAlertDialog` destrutivo).

## Acessibilidade

- É um `DSAlertDialog`: janela modal, o foco do TalkBack fica nela até ser fechada.

## Screenshots

`overlays_discard_dialog`:

![](../../components/screenshots/overlays_discard_dialog_light.png)

Também existe `overlays_discard_dialog_dark`.
