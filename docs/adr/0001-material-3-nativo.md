# 0001 — Material 3 nativo

**Data:** 2026-10 · **Situação:** aceita

## Contexto

A primeira versão do DSKit Android copiava o DSKit iOS: título grande no conteúdo, ajustes agrupados com chevron,
escala de tipo do iOS, toast no topo. No Android isso parece um app estrangeiro e briga com o que o sistema já
oferece (barra superior que encolhe, navegação que vira lateral em tablet, Material You).

## Decisão

Cada componente do DSKit é um componente do Material 3 estável com os padrões do app (vibração, carregando,
destrutivo, acessibilidade, API curta). Onde o Material não tem a peça (esqueleto, estado vazio, banner), o DSKit a
desenha com os papéis de cor, as formas e a tipografia do Material. O DSKit iOS e o Homer iOS servem para saber
**quais** peças um app precisa, nunca **como** elas parecem.

## Consequências

- O visual de um app iOS e de um app Android com a mesma marca diverge de propósito.
- Atualizar o Material atualiza o DSKit; o DSKit não mantém desenho próprio do que o Material já desenha.
- Material 3 Expressive fica para quando sair do alpha.
