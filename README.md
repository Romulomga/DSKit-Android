# DSKit Android

Design system em Jetpack Compose, irmão do [DSKit iOS](https://github.com/Romulomga/DSKit) (paridade com a tag
`1.0.38`): os mesmos tokens, com os mesmos valores, e os mesmos componentes, com os mesmos nomes. É agnóstico:
nenhum app tem código aqui; cada app passa a própria marca e a própria fonte pelo tema.

- minSdk 26, compileSdk 37, Kotlin 2.4, Compose BOM 2026.09, Material 3.
- Sem dependência além de AndroidX/Compose.
- Ainda **não publicado**. Quando houver tag no GitHub, o consumo será pelo JitPack:
  `implementation("com.github.Romulomga.DSKit-Android:components:<tag>")`.

## Uso

```kotlin
val Brand = DSBrand(
    accent = Color(0xFFD0212E),
    accentDark = Color(0xFFEA4652),
    accentSecondary = Color(0xFF1F8A5A),
)

DSTheme(appName = "Homer", brand = Brand, fontFamily = Raleway, fieldLabelStyle = DSFieldLabelStyle.Prominent) {
    DSScreen(title = "Publicações", bottomAction = { DSPrimaryButton("Publicar", onClick = ::publish) }) {
        DSCard { Text("Olá", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }
    }
}
```

| DSKit iOS | DSKit Android |
|---|---|
| `.dsTheme(DSTheme(...))` + colorsets `Accent`/`OnAccent` do app | `DSTheme(brand = DSBrand(...)) { }` |
| `DSTypography.family = DSFontFamily("Raleway")` | `DSTheme(fontFamily = …)` |
| `Color.surface`, `theme.primary` | `DSTheme.colors.surface`, `DSTheme.primary` |
| `DSTypography.headline()` | `DSTypography.headline` |
| `DSSpacing.lg`, `DSRadius.lg`, `.dsShadow(.card)`, `DSMotion.snappy` | `DSSpacing.lg`, `DSRadius.large`, `Modifier.dsShadow(DSShadow.card)`, `DSMotion.snappy()` |
| `.dsGroupedCanvas()`, `dsSurfaceLevel` | `DSGroupedCanvas { }`, `DSSurfaceContainer { }`, `DSTheme.surfaceLevel` |
| `systemImage: "star"` | `icon: ImageVector` (`DSIcons` traz os que a lib usa) |
| `@Binding` | `value` + `onValueChange` |

O `DSTheme` também monta um `MaterialTheme` com os mesmos tokens, então componentes Material (seletor de data,
bottom sheet, menus) herdam cor e fonte.

## Módulos

| Módulo | Conteúdo |
|---|---|
| `tokens` | cores (geradas), tipografia, espaçamento, raio, sombra, movimento, `DSTheme` |
| `components` | os componentes, ícones, vibração, acessibilidade, previews; strings em 10 línguas |
| `lint` | regras de Android Lint publicadas com o AAR (`lintPublish`) |
| `sample` | catálogo com todos os componentes, claro/escuro e rótulos em destaque |

Componentes: `DSPrimaryButton`, `DSSecondaryButton`, `DSDestructiveButton`, `DSPillButton`, `DSIconButton`, `DSCard`,
`DSFeatureCard`, `DSResultCard`, `DSBadge`, `DSProBadge`, `DSTextField`, `DSPickerField`, `DSNumberField`, `DSListInput`,
`DSInputBox`, `DSFieldLabel`, `DSFieldCaption`, `DSToast`/`DSToastHost`, `DSErrorState`, `DSEmptyState`, `DSLoadingState`,
`DSProgressBar`, `DSScreen`, `DSSection`, `DSSettingsSection`/`DSSettingsRow`, `DSAdaptiveGrid`, `DSToggleRow`, `DSChip`,
`DSOptionChip`, `DSSegmentedPicker`, `DSPagedTabs`, `DSRangeSlider`. Ficaram de fora, até um app pedir: paywall, plano,
swatch de cor, rolagem paginada vertical, fundo atmosférico e Liquid Glass.

## Regras de lint para quem usa a lib

Fora do próprio DSKit e de pacotes `…designsystem…` do app, avisam: `DSRawColor` (`Color(0x…)`, `Color.White`),
`DSSpacingLiteral` (`padding(16.dp)`), `DSShadowLiteral`, `DSAnimationLiteral` (`tween(300)`), `DSProgressIndicator`
(`CircularProgressIndicator()` solto), `DSFontLiteral` (`fontSize = 14.sp`) e `DSMaterialIcon` (`Icons.Default.*`).
São as `ds_*` do SwiftLint do app iOS.

`DSComponent` é **erro**: componente do Material 3 chamado onde o DSKit tem o seu (`Button` → `DSPrimaryButton`,
`TextField` → `DSTextField`, `Switch` → `DSToggleRow`, `MaterialTheme` → `DSTheme`…; o mapa está em
`DSComponentDetector.REPLACEMENTS`). Material sem equivalente (`Scaffold`, `ModalBottomSheet`…) segue livre.
Veio do `DesignSystemDetector` do Now in Android. Se o DSKit não cobre o caso, evolua o DSKit em vez de suprimir.

## Desenvolvimento

```sh
./gradlew ktlint lint test :sample:assembleDebug   # tudo o que o CI vai rodar
./gradlew :components:recordRoborazziDebug         # grava as screenshots (claro, escuro, fonte 200 %) e roda o ATF
./gradlew :components:verifyRoborazziDebug         # compara com as gravadas
scripts/sync-tokens.py                             # regenera as cores a partir dos colorsets do DSKit iOS
scripts/sync-tokens.py --check                     # só confere (CI)
```

As cores vêm dos colorsets do DSKit iOS (`Sources/DSKit/Resources/Colors.xcassets`), que são a fonte de verdade; o
`DESIGN.md` do iOS está desatualizado (1.0.21). Convenções de porte em [`CONVENTIONS.md`](CONVENTIONS.md).

## Diferenças em relação ao DSKit iOS

Escolhas de plataforma, não lacunas:

- **Alvo de toque de 48 dp** (o iOS usa 44 pt): `DSIconButton`, caixas de campo, pílula do seletor e trilho do slider
  têm 48 dp de altura mínima; o desenho interno (polegar do slider, chip de opção) mantém o tamanho do iOS.
- **Sem cantos contínuos** (`RoundedCornerShape`) e **sem material translúcido**: `DSCardVariant.Material` desenha como
  `Elevated` sem borda; a barra inferior do `DSScreen` é opaca com uma linha no topo.
- **Sem `DSKeyboardToolbar`**: o "OK" do teclado numérico é a `ImeAction.Done` do próprio teclado.
- **Texto sobre cor de marca usa `onAccent`** onde o iOS pinta branco fixo (lacuna do `DESIGN.md` do iOS).
- **Fonte de sistema é a Roboto** quando o app não passa família (no iOS, a San Francisco).
- `DSErrorState`/`DSEmptyState` juntam ícone e texto num elemento do TalkBack, mas o botão continua tocável à parte.
- `DSResultCard` reduz o texto até 50 % antes de quebrar (`TextAutoSize`), como o `minimumScaleFactor`.
- `DSRangeSlider` tem trilho próprio (o `RangeSlider` do Material sobrepõe as alças e não garante distância mínima em passos): cada alça anda numa pista deslocada de uma largura de alça, então o intervalo desce a 1 passo sem sobrepor; não anima o número.
- `DSNumberField` não apaga o campo quando se digita um caractere inválido depois de um número (o iOS apaga; parece bug
  do iOS, não reproduzido aqui).
