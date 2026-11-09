# DSTextInputDialog

Diálogo do Material 3 (`AlertDialog`) com um campo de texto: título, mensagem opcional, um [`DSTextField`](DSTextField.md) já focado e as duas ações.

## Quando usar

Para pedir um texto curto sem sair da tela: dar um nome, criar uma etiqueta, renomear um item.

- Para mais de um campo, use um [`DSBottomSheet`](DSBottomSheet.md) ou uma tela.
- Para uma pergunta sem campo, use o [`DSAlertDialog`](DSAlertDialog.md).

Quem chama guarda o texto e a visibilidade, e fecha o diálogo em `onConfirm` e em `onDismissRequest`.

## Uso

```kotlin
var name by rememberSaveable { mutableStateOf("") }

if (addingTag) {
    DSTextInputDialog(
        title = "Nova etiqueta",
        message = "As etiquetas agrupam suas publicações.",
        value = name,
        onValueChange = { name = it },
        label = "Nome",
        confirmText = "Adicionar",
        confirmEnabled = name.isNotBlank(),
        onConfirm = {
            addingTag = false
            viewModel.addTag(name)
        },
        onDismissRequest = { addingTag = false }
    )
}
```

## Parâmetros

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `title` | `String` | obrigatório | O título. |
| `value` | `String` | obrigatório | O texto do campo. |
| `onValueChange` | `(String) -> Unit` | obrigatório | Chamado a cada edição. |
| `label` | `String` | obrigatório | Rótulo do campo; é também o que o TalkBack lê. |
| `confirmText` | `String` | obrigatório | Rótulo da confirmação. |
| `onConfirm` | `() -> Unit` | obrigatório | O botão de confirmar e a ação "Concluído" do teclado. Não fecha o diálogo sozinho. |
| `onDismissRequest` | `() -> Unit` | obrigatório | O botão de cancelar e o voltar. Tocar fora não chama. |
| `modifier` | `Modifier` | `Modifier` | Modificador do diálogo. |
| `message` | `String?` | `null` | Texto entre o título e o campo. |
| `placeholder` | `String?` | `null` | Dica dentro do campo vazio. |
| `errorText` | `String?` | `null` | Erro abaixo do campo. |
| `confirmEnabled` | `Boolean` | `true` | Desligado, nem o botão nem a ação do teclado confirmam. |
| `dismissText` | `String` | "Cancelar" | Rótulo de cancelar (string da lib, nas 10 línguas). |
| `keyboardOptions` | `KeyboardOptions` | primeira letra maiúscula | O teclado do campo. A ação é sempre `ImeAction.Done`. |
| `fieldTestTag`, `confirmTestTag`, `dismissTestTag` | `String?` | `null` | `testTag` do campo e dos botões, para os testes de UI do app. |

## Comportamento

- Tocar fora não fecha: um toque ao lado do teclado apagaria o que foi digitado.
- A ação do teclado confirma, como o botão.
- O campo recebe o foco ao abrir, e o teclado sobe.

## Acessibilidade

- O `AlertDialog` do Material 3 é uma janela modal: o foco do TalkBack fica dentro dele até ser fechado.
- O TalkBack lê o `label` do campo e anuncia o `errorText`.

## Screenshots

`overlays_text_input_dialog`:

![](../../components/screenshots/overlays_text_input_dialog_light.png)

Também existe `overlays_text_input_dialog_dark`.
