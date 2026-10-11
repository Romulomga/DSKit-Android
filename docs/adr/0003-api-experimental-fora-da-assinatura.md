# 0003 — API experimental do Material fora da assinatura pública

**Data:** 2026-10 · **Situação:** aceita

## Contexto

Várias peças do Material 3 1.4 ainda são `@ExperimentalMaterial3Api`: `ModalBottomSheet`, `TopAppBarScrollBehavior`,
`ExposedDropdownMenuBox`, `TooltipBox`, `SearchBar`. Se um tipo experimental aparece num parâmetro público do
DSKit, todo app precisa de `@OptIn` para chamar o componente.

## Decisão

O DSKit usa essas APIs com `@OptIn` dentro do componente e expõe algo estável: `DSBottomSheet(visible = …)` no
lugar de `SheetState`, `DSTopAppBarStyle` no lugar do `scrollBehavior`, `DSTooltip(text = …)` no lugar do
`TooltipState`.

## Consequências

- Quem precisa do controle fino (arrastar o painel até a metade) usa o componente do Material direto e suprime o lint.
- Quando a API ficar estável, o DSKit pode expor mais sem quebrar o que existe.
