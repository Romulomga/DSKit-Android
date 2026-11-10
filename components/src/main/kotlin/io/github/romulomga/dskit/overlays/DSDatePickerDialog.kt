package io.github.romulomga.dskit

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.romulomga.dskit.components.R
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/**
 * O calendário do Material 3 (`DatePickerDialog` + `DatePicker`) falando `LocalDate`: abre em [date], e o "OK"
 * entrega o dia escolhido. Quem chama controla a visibilidade e desenha o gatilho (uma linha de lista, um botão
 * com a data): mostre o diálogo enquanto um estado estiver ligado e desligue-o em [onConfirm] e em
 * [onDismissRequest].
 *
 * ```
 * if (pickingDate) {
 *     DSDatePickerDialog(
 *         date = issuedOn,
 *         onConfirm = { issuedOn = it; pickingDate = false },
 *         onDismissRequest = { pickingDate = false },
 *         range = LocalDate.of(2020, 1, 1)..LocalDate.now()
 *     )
 * }
 * ```
 *
 * A conversão do dia para os milissegundos UTC do Material fica aqui dentro, e os anos mostrados ficam sempre
 * entre 1900 e 2100: o calendário monta um mês para cada ano da faixa, e uma faixa como `LocalDate.MIN..hoje`
 * estourava a conta e fechava o app.
 *
 * @param date o dia selecionado ao abrir; `null` abre no mês de hoje sem nada escolhido, e o "OK" espera a escolha.
 * @param onConfirm o "OK", com o dia escolhido. Não fecha o diálogo sozinho.
 * @param onDismissRequest o "Cancelar", o toque fora e o voltar.
 * @param range faixa válida (nascimento não pode ser no futuro); fora dela o dia e o ano não se escolhem.
 * @param showModeToggle mostra o lápis que troca o calendário pela digitação da data.
 * @param confirmText por padrão "OK".
 * @param dismissText por padrão "Cancelar".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
public fun DSDatePickerDialog(
    date: LocalDate?,
    onConfirm: (LocalDate) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    range: ClosedRange<LocalDate>? = null,
    showModeToggle: Boolean = true,
    confirmText: String = stringResource(R.string.dskit_ok),
    dismissText: String = stringResource(R.string.dskit_cancel)
) {
    val selected = DSDatePickerBounds.selectable(date, range)
    val state = rememberDatePickerState(
        initialSelectedDateMillis = selected?.toUtcMillis(),
        initialDisplayedMonthMillis = (selected ?: DSDatePickerBounds.today(range)).toUtcMillis(),
        yearRange = DSDatePickerBounds.yearRange(range),
        selectableDates = remember(range) { rangeSelectableDates(range) }
    )

    DatePickerDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            DSTextButton(
                text = confirmText,
                onClick = { state.selectedDateMillis?.let { onConfirm(it.toLocalDate()) } },
                enabled = state.selectedDateMillis != null
            )
        },
        modifier = modifier,
        dismissButton = { DSTextButton(text = dismissText, onClick = onDismissRequest) }
    ) {
        DatePicker(state = state, showModeToggle = showModeToggle)
    }
}

/** Os limites que o calendário do Material aguenta, puros para o teste. */
internal object DSDatePickerBounds {
    const val MinYear: Int = 1900
    const val MaxYear: Int = 2100

    /** Os anos do calendário: os de [range], sempre dentro de [MinYear]..[MaxYear]. */
    fun yearRange(range: ClosedRange<LocalDate>?): IntRange {
        val first = (range?.start?.year ?: MinYear).coerceIn(MinYear, MaxYear)
        val last = (range?.endInclusive?.year ?: MaxYear).coerceIn(first, MaxYear)

        return first..last
    }

    /**
     * O dia que o calendário abre escolhido: [date] se couber em [range] e nos anos do calendário, senão nenhum. O
     * Material lança exceção quando o ano do dia inicial está fora dos anos do calendário (o relógio do aparelho voltou,
     * um registro antigo fora da faixa), e um dia que não se pode escolher não deve abrir escolhido.
     */
    fun selectable(date: LocalDate?, range: ClosedRange<LocalDate>?): LocalDate? =
        date?.takeIf { it.year in yearRange(range) && (range == null || it in range) }

    /** Hoje, puxado para dentro de [range]: o mês em que o calendário abre quando não há dia escolhido. */
    fun today(range: ClosedRange<LocalDate>?, today: LocalDate = LocalDate.now()): LocalDate {
        val limit = LocalDate.of(MinYear, 1, 1)..LocalDate.of(MaxYear, 12, 31)
        val inRange = if (range == null || range.isEmpty()) today else today.coerceIn(range.start, range.endInclusive)

        return inRange.coerceIn(limit.start, limit.endInclusive)
    }

    /** O dia (meia-noite UTC, como o Material o entrega) só se escolhe dentro de [range]. */
    fun isSelectable(utcTimeMillis: Long, range: ClosedRange<LocalDate>?): Boolean = range == null || utcTimeMillis.toLocalDate() in range

    fun isSelectableYear(year: Int, range: ClosedRange<LocalDate>?): Boolean =
        year in yearRange(range) && (range == null || year in range.start.year..range.endInclusive.year)
}

@OptIn(ExperimentalMaterial3Api::class)
private fun rangeSelectableDates(range: ClosedRange<LocalDate>?): SelectableDates = object : SelectableDates {
    override fun isSelectableDate(utcTimeMillis: Long): Boolean = DSDatePickerBounds.isSelectable(utcTimeMillis, range)

    override fun isSelectableYear(year: Int): Boolean = DSDatePickerBounds.isSelectableYear(year, range)
}

/** O `DatePicker` do Material trabalha com a meia-noite UTC do dia. */
private fun LocalDate.toUtcMillis(): Long = atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private fun Long.toLocalDate(): LocalDate = Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate()

@DSPreviews
@Composable
private fun DSDatePickerDialogPreview() {
    DSPreviewContainer("Date picker dialog") {
        DSDatePickerDialog(
            date = LocalDate.of(2026, 6, 29),
            onConfirm = {},
            onDismissRequest = {},
            range = LocalDate.of(2026, 1, 1)..LocalDate.of(2026, 12, 31)
        )
    }
}
