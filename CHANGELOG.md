# Changelog

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e as versões, o
[SemVer](https://semver.org/lang/pt-BR/). Enquanto a versão for 0.x, qualquer versão menor pode quebrar API; a
quebra vem descrita aqui com o código de migração.

## [Não lançado]

### Removido

- Publicação pelo JitPack: o DSKit entra no app como submódulo git (ver o README).

### Adicionado

- Licença MIT.

## [0.1.1] - 2026-10-11

### Adicionado

- Instalação como submódulo git (`includeBuild`), sem servidor de pacotes: os módulos têm as coordenadas Maven
  para o Gradle trocar pelos projetos, e o `lint` pode entrar por `lintChecks`.
- Incluído num app sem `local.properties` próprio, o DSKit usa o `sdk.dir` do app.

### Corrigido

- As regras de lint não achavam chamadas a funções com parâmetro `Dp` ou `Color` em código de verdade (o Kotlin
  muda o nome delas no binário): `padding(16.dp)`, `spacedBy`, `Text(fontSize = …)`, `HorizontalDivider`, `Badge`
  e outros passavam sem aviso.

## [0.1.0] - 2026-10-11

Primeira versão publicada.

### Mudou

- Reescrita sobre o Material 3 nativo ([ADR 0001](docs/adr/0001-material-3-nativo.md)). Os componentes de
  paridade com o DSKit iOS saíram; no lugar, os do Material com os padrões do app. Migração:

  | Antes | Agora |
  |---|---|
  | `DSPrimaryButton`, `DSSecondaryButton`, `DSDestructiveButton`, `DSPillButton` | `DSButton`, `DSTonalButton`, `DSOutlinedButton` (com `destructive = true`) |
  | `DSToast` | `SnackbarHostState.showDSSnackbar` + `DSSnackbarHost` |
  | `DSProgressBar` | `DSLinearProgress` |
  | `DSPickerField` | `DSDropdownField` |
  | `DSSegmentedPicker` | `DSSegmentedButton` |
  | `DSPagedTabs` | `DSTabbedPager` |
  | `DSToggleRow` | `DSSwitchListItem` |
  | `DSChip`, `DSOptionChip` | `DSFilterChip`, `DSInputChip`, `DSAssistChip` |
  | `DSSection`, `DSSettingsSection` | `DSListSectionHeader` + `DSListItem` |
  | `DSFeatureCard`, `DSResultCard` | `DSCard` com o conteúdo do app |
  | `DSColorTokens`, `DSRadius`, `DSShadow` | `DSTheme.colors`, `DSTheme.shapes`, elevação do Material |

### Adicionado

- `DSBrand` com esquemas de contraste médio e alto; `DSTheme(contrast = …)` segue o "Contraste de cor" do Android 14+.
- `DSAutocompleteField`, `DSTextLink`, `DSBanner`, `DSBadge`, `DSBadgedBox`, `DSTooltip`, `DSDivider`, `DSVerticalDivider`.
- `DSIconButton` mostra a dica do Material com o `contentDescription` ao segurar.
- Lint: `DSSpacingLiteral` e `DSAnimationLiteral` dizem o token e trocam sozinhos quando o valor está na escala.
- `DSTextField(contentType = …)` para o autofill do sistema.
- `DSTextLink(allowedSchemes, onOpenFailure)`: só abre `https`, `mailto` e `tel` por padrão e não derruba o app
  quando nenhum app abre o endereço.
- Publicação pelo JitPack, com o código-fonte junto.

### Corrigido

- `DSTextField(password = true)` com `keyboardOptions` próprio perdia o teclado de senha (o teclado sugeria e
  aprendia a senha). Agora o teclado de senha, sem correção, vale sempre, e o autofill sabe que é senha.
- `Modifier.dsSkeleton` recompunha quem o usava a cada quadro; agora o pulso só redesenha.
- "Remover animações" passa a valer na hora, sem reabrir a tela.
- Screenshots da direita para a esquerda e em alto contraste.
