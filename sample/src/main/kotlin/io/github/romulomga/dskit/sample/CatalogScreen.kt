package io.github.romulomga.dskit.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.DSAdaptiveGrid
import io.github.romulomga.dskit.DSCard
import io.github.romulomga.dskit.DSCardVariant
import io.github.romulomga.dskit.DSChip
import io.github.romulomga.dskit.DSDestructiveButton
import io.github.romulomga.dskit.DSEmptyState
import io.github.romulomga.dskit.DSErrorState
import io.github.romulomga.dskit.DSFeatureCard
import io.github.romulomga.dskit.DSIconButton
import io.github.romulomga.dskit.DSIcons
import io.github.romulomga.dskit.DSListInput
import io.github.romulomga.dskit.DSLoadingState
import io.github.romulomga.dskit.DSNumberField
import io.github.romulomga.dskit.DSOptionChip
import io.github.romulomga.dskit.DSPagedTabs
import io.github.romulomga.dskit.DSPickerField
import io.github.romulomga.dskit.DSPickerOption
import io.github.romulomga.dskit.DSPillButton
import io.github.romulomga.dskit.DSPillButtonVariant
import io.github.romulomga.dskit.DSPrimaryButton
import io.github.romulomga.dskit.DSProgressBar
import io.github.romulomga.dskit.DSRangeSlider
import io.github.romulomga.dskit.DSResultCard
import io.github.romulomga.dskit.DSScreen
import io.github.romulomga.dskit.DSSecondaryButton
import io.github.romulomga.dskit.DSSection
import io.github.romulomga.dskit.DSSegmentedOption
import io.github.romulomga.dskit.DSSegmentedPicker
import io.github.romulomga.dskit.DSSegmentedStyle
import io.github.romulomga.dskit.DSSettingsRow
import io.github.romulomga.dskit.DSSettingsSection
import io.github.romulomga.dskit.DSSpacing
import io.github.romulomga.dskit.DSTextField
import io.github.romulomga.dskit.DSTheme
import io.github.romulomga.dskit.DSToastMessage
import io.github.romulomga.dskit.DSToastStyle
import io.github.romulomga.dskit.DSToggleRow
import io.github.romulomga.dskit.DSTypography

private enum class Tab(val label: String) { Buttons("Botões"), Fields("Campos"), Controls("Controles"), Feedback("Feedback") }

@Composable
internal fun CatalogScreen(
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit,
    prominentLabels: Boolean,
    onProminentLabelsChange: (Boolean) -> Unit,
    onToast: (DSToastMessage) -> Unit
) {
    var loading by remember { mutableStateOf(false) }

    DSScreen(
        title = "DSKit",
        subtitle = "Android ${io.github.romulomga.dskit.DSKit.VERSION}",
        bottomAction = {
            DSPrimaryButton(
                title = "Mostrar toast",
                onClick = { onToast(DSToastMessage("Salvo com sucesso", DSIcons.CheckCircle, DSToastStyle.Success)) },
                isLoading = loading
            )
        }
    ) {
        DSSettingsSection(title = "Aparência") {
            DSSettingsRow(title = "Modo escuro", trailing = {
                androidx.compose.material3.Switch(checked = dark, onCheckedChange = onDarkChange)
            })

            DSSettingsRow(title = "Rótulos em destaque", showsDivider = false, trailing = {
                androidx.compose.material3.Switch(checked = prominentLabels, onCheckedChange = onProminentLabelsChange)
            })
        }

        var tab by remember { mutableStateOf(Tab.Buttons) }

        DSPagedTabs(
            sections = Tab.entries,
            selection = tab,
            onSelectionChange = { tab = it },
            label = { it.label },
            modifier = Modifier.height(720.dp)
        ) { page ->
            Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)) {
                when (page) {
                    Tab.Buttons -> ButtonsPage(loading = loading, onLoadingChange = { loading = it })
                    Tab.Fields -> FieldsPage()
                    Tab.Controls -> ControlsPage()
                    Tab.Feedback -> FeedbackPage(onToast)
                }
            }
        }
    }
}

