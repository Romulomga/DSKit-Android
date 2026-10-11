# Convenções do DSKit Android

O DSKit Android é um design system **nativo do Android**: cada componente é um componente do Material 3 com os
padrões do app, não um desenho próprio. Quando o Material tem a peça, o DSKit a embrulha; quando não tem
(esqueleto, estado vazio, véu de carregamento), o DSKit a desenha com os papéis de cor e a tipografia do Material.

Outros design systems (o DSKit iOS, o do Homer iOS) servem de fonte de ideias sobre **quais** peças um app precisa,
nunca de **como** elas parecem ou se comportam. Nada de título grande no conteúdo, ajustes agrupados com chevron,
escala de tipo do iOS, níveis de superfície ou toast no topo: o equivalente Android é a `TopAppBar` grande, o
`ListItem`, a escala do Material e o snackbar.

## Onde fica

- Um arquivo por componente (ou família pequena, como os quatro botões) em
  `components/src/main/kotlin/io/github/romulomga/dskit/<grupo>/<Nome>.kt` (`buttons/`, `cards/`, `controls/`,
  `feedback/`, `inputs/`, `layout/`, `lists/`, `navigation/`, `overlays/`). O pacote é sempre
  `io.github.romulomga.dskit`, sem subpacote: quem usa importa tudo de um lugar.
- `explicitApi()` está ligado: toda declaração pública leva `public`; o que não é API é `internal` ou `private`.
- Ícone que falte em `DSIcons`: adicione lá, com o nome e o caminho do Material Icons (`autoMirror` nos de direção).

## Como escrever um componente

- Comece pelo componente do Material e mude só o necessário. Cores, formas, alturas e paddings ficam os do Material,
  vindos do `DSTheme` (`DSTheme.colors`, `DSTheme.typography`, `DSTheme.shapes`); não redesenhe o que o Material já
  desenha.
- API: parâmetros obrigatórios, depois `modifier: Modifier = Modifier`, depois opcionais, por último o lambda de
  conteúdo. Texto como `String` (o app passa `stringResource`), ícone como `ImageVector`, estado como
  `value` + `onValueChange`, desabilitado por `enabled: Boolean = true`. Nomes do Material quando existem
  (`DSTonalButton`, `DSFilterChip`, `supportingText`, `label`).
- **API experimental do Material nunca na assinatura pública** (`SheetState`, `TopAppBarScrollBehavior`…): use com
  `@OptIn` dentro do componente e exponha algo estável (`visible: Boolean`, o estilo da barra). Senão todo app
  precisaria de `@OptIn` para usar o DSKit.
- Vibração: `val haptics = rememberDSHaptics()` e `haptics.light()` no toque; `warning()` em ações destrutivas.
- Strings da lib em `components/src/main/res/values*/strings.xml`, nas 10 línguas, com prefixo `dskit_`.
- Remover ou renomear algo público é quebra: anote em `CHANGELOG.md`, com o código de migração.
- Componente novo ganha uma página em `docs/components/` e entra no índice e no mapa do lint
  (`DSComponentDetector.REPLACEMENTS`), se embrulha um componente do Material.

## Regras

- Só tokens: nada de cor, fonte, espaçamento, sombra ou duração literais. Cor vem de `DSTheme.colors` ou
  `DSTheme.status`; texto de `DSTheme.typography` (escala com a fonte do sistema); animação de `DSMotion`.
- Acessibilidade: alvo mínimo de 48 dp em tudo que é tocável (os componentes do Material já garantem); `Role` certo
  quando a linha inteira é o controle (`Role.Switch`, `Role.Checkbox`, `Role.RadioButton`) e o controle desenhado
  com `onCheckedChange = null`; ícone decorativo com `contentDescription = null`; controle só com ícone exige
  `contentDescription` no parâmetro, não opcional.
- Cada arquivo termina com previews `@DSPreviews` dentro de `DSPreviewContainer` (claro, escuro, fonte 200 %),
  cobrindo os estados (normal, desabilitado, carregando, erro…).
- Cada grupo tem um teste de screenshot em `components/src/test/kotlin/io/github/romulomga/dskit/<Grupo>ScreenshotTest.kt`
  que estende `ScreenshotTestBase` e chama `snapshot("<grupo>_<componente>") { … }`, que captura cinco modos:
  claro, escuro, fonte 200 %, da direita para a esquerda (`_rtl`) e alto contraste (`_highcontrast`). Confira o
  `_rtl`: ícone de direção espelha, texto alinha à direita, nada de `padding(left=…)`. Animação infinita (indicador,
  esqueleto) vai no `AnimatedScreenshotTest` pelo `stillSnapshot`; diálogo e painel, que abrem em janela própria, no
  `DialogScreenshotTest`.
- O `snapshot` também roda o Accessibility Test Framework do Google no claro e no escuro (alvo de toque, contraste,
  rótulo legível pelo TalkBack) e falha em erro. Exceção real vai em `accessibilitySuppressions`, com o motivo ao lado.
- KDoc em português nas declarações públicas, dizendo para que serve e quando usar (e quando usar outro componente).
