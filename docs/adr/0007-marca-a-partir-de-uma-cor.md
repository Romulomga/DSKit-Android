# 0007 — Marca a partir de uma cor, num módulo opcional

**Data:** 2026-10 · **Situação:** aceita

## Contexto

O `DSBrand` pede os esquemas exportados do Material Theme Builder ([0004](0004-cor-da-marca.md)). Para um app com
muitas marcas (white label) ou com a cor vinda de configuração, exportar à mão não escala, e usar a cor da marca
direto quebra o contraste (texto branco sobre verde neon).

O cálculo do Material (HCT, paletas tonais, esquemas dinâmicos) está na `material-color-utilities` do Google, que
não sai como AndroidX. O MaterialKolor a publica em Kotlin, mas o artefato principal dele traz o Material 3 do
Compose Multiplatform numa versão alpha, que puxaria o Material 3 do app para essa alpha.

## Decisão

O módulo opcional `seed` depende só de `com.materialkolor:material-color-utilities` (o algoritmo, licença MIT, sem
Compose) e faz ele mesmo a conversão para o `ColorScheme` do Compose, com a especificação de 2021, a do Material 3
estável. `DSBrand.fromSeed(cor, style)` gera os seis esquemas (claro e escuro em contraste padrão, médio e alto) e
as cores de status harmonizadas com a marca, nos mesmos tons do `DSStatusColors`.

## Consequências

- Quem não usa o `seed` não recebe a dependência; `tokens` e `components` continuam só AndroidX.
- O teste do módulo confere o contraste dos papéis principais em dez cores, inclusive preto, branco e verde neon.
- Atualizar o MaterialKolor é revisar a dependência dele (hoje só a biblioteca padrão do Kotlin e anotações).
