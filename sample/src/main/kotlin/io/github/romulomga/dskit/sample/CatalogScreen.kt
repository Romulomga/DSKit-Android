package io.github.romulomga.dskit.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.romulomga.dskit.DSAlertDialog
import io.github.romulomga.dskit.DSAssistChip
import io.github.romulomga.dskit.DSAutocompleteField
import io.github.romulomga.dskit.DSBadgedBox
import io.github.romulomga.dskit.DSBanner
import io.github.romulomga.dskit.DSBannerStyle
import io.github.romulomga.dskit.DSBottomSheet
import io.github.romulomga.dskit.DSButton
import io.github.romulomga.dskit.DSCard
import io.github.romulomga.dskit.DSCardVariant
import io.github.romulomga.dskit.DSCheckbox
import io.github.romulomga.dskit.DSCheckboxListItem
import io.github.romulomga.dskit.DSDivider
import io.github.romulomga.dskit.DSDropdownField
import io.github.romulomga.dskit.DSEmptyState
import io.github.romulomga.dskit.DSErrorState
import io.github.romulomga.dskit.DSExtendedFab
import io.github.romulomga.dskit.DSFilterChip
import io.github.romulomga.dskit.DSIconButton
import io.github.romulomga.dskit.DSIconButtonStyle
import io.github.romulomga.dskit.DSIcons
import io.github.romulomga.dskit.DSInputChip
import io.github.romulomga.dskit.DSKit
import io.github.romulomga.dskit.DSLinearProgress
import io.github.romulomga.dskit.DSLink
import io.github.romulomga.dskit.DSListItem
import io.github.romulomga.dskit.DSListSectionHeader
import io.github.romulomga.dskit.DSLoadingOverlay
import io.github.romulomga.dskit.DSNavigationItem
import io.github.romulomga.dskit.DSNavigationScaffold
import io.github.romulomga.dskit.DSNumberField
import io.github.romulomga.dskit.DSOption
import io.github.romulomga.dskit.DSOutlinedButton
import io.github.romulomga.dskit.DSRadioListItem
import io.github.romulomga.dskit.DSRangeSlider
import io.github.romulomga.dskit.DSScreen
import io.github.romulomga.dskit.DSScreenColumn
import io.github.romulomga.dskit.DSScreenDefaults
import io.github.romulomga.dskit.DSSegmentedButton
import io.github.romulomga.dskit.DSSheetAction
import io.github.romulomga.dskit.DSSkeletonRow
import io.github.romulomga.dskit.DSSnackbarStyle
import io.github.romulomga.dskit.DSSpacing
import io.github.romulomga.dskit.DSSwitchListItem
import io.github.romulomga.dskit.DSTabs
import io.github.romulomga.dskit.DSTextButton
import io.github.romulomga.dskit.DSTextField
import io.github.romulomga.dskit.DSTextLink
import io.github.romulomga.dskit.DSTheme
import io.github.romulomga.dskit.DSTonalButton
import io.github.romulomga.dskit.DSTooltip
import io.github.romulomga.dskit.DSTopAppBarStyle
import io.github.romulomga.dskit.dsSkeleton
import io.github.romulomga.dskit.showDSSnackbar
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private enum class Tab(val label: String) { Actions("Ações"), Inputs("Entrada"), Feedback("Feedback"), Settings("Ajustes") }

@Composable
internal fun CatalogApp(
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    brand: SampleBrand,
    onBrandChange: (SampleBrand) -> Unit
) {
    var tab by rememberSaveable { mutableStateOf(Tab.Actions) }

    DSNavigationScaffold(
        items = listOf(
            DSNavigationItem(Tab.Actions, Tab.Actions.label, DSIcons.Home),
            DSNavigationItem(Tab.Inputs, Tab.Inputs.label, DSIcons.Edit),
            DSNavigationItem(Tab.Feedback, Tab.Feedback.label, DSIcons.Inbox, badgeCount = 2),
            DSNavigationItem(Tab.Settings, Tab.Settings.label, DSIcons.Person)
        ),
        selection = tab,
        onSelectionChange = { tab = it }
    ) {
        when (tab) {
            Tab.Actions -> ActionsScreen()
            Tab.Inputs -> InputsScreen()
            Tab.Feedback -> FeedbackScreen()
            Tab.Settings -> SettingsScreen(dark, onDarkChange, dynamicColor, onDynamicColorChange, brand, onBrandChange)
        }
    }
}

