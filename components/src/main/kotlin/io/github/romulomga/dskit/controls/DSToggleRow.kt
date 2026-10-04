package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

/**
 * Linha de ajuste com um interruptor, no visual de Ajustes. Use dentro de uma seção de ajustes ou solta,
 * para um liga/desliga no meio da tela.
 *
 * A linha inteira é tocável e o TalkBack a lê como um interruptor só (título, subtítulo e estado).
 *
 * @param title texto principal.
 * @param checked se está ligado.
 * @param onCheckedChange chamado com o novo valor quando o usuário toca a linha ou o interruptor.
 * @param subtitle linha de apoio opcional, abaixo do título.
 * @param icon ícone opcional num quadrado na cor primária, à esquerda.
 * @param enabled desligado, a linha fica esmaecida e não responde.
 */
@Composable
public fun DSToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Row(
        modifier = modifier
            .toggleable(value = checked, enabled = enabled, role = Role.Switch, onValueChange = onCheckedChange)
            .heightIn(min = DSTouchTarget.minimum)
            .padding(vertical = DSSpacing.xs),
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(
                modifier = Modifier
                    .size(ToggleRowIconTile)
                    .clip(DSRadius.small)
                    .background(DSTheme.primary)
                    .alpha(if (enabled) 1f else DisabledAlpha),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = DSTheme.colors.onAccent, modifier = Modifier.size(ToggleRowIconGlyph))
            }
        }

        Column(
            modifier = Modifier.weight(1f).alpha(if (enabled) 1f else DisabledAlpha),
            verticalArrangement = Arrangement.spacedBy(DSSpacing.xxs)
        ) {
            Text(title, style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh)

            if (subtitle != null) {
                Text(subtitle, style = DSTypography.footnote, color = DSTheme.colors.onSurfaceMedium)
            }
        }

        // A linha cuida do toque; o interruptor só desenha o estado.
        Switch(
            checked = checked,
            onCheckedChange = null,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedTrackColor = DSTheme.primary,
                checkedThumbColor = DSTheme.colors.onAccent,
                checkedBorderColor = DSTheme.primary
            )
        )
    }
}

private val ToggleRowIconTile = 28.dp
private val ToggleRowIconGlyph = 17.dp
private const val DisabledAlpha = 0.5f

@DSPreviews
@Composable
private fun DSToggleRowPreview() {
    DSPreviewContainer("Toggle row") {
        var haptics by remember { mutableStateOf(true) }
        var sound by remember { mutableStateOf(false) }

        DSToggleRow(
            title = "Haptics",
            subtitle = "Subtle vibration on actions",
            icon = DSIcons.Star,
            checked = haptics,
            onCheckedChange = { haptics = it }
        )

        DSToggleRow(title = "Sound", icon = DSIcons.InfoCircle, checked = sound, onCheckedChange = { sound = it })

        DSToggleRow(title = "Disabled", checked = true, onCheckedChange = {}, enabled = false)
    }
}
