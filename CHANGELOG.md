# Changelog

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e as versões, o
[SemVer](https://semver.org/lang/pt-BR/). Enquanto a versão for 0.x, qualquer versão menor pode quebrar API; a
quebra vem descrita aqui com o código de migração.

## [Não lançado]

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
- Screenshots da direita para a esquerda e em alto contraste.
