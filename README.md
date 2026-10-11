# DSKit Android

Design system em Jetpack Compose sobre o **Material 3**. Os componentes são os do Material, configurados com os
padrões do app: marca, vibração, acessibilidade, estados (carregando, destrutivo, erro) e uma API curta. A tela
segue o Android: barra superior, FAB, listas, campos com rótulo flutuante, snackbar, navegação inferior que vira
lateral em tela larga. É agnóstico: nenhum app tem código aqui; cada app passa a própria marca e a própria fonte
pelo tema.

- minSdk 26, compileSdk 37, Kotlin 2.4, Compose BOM 2026.09, Material 3 estável (1.4).
- Sem dependência além de AndroidX/Compose.
- Publicado pelo [JitPack](https://jitpack.io/#Romulomga/DSKit-Android); mudanças de cada versão em
  [`CHANGELOG.md`](CHANGELOG.md).

## Instalação

```kotlin
// settings.gradle.kts
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") { content { includeGroup("com.github.Romulomga.DSKit-Android") } }
    }
}

// build.gradle.kts do app
dependencies {
    implementation("com.github.Romulomga.DSKit-Android:components:0.1.0") // traz o tokens e as regras de lint
}
```

O `content { includeGroup(…) }` faz o Gradle buscar só o DSKit no JitPack, e nada mais.

## Uso

Gere os esquemas de cor no [Material Theme Builder](https://material-foundation.github.io/material-theme-builder/)
a partir da cor da marca e exporte para Jetpack Compose; o arquivo exportado traz o `lightScheme` e o `darkScheme`.

```kotlin
val HomerBrand = DSBrand(light = lightScheme, dark = darkScheme)

DSTheme(brand = HomerBrand, fontFamily = Raleway, dynamicColor = false) {
    DSScreen(
        title = "Publicações",
        topBarStyle = DSTopAppBarStyle.Large,
        floatingActionButton = { DSExtendedFab("Nova publicação", DSIcons.Add, onClick = ::compose) },
    ) { padding ->
        LazyColumn(contentPadding = DSScreenDefaults.contentPadding(padding)) {
            items(posts) { DSCard(onClick = { open(it) }) { Text(it.title, style = DSTheme.typography.titleMedium) } }
        }
    }
}
```

- **Cor:** `DSTheme.colors` é o `ColorScheme` do Material (`primary`, `surfaceContainer`, `onSurfaceVariant`…);
  `DSTheme.status` traz sucesso, aviso e informação, que o Material não tem. `dynamicColor = true` usa as cores do
  papel de parede (Material You) no Android 12+. Com os esquemas de contraste médio e alto no `DSBrand`
  (o Theme Builder também os exporta), o tema segue o "Contraste de cor" do Android 14+.
- **Tipo:** `DSTheme.typography` é a escala do Material (display, headline, title, body, label) com a fonte do app.
- **Forma e movimento:** `DSTheme.shapes` do Material; `DSMotion` com as durações e curvas do Material 3.
- **Espaço:** `DSSpacing` (grade de 4 dp) e `DSTouchTarget.minimum` (48 dp).

## Componentes

| Grupo | Componentes |
|---|---|
| Ações | `DSButton`, `DSTonalButton`, `DSOutlinedButton`, `DSTextButton` (com `loading` e `destructive`), `DSIconButton` (com dica ao segurar), `DSFab`, `DSExtendedFab`, `DSTextLink` |
| Contêiner | `DSCard` (filled, elevated, outlined), `DSAdaptiveGrid`, `DSDivider`, `DSVerticalDivider` |
| Listas | `DSListItem`, `DSSwitchListItem`, `DSCheckboxListItem`, `DSRadioListItem`, `DSListSectionHeader` |
| Entrada | `DSTextField` (senha, limpar, erro), `DSNumberField`, `DSDropdownField`, `DSAutocompleteField`, `DSSlider`, `DSRangeSlider` |
| Seleção | `DSSegmentedButton`, `DSMultiSegmentedButton`, `DSFilterChip`, `DSInputChip`, `DSAssistChip`, `DSCheckbox`, `DSTabs`, `DSTabbedPager` |
| Navegação | `DSScreen`, `DSScreenColumn`, `DSTopAppBar`, `DSNavigationScaffold`, `DSNavigationBar`, `DSNavigationRail` |
| Feedback | `DSSnackbarHost` + `showDSSnackbar`, `DSBanner`, `DSBadge`, `DSBadgedBox`, `DSLinearProgress`, `DSLoadingState`, `DSEmptyState`, `DSErrorState`, `Modifier.dsSkeleton`, `DSSkeletonRow` |
| Sobreposição | `DSAlertDialog`, `DSBottomSheet` + `DSSheetAction`, `DSTooltip`, `DSLoadingOverlay` |
| Tela larga | `Modifier.dsReadableColumn`, `DSAdaptiveColumns`, `DSLayout` |

Cada componente tem uma página em [`docs/components/`](docs/components/README.md). O que o Material já resolve
bem e o DSKit não embrulha (`DropdownMenu`, `DatePicker`, `TimePicker`, `SearchBar`) segue livre e já sai com a
marca, porque o `DSTheme` é um `MaterialTheme`.

Uma tela de formulário:

```kotlin
DSScreen(title = "Editar perfil", onBack = ::pop) { padding ->
    DSScreenColumn(padding) {
        DSTextField(value = name, onValueChange = { name = it }, label = "Nome")
        DSButton("Salvar", onClick = ::save, loading = saving)
    }
}
```

A navegação principal (barra embaixo no celular, lateral em tablet):

```kotlin
DSNavigationScaffold(items = tabs, selection = tab, onSelectionChange = { tab = it }) {
    when (tab) { Tab.Home -> HomeScreen(); Tab.Chat -> ChatScreen() }
}
```

## Módulos

| Módulo | Conteúdo |
|---|---|
| `tokens` | `DSTheme`, `DSBrand`, cores de status, tipografia, espaçamento, movimento |
| `components` | os componentes, ícones, vibração, acessibilidade, previews; strings em 10 línguas |
| `lint` | regras de Android Lint publicadas com o AAR (`lintPublish`) |
| `sample` | catálogo com abas, claro/escuro e Material You |

## Regras de lint para quem usa a lib

`DSComponent` é **erro**: componente do Material 3 chamado direto onde o DSKit tem o seu (`Button` → `DSButton`,
`OutlinedTextField` → `DSTextField`, `Scaffold` → `DSScreen`, `TopAppBar` → `DSTopAppBar`, `AlertDialog` →
`DSAlertDialog`, `ModalBottomSheet` → `DSBottomSheet`, `ListItem` → `DSListItem`…; o mapa está em
`DSComponentDetector.REPLACEMENTS`). Veio do `DesignSystemDetector` do Now in Android. Se o DSKit não cobre o caso,
evolua o DSKit em vez de suprimir.

Fora do próprio DSKit e de pacotes `…designsystem…` do app, avisam: `DSRawColor` (`Color(0x…)`, `Color.White`),
`DSSpacingLiteral` (`padding(16.dp)`), `DSShadowLiteral`, `DSAnimationLiteral` (`tween(300)`), `DSProgressIndicator`
(`CircularProgressIndicator()` solto), `DSFontLiteral` (`fontSize = 14.sp`) e `DSMaterialIcon` (`Icons.Default.*`).
Espaçamento e duração dizem o token certo e, quando o valor está na escala, o Android Studio troca sozinho
(`16.dp` → `DSSpacing.lg`, `tween(300)` → `DSMotion.medium()`).

## Desenvolvimento

```sh
./gradlew ktlint lint test :sample:assembleDebug   # tudo o que o CI vai rodar
./gradlew :components:recordRoborazziDebug   # grava as screenshots (claro, escuro, fonte 200 %, RTL, alto contraste) e roda o ATF
./gradlew :components:verifyRoborazziDebug   # compara com as gravadas
./gradlew assembleRelease -Pdskit.composeReports=true   # relatório de estabilidade do Compose em build/compose_compiler
```

Para lançar uma versão: mova o "Não lançado" do `CHANGELOG.md` para `## [x.y.z] - data`, ponha `VERSION_NAME=x.y.z`
em `gradle.properties`, faça o commit e envie a tag (`git tag x.y.z && git push origin x.y.z`). O workflow
`release.yml` confere a versão, roda as checagens, cria a release no GitHub e pede a compilação ao JitPack.

Decisões de arquitetura em [`docs/adr/`](docs/adr/); convenções em
[`CONVENTIONS.md`](CONVENTIONS.md).