@Composable
private fun ButtonsPage(loading: Boolean, onLoadingChange: (Boolean) -> Unit) {
    DSSection(title = "Botões") {
        DSPrimaryButton(title = "Publicar", onClick = {}, icon = DSIcons.Plus)
        DSSecondaryButton(title = "Continuar depois", onClick = {})
        DSDestructiveButton(title = "Excluir", onClick = {})
        DSToggleRow(title = "Botão principal carregando", checked = loading, onCheckedChange = onLoadingChange)

        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm), verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSPillButton(title = "Primário", onClick = {})
            DSPillButton(title = "Secundário", onClick = {}, variant = DSPillButtonVariant.Secondary)
            DSPillButton(title = "Contorno", onClick = {}, variant = DSPillButtonVariant.Outlined)
            DSIconButton(icon = DSIcons.Star, contentDescription = "Favoritar", onClick = {})
        }
    }

    DSSection(title = "Cards") {
        DSCard { Text("Card padrão", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }
        DSCard(variant = DSCardVariant.Elevated) { Text("Card elevado", style = DSTypography.body, color = DSTheme.colors.onSurfaceHigh) }
        DSFeatureCard(title = "Relatórios", description = "Resumo mensal das parcerias", icon = DSIcons.InfoCircle, onClick = {
        }, badge = "Novo")
        DSResultCard(title = "Resultado", primaryResult = "42")
        DSAdaptiveGrid(minItemWidth = 120.dp) {
            repeat(4) { DSCard { Text("Item ${it + 1}", style = DSTypography.subheadline, color = DSTheme.colors.onSurfaceHigh) } }
        }
    }
}

@Composable
private fun FieldsPage() {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("corretor@") }
    var quantity by remember { mutableStateOf<Int?>(3) }
    var state by remember { mutableStateOf<String?>(null) }
    var list by remember { mutableStateOf("Ipanema\nLeblon\nIpanema") }

    DSSection(title = "Campos") {
        DSTextField(value = name, onValueChange = { name = it }, title = "Nome", placeholder = "Seu nome", showsClearButton = true)
        DSTextField(value = password, onValueChange = {
            password = it
        }, title = "Senha", placeholder = "Mínimo 6 caracteres", isSecure = true)
        DSTextField(value = email, onValueChange = { email = it }, title = "E-mail", errorMessage = "E-mail inválido")
        DSNumberField(value = quantity, onValueChange = { quantity = it }, title = "Quartos")
        DSPickerField(
            placeholder = "Escolha",
            selection = state,
            onSelectionChange = { state = it },
            options = listOf("RJ", "SP", "MG").map { DSPickerOption(it, it) },
            title = "Estado"
        )
        DSListInput(value = list, onValueChange = { list = it }, title = "Bairros")
    }
}

@Composable
private fun ControlsPage() {
    var chip by remember { mutableStateOf(true) }
    var option by remember { mutableStateOf(false) }
    var segment by remember { mutableStateOf("venda") }
    var lower by remember { mutableIntStateOf(2) }
    var upper by remember { mutableIntStateOf(6) }
    var progress by remember { mutableFloatStateOf(0.4f) }

    DSSection(title = "Controles") {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSChip(title = "Apartamento", selected = chip, onClick = { chip = !chip }, emoji = "🏢")
            DSOptionChip(title = "Com vaga", selected = option, onSelectedChange = { option = it })
        }

        val options = listOf(DSSegmentedOption("venda", "Tenho"), DSSegmentedOption("compra", "Preciso"))
        DSSegmentedPicker(selection = segment, onSelectionChange = { segment = it }, options = options, title = "Sistema")
        DSSegmentedPicker(
            selection = segment,
            onSelectionChange = { segment = it },
            options = options,
            title = "Preenchido",
            style = DSSegmentedStyle.Filled
        )

        DSRangeSlider(
            lowerIndex = lower,
            upperIndex = upper,
            onLowerIndexChange = { lower = it },
            onUpperIndexChange = { upper = it },
            stepCount = 10,
            title = "Quartos",
            valueText = "$lower a $upper"
        )

        DSProgressBar(value = progress)
        DSSecondaryButton(title = "Avançar progresso", onClick = { progress = (progress + 0.2f) % 1.2f })
    }
}

@Composable
private fun FeedbackPage(onToast: (DSToastMessage) -> Unit) {
    DSSection(title = "Feedback") {
        val offline = DSToastMessage("Sem conexão com a internet.", DSIcons.ExclamationCircle, DSToastStyle.Error)
        DSSecondaryButton(title = "Toast de erro", onClick = { onToast(offline) })

        DSLoadingState(message = "Carregando…")

        DSEmptyState(
            icon = DSIcons.Tray,
            title = "Nada por aqui",
            message = "Publique um imóvel para começar.",
            actionTitle = "Publicar",
            onAction = {}
        )

        DSErrorState(title = "Não carregou", message = "Confira a conexão e tente de novo.", onRetry = {})
    }
}
