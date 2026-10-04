# Convenções do DSKit Android

O DSKit Android é o irmão do DSKit iOS (`~/DSKit`, tag 1.0.37). Cada componente é o porte do arquivo Swift de
mesmo nome: mesmo nome, mesmos parâmetros traduzidos, mesmo visual, mesmos estados.

## Onde fica

- Um arquivo por componente em `components/src/main/kotlin/io/github/romulomga/dskit/<grupo>/<Nome>.kt`
  (`buttons/`, `cards/`, `inputs/`, `feedback/`, `layout/`, `controls/`, `monetization/`). O pacote é sempre
  `io.github.romulomga.dskit`, sem subpacote: quem usa importa tudo de um lugar, como o `import DSKit`.
- `explicitApi()` está ligado: toda declaração pública leva `public`; o que não é API é `internal` ou `private`.
- Não mexer em `tokens/` nem em `utilities/`. Ícone que falte em `DSIcons`: declare `private` no seu arquivo,
  com o caminho do Material Icons.

## Tradução da API do SwiftUI

| SwiftUI (DSKit iOS) | Compose (DSKit Android) |
|---|---|
| `title: LocalizedStringKey` | `title: String` (o app passa `stringResource`) |
| `systemImage: String?` | `icon: ImageVector?` |
| `@Binding var x` | `value: T` + `onValueChange: (T) -> Unit` |
| `.disabled(true)` / `isEnabled` | parâmetro `enabled: Boolean = true` |
| `@ViewBuilder content` | `content: @Composable () -> Unit` (ou `ColumnScope.() -> Unit` quando empilha) |
| `action: () -> Void` | `onClick: () -> Unit` |
| ordem | parâmetros obrigatórios, depois `modifier: Modifier = Modifier`, depois opcionais, por último o lambda de conteúdo |
| `Color.surface`, `Color.accent`… | `DSTheme.colors.surface`, `DSTheme.colors.accent` |
| `theme.primary` | `DSTheme.primary` |
| `DSTypography.headline()` | `DSTypography.headline` |
| `DSSpacing.lg`, `DSRadius.lg` | os mesmos nomes (`Dp`); formas prontas em `DSRadius.large`, `DSRadius.capsule` |
| `.dsShadow(.card)` | `Modifier.dsShadow(DSShadow.card, shape)` |
| `DSMotion.snappy` | `DSMotion.snappy()` (`AnimationSpec`) |
| `@Environment(\.dsSurfaceLevel)` | `DSTheme.surfaceLevel`; pinte `DSTheme.colors.surface(DSTheme.surfaceLevel)` e embrulhe o conteúdo em `DSSurfaceContainer { }` |
| `accessibilityReduceMotion` | `DSTheme.reduceMotion` (troque movimento por crossfade ou nada) |
| `DSHaptics.medium(if: hapticsEnabled)` | `val haptics = rememberDSHaptics()` e `haptics.medium()` |
| `DSPressableButtonStyle` | `Modifier.dsPressScale(interactionSource)` junto com o ripple |
| `@Environment(\.dsFieldLabelStyle)` | `LocalDSFieldLabelStyle.current` |
| `ProgressView()` | `CircularProgressIndicator` do Material 3 com cor de token e tamanho do texto |
| `.strokeBorder(Color.hairline, lineWidth: 0.75)` | `Modifier.border(0.75.dp, DSTheme.colors.hairline, shape)` |
| strings do `Localizable.xcstrings` | `stringResource(R.string.dskit_…)` (`io.github.romulomga.dskit.components.R`) |

## Regras

- Só tokens: nada de cor, fonte, espaçamento, raio, sombra ou duração literais. Exceções que o iOS também tem
  (altura mínima de botão = `DSTouchTarget.button`, linha de 0,75 dp) ficam como no Swift.
- Texto sempre em `DSTypography` (escala com a fonte do sistema). Sem altura fixa em linha de texto: use
  `heightIn(min = …)`.
- Acessibilidade: alvo mínimo de 48 dp (`DSTouchTarget.minimum`) em tudo que é tocável; `Role` certo
  (`Role.Button`, `Role.Switch`, `Role.RadioButton`, `Role.Checkbox`, `Role.Tab`); estado por `selected`,
  `stateDescription` ou `toggleableState`; cards e linhas com `mergeDescendants`; ícone decorativo com
  `contentDescription = null`; controle só com ícone exige `contentDescription` no parâmetro, não opcional.
- Nativo do Android: ripple nos tocáveis (o `clickable` padrão), `ModalBottomSheet`/`DropdownMenu` do Material
  onde o iOS usa `Menu`, teclado por `KeyboardOptions`/`KeyboardActions` (o "OK" do teclado numérico é a
  `ImeAction.Done`; não existe `DSKeyboardToolbar`).
- Cada arquivo termina com previews `@DSPreviews` dentro de `DSPreviewContainer`, cobrindo os estados do
  `#Preview` do Swift (normal, desabilitado, carregando, erro…).
- Cada grupo tem um teste de screenshot em `components/src/test/kotlin/io/github/romulomga/dskit/<Grupo>ScreenshotTest.kt`
  que estende `ScreenshotTestBase` e chama `snapshot("<grupo>_<componente>") { … }` para cada componente
  e estado relevante.
- KDoc em português nas declarações públicas, dizendo para que serve e quando usar (o iOS tem o texto em inglês;
  traduza o sentido).