@Composable
private fun ActionsScreen() {
    var parking by remember { mutableStateOf(true) }
    var pets by remember { mutableStateOf(false) }

    DSScreen(
        title = "DSKit ${DSKit.VERSION}",
        topBarStyle = DSTopAppBarStyle.Large,
        actions = { DSIconButton(DSIcons.Search, "Buscar", onClick = {}) },
        floatingActionButton = { DSExtendedFab("Nova publicação", DSIcons.Add, onClick = {}) }
    ) { padding ->
        DSScreenColumn(padding) {
            DSButton("Publicar", onClick = {}, icon = DSIcons.Add)

            DSTonalButton("Salvar rascunho", onClick = {})

            DSOutlinedButton("Cancelar", onClick = {})

            DSTextButton("Esqueci a senha", onClick = {})

            DSButton("Excluir conta", onClick = {}, destructive = true)

            FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSIconButton(DSIcons.Share, "Compartilhar", onClick = {})
                DSIconButton(DSIcons.Add, "Adicionar", onClick = {}, style = DSIconButtonStyle.Filled)
                DSIconButton(DSIcons.Edit, "Editar", onClick = {}, style = DSIconButtonStyle.Tonal)
                DSIconButton(DSIcons.Delete, "Excluir", onClick = {}, style = DSIconButtonStyle.Outlined)
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
                DSFilterChip("Com vaga", selected = parking, onSelectedChange = { parking = it })
                DSFilterChip("Aceita pet", selected = pets, onSelectedChange = { pets = it })
                DSInputChip("Ipanema", onRemove = {})
                DSAssistChip("Ligar", onClick = {}, icon = DSIcons.Share)
            }

            DSCard {
                Text("Apartamento em Ipanema", style = DSTheme.typography.titleMedium)
                Text("3 quartos · 2 vagas · 120 m²", style = DSTheme.typography.bodyMedium)
            }

            DSCard(variant = DSCardVariant.Outlined, onClick = {}) { Text("Card com contorno, tocável") }

            DSDivider()

            Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.xl), verticalAlignment = Alignment.CenterVertically) {
                DSBadgedBox(count = 3, badgeDescription = "3 mensagens não lidas") { Icon(DSIcons.Inbox, contentDescription = "Mensagens") }

                DSTooltip(text = "Imóveis fixados ficam no topo do feed.", title = "Fixar") {
                    Icon(DSIcons.Info, contentDescription = "Sobre fixar")
                }
            }

            DSTextLink(
                text = "Ao publicar, você aceita os Termos de uso e a Política de privacidade.",
                links = listOf(DSLink.Url("Termos de uso", "https://example.com/termos"), DSLink.Action("Política de privacidade") {})
            )
        }
    }
}

@Composable
private fun InputsScreen() {
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("corretor@") }
    var rooms by remember { mutableStateOf<Int?>(3) }
    var state by remember { mutableStateOf<String?>(null) }
    var neighborhood by remember { mutableStateOf("") }
    var deal by remember { mutableStateOf("sale") }
    var price by remember { mutableStateOf(2f..6f) }
    var kind by remember { mutableStateOf("Apartamento") }
    var pool by remember { mutableStateOf(true) }
    var terms by remember { mutableStateOf(false) }

    DSScreen(title = "Entrada") { padding ->
        DSScreenColumn(padding) {
            DSTextField(value = name, onValueChange = {
                name = it
            }, label = "Nome", supportingText = "Como aparece no perfil", clearable = true)

            DSTextField(value = password, onValueChange = { password = it }, label = "Senha", password = true)

            DSTextField(value = email, onValueChange = { email = it }, label = "E-mail", errorText = "E-mail inválido")

            DSNumberField(value = rooms, onValueChange = { rooms = it }, label = "Quartos")

            DSAutocompleteField(
                query = neighborhood,
                onQueryChange = { neighborhood = it },
                suggestions = Neighborhoods
                    .filter { neighborhood.isNotBlank() && it.contains(neighborhood, ignoreCase = true) }
                    .map { DSOption(it, it) },
                onSuggestionSelect = { neighborhood = it.label },
                label = "Bairro",
                leadingIcon = DSIcons.Search
            )

            DSDropdownField(
                selection = state,
                onSelectionChange = { state = it },
                options = listOf("RJ", "SP", "MG").map { DSOption(it, it) },
                label = "Estado"
            )

            DSSegmentedButton(
                selection = deal,
                onSelectionChange = { deal = it },
                options = listOf(DSOption("sale", "Venda"), DSOption("rent", "Aluguel"))
            )

            DSRangeSlider(
                value = price,
                onValueChange = { price = it },
                label = "Preço (R$ mil)",
                valueRange = 0f..10f,
                steps = 9,
                minimumDistance = 1f,
                valueText = "${price.start.roundToInt() * 100} – ${price.endInclusive.roundToInt() * 100}"
            )

            Column(Modifier.selectableGroup()) {
                listOf("Apartamento", "Casa").forEach { option ->
                    DSRadioListItem(option, selected = kind == option, onClick = { kind = option })
                }
            }

            DSCheckboxListItem("Piscina", checked = pool, onCheckedChange = { pool = it })

            DSCheckbox("Li e aceito os termos", checked = terms, onCheckedChange = { terms = it })
        }
    }
}

