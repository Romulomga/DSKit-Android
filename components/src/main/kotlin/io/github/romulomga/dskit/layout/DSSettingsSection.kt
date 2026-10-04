package io.github.romulomga.dskit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Seção agrupada no estilo de Ajustes: título em caixa alta, um bloco arredondado com [DSSettingsRow]s e
 * rodapé opcional. O bloco fica dentro de [DSSurfaceContainer], então controles dentro das linhas leem um
 * nível de superfície a mais.
 */
@Composable
public fun DSSettingsSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(DSSpacing.xs)) {
        if (title != null) {
            Text(
                text = title.uppercase(),
                style = DSTypography.footnote.copy(letterSpacing = 0.5.sp),
                color = DSTheme.colors.onSurfaceMedium,
                modifier = Modifier.padding(horizontal = DSSpacing.md)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(DSRadius.medium)
                .background(DSTheme.colors.surface(DSTheme.surfaceLevel))
        ) {
            DSSurfaceContainer {
                Column(content = content)
            }
        }

        if (footer != null) {
            Text(
                text = footer,
                style = DSTypography.footnote,
                color = DSTheme.colors.onSurfaceMedium,
                modifier = Modifier.padding(horizontal = DSSpacing.md)
            )
        }
    }
}

/** Como o título da [DSSettingsRow] se apresenta ao lado do subtítulo. */
public enum class DSSettingsRowTitleStyle {
    /** Peso de corpo: uma opção comum numa lista de ajustes. */
    Regular,

    /** Peso de headline e mais respiro: uma entrada que leva a algum lugar (categoria de ajuda, seção). */
    Prominent
}

/**
 * Uma linha dentro de [DSSettingsSection]: ícone opcional num quadrado colorido, título, subtítulo,
 * conteúdo à direita (switch, valor) e ação opcional. Com [onClick], a linha inteira é tocável e mostra o
 * chevron de navegação; sem ele, é um contêiner simples e o controle à direita continua ativo.
 *
 * Use `showsDivider = false` na última linha da seção.
 *
 * @param iconColor fundo do quadrado do ícone. Padrão: `DSTheme.colors.accent`.
 */
@Composable
public fun DSSettingsRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconColor: Color? = null,
    titleStyle: DSSettingsRowTitleStyle = DSSettingsRowTitleStyle.Regular,
    subtitle: String? = null,
    showsDivider: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null
) {
    val haptics = rememberDSHaptics()
    val colors = DSTheme.colors
    val prominent = titleStyle == DSSettingsRowTitleStyle.Prominent

    val interaction = if (onClick != null) {
        Modifier
            .clickable(role = Role.Button) {
                haptics.light()
                onClick()
            }
            .semantics(mergeDescendants = true) {}
    } else {
        Modifier
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = DSTouchTarget.minimum)
                .then(interaction)
                .padding(horizontal = DSSpacing.md, vertical = if (prominent) DSSpacing.lg else DSSpacing.md),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(IconTile)
                        .clip(DSRadius.small)
                        .background(iconColor ?: colors.accent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = colors.onAccent, modifier = Modifier.size(17.dp))
                }
            }

            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(DSSpacing.xxs)) {
                Text(
                    text = title,
                    style = if (prominent) DSTypography.headline else DSTypography.body,
                    color = colors.onSurfaceHigh
                )

                if (subtitle != null) {
                    Text(subtitle, style = DSTypography.footnote, color = colors.onSurfaceMedium)
                }
            }

            trailing?.invoke()

            if (onClick != null) {
                Icon(DSIcons.ChevronRight, contentDescription = null, tint = colors.onSurfaceLight, modifier = Modifier.size(20.dp))
            }
        }

        if (showsDivider) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = DSSpacing.md + IconTile + DSSpacing.md)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(colors.border)
            )
        }
    }
}

private val IconTile = 28.dp

@DSPreviews
@Composable
private fun DSSettingsSectionPreview() {
    var haptics by remember { mutableStateOf(true) }

    DSPreviewContainer("Settings section") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
            DSSettingsSection(title = "Help") {
                DSSettingsRow(
                    title = "Account and profile",
                    titleStyle = DSSettingsRowTitleStyle.Prominent,
                    subtitle = "Manage your profile",
                    onClick = {}
                )

                DSSettingsRow(
                    title = "Billing",
                    titleStyle = DSSettingsRowTitleStyle.Prominent,
                    subtitle = "Invoices and receipts",
                    showsDivider = false,
                    onClick = {}
                )
            }

            DSSettingsSection(title = "Preferences", footer = "Version 1.0.0") {
                DSSettingsRow(title = "Haptics", icon = DSIcons.Check) {
                    Switch(checked = haptics, onCheckedChange = { haptics = it })
                }

                DSSettingsRow(
                    title = "Rate the app",
                    icon = DSIcons.Star,
                    iconColor = DSTheme.colors.warningHigh,
                    showsDivider = false,
                    onClick = {}
                )
            }
        }
    }
}
