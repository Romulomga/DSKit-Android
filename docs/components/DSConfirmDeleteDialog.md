# DSConfirmDeleteDialog

A confirmação de uma exclusão, sobre o [`DSAlertDialog`](DSAlertDialog.md) (`AlertDialog` do Material 3): a pergunta, o botão que exclui na cor de erro e o "Cancelar".

## Quando usar

Antes de toda exclusão que não tem desfazer. O gatilho (o menu da linha, o arrastar, o botão do editor) guarda o item que vai sair; o diálogo aparece enquanto houver item em espera, entrega-o ao confirmar e fecha.

- Para outras confirmações destrutivas ("Sair da conta"), use o `DSAlertDialog` com `destructive = true`.
- Para perguntar antes de perder o que foi digitado, use o [`DSDiscardGuard`](DSDiscardGuard.md).

## Uso

```kotlin
var pendingDelete by rememberSaveable { mutableStateOf<String?>(null) }

DSConfirmDeleteDialog(
    pending = pendingDelete,
    title = "Excluir cliente?",
    message = "Os recibos deste cliente continuam no histórico.",
    confirmText = "Excluir cliente",
    onDelete = viewModel::delete,
    onDismiss = { pendingDelete = null }
)
```

Dentro de um editor, que não tem um item em espera:

```kotlin
DSConfirmDeleteDialog(
    visible = confirmsDelete,
    title = "Excluir dose?",
    confirmText = "Excluir dose",
    onDelete = viewModel::delete,
    onDismiss = { confirmsDelete = false }
)
```

## Parâmetros

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `pending` | `T?` | obrigatório | O que será excluído (o item ou o id); `null` esconde o diálogo. Na outra forma, `visible: Boolean`. |
| `title` | `String` | obrigatório | A pergunta. |
| `confirmText` | `String` | obrigatório | O botão que exclui. Nomeie o que sai ("Excluir cliente"). |
| `onDelete` | `(T) -> Unit` | obrigatório | Chamado ao confirmar, com o item em espera. Na forma com `visible`, `() -> Unit`. |
| `onDismiss` | `() -> Unit` | obrigatório | Chamado depois de `onDelete`, e sozinho ao cancelar, tocar fora ou voltar. Deve zerar o `pending`. |
| `modifier` | `Modifier` | `Modifier` | Modificador do diálogo. |
| `message` | `String?` | `null` | O que se perde junto. |
| `dismissText` | `String` | "Cancelar" | Rótulo de cancelar (string da lib, nas 10 línguas). |

## Comportamento

- Confirmar vibra o aviso uma vez (o do `DSAlertDialog` destrutivo). Quem chama não vibra de novo.
- Guarde o `pending` num `rememberSaveable` para a pergunta sobreviver à rotação.

## Acessibilidade

- É um `DSAlertDialog`: janela modal, o foco do TalkBack fica nela até ser fechada.

## Screenshots

`overlays_confirm_delete_dialog`:

![](../../components/screenshots/overlays_confirm_delete_dialog_light.png)

Também existe `overlays_confirm_delete_dialog_dark`.
