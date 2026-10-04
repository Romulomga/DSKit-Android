package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.components.R

/**
 * Bloco da tela inicial que leva a um recurso ou ferramenta: ícone, título, descrição e, opcionalmente,
 * um selo curto ("PRO", "NOVO") ou o estado bloqueado (cadeado).
 *
 * O card inteiro é um botão com ripple e leve redução de escala ao toque; o TalkBack lê tudo como um
 * elemento só.
 *
 * @param badge texto do selo ao lado do título.
 * @param isLocked mostra o cadeado. Passe [lockedDescription] (padrão: "Bloqueado", traduzido) para o TalkBack anunciar
 *   o estado; no iOS é a dica "Locked".
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
public fun DSFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    badge: String? = null,
    isLocked: Boolean = false,
    lockedDescription: String? = stringResource(R.string.dskit_locked)
) {
    val haptics = rememberDSHaptics()
    val interactionSource = remember { MutableInteractionSource() }
    val primary = DSTheme.primary
    val colors = DSTheme.colors
    val shape = DSRadius.large
    val lockSize = with(LocalDensity.current) { DSTypography.caption.fontSize.toDp() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = DSTouchTarget.minimum)
            .dsPressScale(interactionSource)
            .clip(shape)
            .background(colors.surface(DSTheme.surfaceLevel))
            .clickable(interactionSource = interactionSource, role = Role.Button) {
                haptics.light()
                onClick()
            }
            .semantics(mergeDescendants = true) {
                if (isLocked && lockedDescription != null) stateDescription = lockedDescription
            }
            .padding(DSSpacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(DSRadius.medium)
                .background(primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = primary, modifier = Modifier.size(22.dp))
        }

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(DSSpacing.xxs)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(DSSpacing.xs),
                itemVerticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, style = DSTypography.headline, color = colors.onSurfaceHigh)

                if (badge != null) {
                    Text(
                        text = badge,
                        style = DSTypography.caption2.copy(fontWeight = FontWeight.Bold),
                        color = primary,
                        modifier = Modifier
                            .clip(DSRadius.capsule)
                            .background(primary.copy(alpha = 0.15f))
                            .padding(horizontal = DSSpacing.xs + DSSpacing.xxs, vertical = DSSpacing.xxs)
                    )
                }

                if (isLocked) {
                    Icon(DSIcons.Lock, contentDescription = null, tint = colors.onSurfaceMedium, modifier = Modifier.size(lockSize))
                }
            }

            Text(description, style = DSTypography.subheadline, color = colors.onSurfaceMedium)
        }

        Icon(DSIcons.ChevronRight, contentDescription = null, tint = colors.onSurfaceLight, modifier = Modifier.size(20.dp))
    }
}

@DSPreviews
@Composable
private fun DSFeatureCardPreview() {
    DSPreviewContainer("Feature card") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSFeatureCard(title = "Number Picker", description = "Pick random numbers from a range.", icon = DSIcons.Plus, onClick = {})

            DSFeatureCard(
                title = "Name Picker",
                description = "Draw a winner from a list of names.",
                icon = DSIcons.Star,
                badge = "NEW",
                onClick = {}
            )

            DSFeatureCard(
                title = "Wheel",
                description = "Spin a roulette wheel.",
                icon = DSIcons.Circle,
                badge = "PRO",
                isLocked = true,
                onClick = {}
            )
        }
    }
}
