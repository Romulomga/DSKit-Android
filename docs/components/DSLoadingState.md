# DSLoadingState

Carregamento de uma tela inteira: o `CircularProgressIndicator` do Material 3 e uma legenda opcional, centralizados.

## Quando usar

Quando a forma do conteúdo não é conhecida. Quando ela é conhecida, prefira o esqueleto ([`Modifier.dsSkeleton`](DSSkeleton.md)), que não pula quando os dados chegam. Para bloquear a tela durante uma ação (enviar, publicar), use [`DSLoadingOverlay`](DSLoadingOverlay.md).

## Uso

```kotlin
DSLoadingState(message = "Carregando imóveis…")
```

## O indicador do app

Com `LocalDSLoadingIndicator` ligado no tema, o indicador é o do app, no lugar do círculo do Material: no mesmo
tamanho e na mesma cor, e com a mesma leitura no TalkBack (o que o indicador do app disser é descartado). Ele vale para
o `DSButton` em andamento, o `DSLoadingOverlay` e o `DSLoadingState`; o "puxar para atualizar" do `DSScreen` continua
com o do Material.

```kotlin
CompositionLocalProvider(LocalDSLoadingIndicator provides { modifier, color -> AppLoadingView(modifier, color) }) {
    content()
}
```

## Parâmetros

| Parâmetro | Tipo | Padrão | Descrição |
| --- | --- | --- | --- |
| `modifier` | `Modifier` | `Modifier` | Modificador do contêiner (que já ocupa a largura toda). |
| `message` | `String?` | `null` | Legenda abaixo do indicador. |

## Acessibilidade

- Indicador e legenda são lidos juntos (`mergeDescendants`).

## Screenshots

`feedback_loading_state`:

![](../../components/screenshots/feedback_loading_state_light.png)

Também existem `feedback_loading_state_dark` e `_font200`. Com o indicador do app (um quadrado de teste):
`feedback_loading_indicator_custom`.

![](../../components/screenshots/feedback_loading_indicator_custom_light.png)