@Composable
private fun FeedbackScreen() {
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var section by remember { mutableIntStateOf(0) }
    var refreshing by remember { mutableStateOf(false) }
    var alert by remember { mutableStateOf(false) }
    var sheet by remember { mutableStateOf(false) }
    var overlay by remember { mutableStateOf(false) }
    var progress by remember { mutableFloatStateOf(0.4f) }
    var offlineBanner by remember { mutableStateOf(true) }

    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(1_500)
            refreshing = false
        }
    }

    LaunchedEffect(overlay) {
        if (overlay) {
            delay(2_000)
            overlay = false
        }
    }

    Box(Modifier.fillMaxSize()) {
        DSScreen(
            title = "Feedback",
            snackbarHostState = snackbar,
            isRefreshing = refreshing,
            onRefresh = { refreshing = true }
        ) { padding ->
            LazyColumn(
                contentPadding = DSScreenDefaults.contentPadding(padding),
                verticalArrangement = Arrangement.spacedBy(DSSpacing.lg)
            ) {
                item { DSTabs(tabs = listOf("Estados", "Sobreposições"), selectedIndex = section, onSelect = { section = it }) }

                if (section == 0) {
                    if (offlineBanner) {
                        item {
                            DSBanner(
                                message = "Sem conexão. Mostrando os dados salvos.",
                                style = DSBannerStyle.Warning,
                                onDismiss = { offlineBanner = false }
                            )
                        }
                    }
                    item { DSSkeletonRow() }
                    item { Text("Carregando título", style = DSTheme.typography.titleMedium, modifier = Modifier.dsSkeleton(true)) }
                    item { DSLinearProgress(progress = progress) }
                    item { DSTonalButton("Avançar progresso", onClick = { progress = (progress + 0.2f) % 1.2f }) }
                    item {
                        DSEmptyState(
                            icon = DSIcons.Inbox,
                            title = "Nada por aqui",
                            message = "Publique um imóvel para começar.",
                            actionText = "Publicar",
                            onAction = {}
                        )
                    }
                    item { DSErrorState(title = "Não carregou", message = "Confira a conexão e tente de novo.", onRetry = {}) }
                } else {
                    item {
                        DSButton("Snackbar de sucesso", onClick = {
                            scope.launch { snackbar.showDSSnackbar("Imóvel publicado", DSSnackbarStyle.Success, actionLabel = "Ver") }
                        })
                    }
                    item {
                        DSOutlinedButton("Snackbar de erro", onClick = {
                            scope.launch { snackbar.showDSSnackbar("Sem conexão", DSSnackbarStyle.Error) }
                        })
                    }
                    item { DSOutlinedButton("Diálogo", onClick = { alert = true }) }
                    item { DSOutlinedButton("Painel de ações", onClick = { sheet = true }) }
                    item { DSOutlinedButton("Carregamento bloqueante", onClick = { overlay = true }) }
                }
            }
        }

        DSLoadingOverlay(visible = overlay, message = "Publicando…")
    }

    if (alert) {
        DSAlertDialog(
            title = "Excluir publicação?",
            message = "Ela sai do feed e os parceiros deixam de vê-la.",
            confirmText = "Excluir",
            dismissText = "Cancelar",
            destructive = true,
            onConfirm = { alert = false },
            onDismissRequest = { alert = false }
        )
    }

    DSBottomSheet(visible = sheet, onDismissRequest = { sheet = false }, title = "Publicação") {
        DSSheetAction("Editar", onClick = { sheet = false }, icon = DSIcons.Edit)

        DSSheetAction("Compartilhar", onClick = { sheet = false }, icon = DSIcons.Share)

        DSSheetAction("Excluir", onClick = { sheet = false }, icon = DSIcons.Delete, destructive = true)
    }
}

private val Neighborhoods = listOf("Botafogo", "Copacabana", "Gávea", "Ipanema", "Jardim Botânico", "Lagoa", "Laranjeiras", "Leblon")

@Composable
private fun SettingsScreen(
    dark: Boolean,
    onDarkChange: (Boolean) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit,
    brand: SampleBrand,
    onBrandChange: (SampleBrand) -> Unit
) {
    var notifications by remember { mutableStateOf(true) }

    DSScreen(title = "Ajustes", topBarStyle = DSTopAppBarStyle.Large) { padding ->
        LazyColumn(contentPadding = DSScreenDefaults.contentPadding(padding, horizontalMargin = 0.dp)) {
            item { DSListSectionHeader("Aparência") }

            item { DSSwitchListItem("Tema escuro", checked = dark, onCheckedChange = onDarkChange) }

            item {
                DSSwitchListItem(
                    "Cores do papel de parede",
                    supporting = "Material You, no Android 12 ou mais novo",
                    checked = dynamicColor,
                    onCheckedChange = onDynamicColorChange
                )
            }

            item {
                DSDropdownField(
                    selection = brand,
                    onSelectionChange = onBrandChange,
                    options = SampleBrand.entries.map { DSOption(it, it.label) },
                    label = "Cor da marca",
                    supportingText = "Gerada por DSBrand.fromSeed; vale quando as cores do papel de parede estão desligadas",
                    modifier = Modifier.padding(horizontal = DSSpacing.lg, vertical = DSSpacing.sm)
                )
            }

            item { DSListSectionHeader("Conta") }

            item { DSListItem("Perfil", supporting = "Nome, foto e CRECI", leadingIcon = DSIcons.Person, onClick = {}) }

            item { DSSwitchListItem("Notificações", checked = notifications, onCheckedChange = { notifications = it }) }

            item { DSListItem("Sair", leadingIcon = DSIcons.Lock, onClick = {}) }
        }
    }
}
