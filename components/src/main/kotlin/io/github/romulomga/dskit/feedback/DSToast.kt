package io.github.romulomga.dskit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay

/** Estilos do `DSToast`: cada um tem um ícone padrão e uma cor de fundo. */
public enum class DSToastStyle {
    /** Fundo `accent`, ícone de informação. Padrão. */
    Info,

    /** Fundo `successHigh`, ícone de check. */
    Success,

    /** Fundo `errorHigh`, ícone de exclamação. */
    Error,

    /** Fundo `warningHigh`, ícone de alerta. */
    Warning
}

/**
 * Aviso leve em forma de pílula (snackbar). Use direto num overlay, ou passe um `DSToastMessage` ao
 * `DSToastHost` para ele aparecer no topo e sumir sozinho.
 */
@Composable
public fun DSToast(message: String, modifier: Modifier = Modifier, icon: ImageVector? = null, style: DSToastStyle = DSToastStyle.Info) {
    val colors = DSTheme.colors
    val tint = when (style) {
        DSToastStyle.Info -> colors.accent
        DSToastStyle.Success -> colors.successHigh
        DSToastStyle.Error -> colors.errorHigh
        DSToastStyle.Warning -> colors.warningHigh
    }
    val glyph = with(LocalDensity.current) { DSTypography.body.fontSize.toDp() }

    Row(
        modifier = modifier
            .dsShadow(DSShadow.subtle, DSRadius.capsule)
            .background(tint, DSRadius.capsule)
            .padding(horizontal = DSSpacing.md, vertical = DSSpacing.sm + DSSpacing.xxs)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon ?: style.defaultIcon,
            contentDescription = null,
            tint = colors.onAccent,
            modifier = Modifier.size(glyph)
        )

        Text(
            text = message,
            style = DSTypography.subheadline,
            fontWeight = FontWeight.SemiBold,
            color = colors.onAccent,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/** Os dados de um aviso, para o `DSToastHost`. Um valor novo (mesmo com o mesmo texto) reinicia o tempo. */
@Immutable
public class DSToastMessage(
    public val message: String,
    public val icon: ImageVector? = null,
    public val style: DSToastStyle = DSToastStyle.Info
)

/**
 * Mostra `message` no topo, abaixo da barra de status, e chama `onDismiss` depois de `duration`. Coloque
 * por último num `Box` que cobre a tela, por cima do conteúdo; o equivalente do `.dsToast(_:)` do iOS.
 *
 * ```
 * var toast by remember { mutableStateOf<DSToastMessage?>(null) }
 * Box(Modifier.fillMaxSize()) {
 *     Content(onCopy = { toast = DSToastMessage(copied, style = DSToastStyle.Success) })
 *     DSToastHost(toast, onDismiss = { toast = null })
 * }
 * ```
 *
 * Entra deslizando do topo com fade; com "Remover animações", só o fade.
 */
@Composable
public fun DSToastHost(message: DSToastMessage?, onDismiss: () -> Unit, modifier: Modifier = Modifier, duration: Duration = 2.seconds) {
    val reduceMotion = DSTheme.reduceMotion
    val currentOnDismiss by rememberUpdatedState(onDismiss)

    // Guarda o último aviso para a animação de saída ainda ter o que desenhar depois do `null`.
    var lastMessage by remember { mutableStateOf(message) }
    if (message != null) lastMessage = message

    LaunchedEffect(message) {
        if (message != null) {
            delay(duration)
            currentOnDismiss()
        }
    }

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        AnimatedVisibility(
            visible = message != null,
            enter = if (reduceMotion) {
                fadeIn(finite(DSMotion.medium()))
            } else {
                slideInVertically(finite(DSMotion.spring())) { -it } + fadeIn(finite(DSMotion.spring()))
            },
            exit = if (reduceMotion) {
                fadeOut(finite(DSMotion.medium()))
            } else {
                slideOutVertically(finite(DSMotion.medium())) { -it } + fadeOut(finite(DSMotion.medium()))
            }
        ) {
            lastMessage?.let { toast ->
                DSToast(
                    message = toast.message,
                    icon = toast.icon,
                    style = toast.style,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = DSSpacing.lg)
                        .padding(top = DSSpacing.md)
                        .semantics { liveRegion = LiveRegionMode.Polite }
                )
            }
        }
    }
}

/**
 * `DSMotion` devolve `AnimationSpec`, mas as transições do `AnimatedVisibility` pedem `FiniteAnimationSpec`.
 * As curvas do DSMotion são `tween` e `spring`, ambas finitas.
 */
private fun <T> finite(spec: AnimationSpec<T>): FiniteAnimationSpec<T> = spec as FiniteAnimationSpec<T>

private val DSToastStyle.defaultIcon: ImageVector
    get() = when (this) {
        DSToastStyle.Info -> DSIcons.InfoCircle
        DSToastStyle.Success -> DSIcons.CheckCircle
        DSToastStyle.Error -> DSIcons.ExclamationCircle
        DSToastStyle.Warning -> DSIcons.ExclamationTriangle
    }

@DSPreviews
@Composable
private fun DSToastPreview() {
    DSPreviewContainer("Toast") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSToast("Info notice", style = DSToastStyle.Info)

            DSToast("Saved successfully", style = DSToastStyle.Success)

            DSToast("Something went wrong", style = DSToastStyle.Error)

            DSToast("Heads up", style = DSToastStyle.Warning)
        }
    }
}

@DSPreviews
@Composable
private fun DSToastHostPreview() {
    DSPreviewContainer("Toast host") {
        Box(Modifier.fillMaxWidth().heightIn(min = DSTouchTarget.minimum * 3)) {
            DSToastHost(DSToastMessage("Copied to clipboard", style = DSToastStyle.Success), onDismiss = {})
        }
    }
}
