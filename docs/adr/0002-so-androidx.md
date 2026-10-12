# 0002 — Só dependências AndroidX

**Data:** 2026-10 · **Situação:** aceita

## Contexto

Uma lib de componentes entra em todos os apps que a usam; cada dependência dela vira dependência deles, com versão
e licença.

## Decisão

O DSKit depende só de AndroidX/Compose. Exceção é possível, mas é decidida caso a caso, com o custo à vista. Já
avaliadas: Coil (carregar imagem remota, aceito se um componente precisar), MaterialKolor (gerar o esquema de cor a
partir de uma semente, recusado: o Theme Builder exporta o esquema), Dokka e Showkase (ferramentas de
documentação, recusadas: o KDoc e o app `sample` cobrem) e o plugin de publicação no Maven Central (adiado: o app inclui o DSKit como
submódulo git, sem servidor de pacotes).

## Consequências

- Ferramenta de build que já vem com o Kotlin ou o AGP (relatórios do compilador do Compose) não conta como
  dependência nova.
