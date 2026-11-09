# Changelog

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e as versões, o
[SemVer](https://semver.org/lang/pt-BR/). Enquanto a versão for 0.x, qualquer versão menor pode quebrar API; a
quebra vem descrita aqui com o código de migração.

## [Não lançado]

### Adicionado
- `DSDiscardGuard`, `rememberDSDiscardGuardState`, `rememberDSHasChanges` e `DSDiscardDialog`: a guarda de descarte de
  uma tela ou painel de edição. Com mudança, o voltar do sistema e o `requestExit` perguntam antes de sair. A pergunta
  aberta e o retrato de quando a tela abriu sobrevivem à recriação da Activity e à morte do processo. As strings
  ("Descartar as alterações?", "Descartar", "Continuar editando") vêm nas 10 línguas e podem ser trocadas.
- `DSConfirmDeleteDialog`: a confirmação de exclusão sobre o `DSAlertDialog`, que aparece enquanto houver um item em
  espera (`pending`), entrega o item em `onDelete` e fecha. Vibra o aviso uma vez só.
- `DSTextInputDialog`: diálogo do Material com um campo de texto já focado. Tocar fora não fecha, e a ação do teclado
  confirma.

## [0.1.6] - 2026-11-03

### Adicionado

- `DSScreen(navigationIcon)` e `DSTopAppBar(navigationIcon)`: o lugar da seta de voltar numa tela sem `onBack`, para
  a marca do app na tela de topo de uma aba (ou um botão de menu). Com `onBack`, a seta vence.

## [0.1.5] - 2026-11-01

### Adicionado

- `DSAlertDialog(emphasizeConfirm)`: a confirmação vira um botão cheio quando ela é a escolha segura ("Continuar no
  alerta"), com os botões empilhados em largura cheia.
- `DSAlertDialog(onDismiss)`: o botão de cancelar pode fazer outra coisa que não o toque fora do diálogo. Sem ele,
  continua igual a `onDismissRequest`.

### Mudado

- Os ícones dos botões crescem com a fonte do sistema, até o dobro, em vez de ficarem fixos em 18dp.
- `DSAlertDialog`: a mensagem rola se não couber, e com fonte a partir de 150% os botões empilham em largura cheia,
  a confirmação em cima.

## [0.1.4] - 2026-10-31

### Adicionado

- `DSTextField(leadingContent, trailingContent)`: seletor no início ou no fim do campo, para valores em partes
  (DDI + número, UF + registro + tipo) lidos como uma coisa só.

## [0.1.3] - 2026-10-31

### Adicionado

- `DSTextField(visualTransformation)`: máscara enquanto se digita (telefone, documento) sem mudar o `value` nem
  perder a posição do cursor.

## [0.1.2] - 2026-10-31

### Removido

- Publicação pelo JitPack: o DSKit entra no app como submódulo git (ver o README).

### Adicionado

- Licença MIT.
- Módulo opcional `seed`: `DSBrand.fromSeed(cor, style)` gera a marca inteira a partir de uma cor (claro, escuro,
  contraste médio e alto, cores de status puxadas para o tom da marca), com o algoritmo de cor do Material.
- Cores de status de alto contraste (`DSStatusColors.LightHighContrast`/`DarkHighContrast`, acima de 7:1), usadas
  pelo `DSTheme` no contraste alto; o `DSBrand` aceita as suas.
- `DSBottomSheet(scrollable = false)` para conteúdo que já rola sozinho, como uma `LazyColumn`.
- `DSSlider`/`DSRangeSlider(onValueChangeFinished)`, para aplicar um filtro só ao soltar a alça.
- `DSNumberField(imeAction, keyboardActions)`, para avançar o foco num formulário.

### Mudado

- `DSLoadingOverlay` abre numa janela própria: bloqueia o toque, o TalkBack, o teclado e o voltar, e pode ser
  chamado em qualquer lugar da tela (antes só parava o dedo, e o TalkBack alcançava o que estava por baixo). A
  assinatura não muda; a saída deixa de ter o fade.
- `DSBrand.lightHighContrastStatus`/`darkHighContrastStatus` agora são opcionais (`null`): sem eles, a marca que
  personalizou `lightStatus`/`darkStatus` continua com as suas cores no contraste alto, e a que ficou com as do
  DSKit recebe as de alto contraste. Quem passava os dois não muda nada; quem os lia de um `DSBrand` trata o `null`.
- `DSSnackbarVisuals`/`showDSSnackbar`: com ação, `withDismissAction` passa a ser `true` por padrão. O snackbar com
  ação não some sozinho, e sem o "x" travava a fila do host.
- Ações destrutivas (`DSButton`, `DSTonalButton`, `DSOutlinedButton`, `DSTextButton`, `DSSheetAction`) vibram com
  `warning()`, como pede o CONVENTIONS.

### Corrigido

- Uma `LazyColumn` dentro do `DSBottomSheet` travava o app (lista sem altura máxima dentro de conteúdo que rola).
- `DSExtendedFab` recolhido ficava sem nome para o TalkBack (o texto some; agora o ícone leva o nome).
- `DSInputChip` era lido como caixa de seleção "não marcado" e repetia o rótulo; agora é um botão "Remover <rótulo>".
- `Modifier.dsSkeleton` não bloqueava o toque do elemento: o `clickable` de dentro recebia o evento primeiro.
- Linhas de lista desabilitadas com controle (`DSSwitchListItem`, `DSCheckboxListItem`, `DSRadioListItem`) ficavam
  esmaecidas duas vezes (~14 % de opacidade no controle).
- Botão carregando era anunciado como acionável; agora o TalkBack ouve "desativado".
- Sliders: o TalkBack lê o `valueText` ("3 quartos") em vez da porcentagem, o rótulo não é lido duas vezes, o
  cabeçalho esmaece quando desabilitado, e o `minimumDistance` encosta a alça no limite em vez de descartar o arraste.
- `DSNumberField`: um valor que o app devolve atrasado (ViewModel assíncrono) não desfaz mais o que foi digitado.
- `DSBanner` e `DSErrorState` são anunciados ao aparecer; os títulos deles e do `DSEmptyState` são cabeçalhos.
- `DSDropdownField` anuncia qual opção está escolhida.
- Lint: `DSComponent` pega um componente do Material importado com alias. Os testes da regra rodam em todos os modos
  do lint; os das regras de token ainda pulam o de alias e o de espaços, porque elas comparam texto (limitação
  conhecida, a resolver).
- Lint ligado com `warningsAsErrors` também no módulo `tokens`, como nos outros.
- Exemplo do `fromSeed` no KDoc e no README calcula a marca uma vez (`remember`), não a cada recomposição.

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
