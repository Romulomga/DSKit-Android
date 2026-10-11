# DSAlertDialog

Diálogo de alerta do Material 3 (`AlertDialog`): título, mensagem opcional, a ação de confirmar e, se houver, a de cancelar. Os botões são [`DSTextButton`](DSButton.md)s.

## Quando usar

Para confirmar o que não dá para desfazer ou para um erro que pede decisão.

- Para avisos que não pedem resposta, use um snackbar ([`showDSSnackbar`](DSSnackbar.md)).
- Para escolher entre várias ações, use um [`DSBottomSheet`](DSBottomSheet.md) com `DSSheetAction`s.

Quem chama controla a visibilidade: mostre o diálogo enquanto um estado estiver ligado e desligue-o em `onDismissRequest` e depois de `onConfirm`.

## Uso

```kotlin
var confirmDelete by remember { mutableStateOf(false) }

if (confirmDelete) {
    DSAlertDialog(
        title = "Excluir publicação?",
        message = "A publicação sai do feed e seus parceiros deixam de vê-la.",
        confirmText = "Excluir",
        dismissText = "Cancelar",
        destructive = true,
        onConfirm = {
            confirmDelete = false
            delete()
        },
        onDismissRequest = { confirmDelete = false }
    )
}
```

## Parâmetros

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `title` | `String` | obrigatório | O título. |
| `confirmText` | `String` | obrigatório | Rótulo da confirmação. |
| `onConfirm` | `() -> Unit` | obrigatório | Chamado no toque da confirmação. Não fecha o diálogo sozinho. |
| `onDismissRequest` | `() -> Unit` | obrigatório | Chamado ao tocar fora, no voltar e no botão de cancelar. |
| `modifier` | `Modifier` | `Modifier` | Modificador do diálogo. |
| `message` | `String?` | `null` | Texto abaixo do título. |
| `dismissText` | `String?` | `null` | Rótulo de cancelar; `null` deixa só a confirmação. |
| `icon` | `ImageVector?` | `null` | Ícone opcional acima do título, que então fica centralizado. |
| `destructive` | `Boolean` | `false` | Pinta a confirmação na cor de erro ("Excluir", "Sair da conta") e vibra em tom de aviso ao confirmar. |

## Acessibilidade

- O ícone é decorativo; o TalkBack lê título e mensagem.
- O `AlertDialog` do Material 3 é uma janela modal: o foco do TalkBack fica dentro dele até ser fechado.

## Screenshots

`overlays_alert_dialog`:

![](../../components/screenshots/overlays_alert_dialog_light.png)

Também existe `overlays_alert_dialog_dark`.
