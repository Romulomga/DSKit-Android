# DSDatePickerDialog

O calendário do Material 3 (`DatePickerDialog` com o `DatePicker` dentro) falando `LocalDate`: abre num dia e o "OK" entrega o dia escolhido.

## Quando usar

Para escolher um dia: emissão, vencimento, nascimento, a data de um registro. Quem chama desenha o gatilho, que muda de app para app (uma linha de lista com a data, um botão de texto, uma pílula), e controla a visibilidade.

O DSKit não embrulha a escolha de hora (`TimePicker`) nem a de um período (`DateRangePicker`): use os do Material, que já saem com a marca.

## Uso

```kotlin
var pickingDate by rememberSaveable { mutableStateOf(false) }

DSListItem(headline = formatted, overline = "Emissão", onClick = { pickingDate = true })

if (pickingDate) {
    DSDatePickerDialog(
        date = issuedOn,
        onConfirm = {
            issuedOn = it
            pickingDate = false
        },
        onDismissRequest = { pickingDate = false },
        range = LocalDate.of(2020, 1, 1)..LocalDate.now()
    )
}
```

## Parâmetros

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `date` | `LocalDate?` | obrigatório | O dia selecionado ao abrir. `null` abre no mês de hoje sem nada escolhido, e o "OK" espera a escolha. |
| `onConfirm` | `(LocalDate) -> Unit` | obrigatório | O "OK", com o dia escolhido. Não fecha o diálogo sozinho. |
| `onDismissRequest` | `() -> Unit` | obrigatório | O "Cancelar", o toque fora e o voltar. |
| `modifier` | `Modifier` | `Modifier` | Modificador do diálogo. |
| `range` | `ClosedRange<LocalDate>?` | `null` | Faixa válida; fora dela o dia e o ano não se escolhem. |
| `showModeToggle` | `Boolean` | `true` | Mostra o lápis que troca o calendário pela digitação da data. |
| `confirmText` | `String` | "OK" | Rótulo de confirmar (string da lib, nas 10 línguas). |
| `dismissText` | `String` | "Cancelar" | Rótulo de cancelar (string da lib, nas 10 línguas). |

## Comportamento

- O Material trabalha com a meia-noite UTC do dia em milissegundos; a conversão de e para `LocalDate` fica dentro do componente. Para um `Instant`, converta no fuso do app antes (`instant.atZone(zone).toLocalDate()`).
- Os anos mostrados ficam sempre entre 1900 e 2100, qualquer que seja o `range`: o calendário monta um mês para cada ano da faixa, e uma faixa como `LocalDate.MIN..LocalDate.now()` estourava a conta e fechava o app.
- A API experimental do Material (`DatePickerState`, `SelectableDates`) não aparece na assinatura.

## Acessibilidade

- O calendário do Material anuncia cada dia por extenso e marca os dias fora da faixa como desabilitados.
- Com o lápis (`showModeToggle`), a data pode ser digitada, o que ajuda quem usa teclado ou leitor de tela.

## Screenshots

`overlays_date_picker_dialog`:

![](../../components/screenshots/overlays_date_picker_dialog_light.png)

Também existe `overlays_date_picker_dialog_dark`.
