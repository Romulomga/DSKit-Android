# 0005 — Screenshot em cinco modos com o Accessibility Test Framework

**Data:** 2026-10 · **Situação:** aceita

## Contexto

Um design system quebra de jeitos que teste de unidade não pega: texto cortado com fonte grande, ícone que não
espelha em árabe, contraste que cai no tema escuro, controle sem rótulo para o TalkBack.

## Decisão

Todo componente tem um `snapshot` (Roborazzi + Robolectric, sem emulador) em claro, escuro, fonte 200 %, da direita
para a esquerda e alto contraste. O mesmo `snapshot` roda o Accessibility Test Framework do Google no claro e no
escuro e falha em erro. As imagens ficam no repositório e o `verifyRoborazziDebug` compara.

## Consequências

- Diálogo e painel, que abrem em janela própria, só são capturados em claro e escuro: o Robolectric não passa a
  direção da direita para a esquerda para a janela deles.
- O alto contraste usa um esquema de teste feito à mão (`HighContrastTestBrand`), porque o esquema base do
  Material não traz um.
