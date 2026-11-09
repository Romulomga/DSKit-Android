package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import io.github.romulomga.dskit.components.R
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale

/**
 * Campo para um número inteiro, sobre o [DSTextField]: teclado numérico e o valor já convertido. Texto que
 * não é número vira `null` e mostra [invalidNumberMessage]; campo vazio também é `null`, sem erro. Para um número
 * com casas decimais, use o [DSDecimalField].
 *
 * @param value o número atual, ou `null` se vazio ou inválido.
 * @param onValueChange chamado a cada edição com o número convertido.
 */
@Composable
public fun DSNumberField(
    value: Int?,
    onValueChange: (Int?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    errorText: String? = null,
    invalidNumberMessage: String = stringResource(R.string.dskit_enter_valid_number),
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    ParsedNumberField(
        value = value,
        onValueChange = onValueChange,
        parse = ::parseNumber,
        format = Int::toString,
        label = label,
        modifier = modifier,
        placeholder = null,
        supportingText = supportingText,
        errorText = errorText,
        invalidNumberMessage = invalidNumberMessage,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = imeAction),
        keyboardActions = keyboardActions
    )
}

/**
 * Campo para um número com casas decimais (peso, dose, quantidade), sobre o [DSTextField]: teclado decimal e o
 * valor já convertido. Como no [DSNumberField], texto que não é número vira `null` e mostra
 * [invalidNumberMessage]; campo vazio também é `null`, sem erro.
 *
 * Vírgula e ponto valem como separador decimal em qualquer idioma (alguns teclados só oferecem o ponto, e ele não
 * pode ser descartado: "0.5" virar 50 é erro grave numa dose). O valor que chega de fora aparece com o separador
 * do idioma do aparelho, sem separador de milhar. Dígitos além de [maxFractionDigits] não entram.
 *
 * Para dinheiro, guarde centavos inteiros e use um [DSTextField] com `visualTransformation`: `Double` não serve
 * para valores monetários.
 *
 * @param value o número atual, ou `null` se vazio ou inválido.
 * @param onValueChange chamado a cada edição com o número convertido.
 * @param maxFractionDigits casas decimais aceitas na digitação e mostradas (2 em mg, 1 em kg).
 */
@Composable
public fun DSDecimalField(
    value: Double?,
    onValueChange: (Double?) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorText: String? = null,
    invalidNumberMessage: String = stringResource(R.string.dskit_enter_valid_number),
    maxFractionDigits: Int = 2,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    keyboardActions: KeyboardActions = KeyboardActions.Default
) {
    val locale = LocalConfiguration.current.locales[0]

    ParsedNumberField(
        value = value,
        onValueChange = onValueChange,
        parse = DSDecimalInput::parse,
        format = { DSDecimalInput.format(it, maxFractionDigits, locale) },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        supportingText = supportingText,
        errorText = errorText,
        invalidNumberMessage = invalidNumberMessage,
        enabled = enabled,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
        keyboardActions = keyboardActions,
        accepts = { DSDecimalInput.fractionDigits(it) <= maxFractionDigits }
    )
}

/** A regra do [DSDecimalField], pura para o teste: ler o que foi digitado e mostrar o valor. */
internal object DSDecimalInput {
    private val Number = Regex("-?(\\d+[.,]?\\d*|[.,]\\d+)")

    /** Vírgula e ponto valem como separador decimal. O que não é um número só (letras, dois separadores) é `null`. */
    fun parse(raw: String): Double? {
        val text = raw.trim()

        return if (Number.matches(text)) text.replace(',', '.').toDoubleOrNull() else null
    }

    /** Quantos dígitos há depois do primeiro separador. */
    fun fractionDigits(raw: String): Int {
        val separator = raw.indexOfFirst { it == ',' || it == '.' }

        return if (separator < 0) 0 else raw.drop(separator + 1).count(Char::isDigit)
    }

    /**
     * O valor com o separador decimal de [locale], em algarismos de 0 a 9 e sem separador de milhar ("1.234" se
     * leria como 1,234 ao editar). Idioma com outro separador (o árabe) fica com o ponto, que o campo sabe ler.
     */
    fun format(value: Double, maxFractionDigits: Int, locale: Locale): String {
        val plain = NumberFormat.getNumberInstance(Locale.ROOT).apply {
            isGroupingUsed = false
            maximumFractionDigits = maxFractionDigits
        }.format(value)

        return if (DecimalFormatSymbols.getInstance(locale).decimalSeparator == ',') plain.replace('.', ',') else plain
    }
}

/** O campo dos dois: guarda o texto digitado (que nem sempre é um número ainda, como "1,") e entrega o valor. */
@Composable
private fun <T : Any> ParsedNumberField(
    value: T?,
    onValueChange: (T?) -> Unit,
    parse: (String) -> T?,
    format: (T) -> String,
    label: String,
    modifier: Modifier,
    placeholder: String?,
    supportingText: String?,
    errorText: String?,
    invalidNumberMessage: String,
    enabled: Boolean,
    keyboardOptions: KeyboardOptions,
    keyboardActions: KeyboardActions,
    accepts: (String) -> Boolean = { true }
) {
    var text by rememberSaveable { mutableStateOf(value?.let(format).orEmpty()) }
    val invalid = remember(text) { text.isNotBlank() && parse(text) == null }
    // O que este campo já emitiu e o app ainda pode devolver atrasado (StateFlow, ViewModel assíncrono).
    val emitted = remember { ArrayDeque<T?>() }

    // Valor mudou por fora (reset, outro campo): o texto acompanha. Um valor que é eco do que o próprio campo
    // emitiu não conta, senão "1" chegando atrasado apagaria o "12" já digitado.
    LaunchedEffect(value) {
        when {
            parse(text) == value -> emitted.clear()
            value !in emitted -> text = value?.let(format).orEmpty()
        }
    }

    DSTextField(
        value = text,
        onValueChange = { newText ->
            if (accepts(newText)) {
                text = newText
                val number = parse(newText)
                emitted.addLast(number)
                if (emitted.size > EmittedHistory) emitted.removeFirst()
                onValueChange(number)
            }
        },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        supportingText = supportingText,
        errorText = errorText ?: invalidNumberMessage.takeIf { invalid },
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions
    )
}

private const val EmittedHistory = 16

private fun parseNumber(raw: String): Int? = raw.trim().takeIf { it.isNotEmpty() }?.toIntOrNull()

@DSPreviews
@Composable
private fun DSNumberFieldPreview() {
    var minimum by remember { mutableStateOf<Int?>(1) }
    var maximum by remember { mutableStateOf<Int?>(100) }

    DSPreviewContainer("Number field") {
        Row(horizontalArrangement = Arrangement.spacedBy(DSSpacing.md)) {
            DSNumberField(value = minimum, onValueChange = { minimum = it }, label = "Min", modifier = Modifier.weight(1f))

            DSNumberField(value = maximum, onValueChange = { maximum = it }, label = "Max", modifier = Modifier.weight(1f))
        }
    }
}

@DSPreviews
@Composable
private fun DSDecimalFieldPreview() {
    var weight by remember { mutableStateOf<Double?>(82.5) }

    DSPreviewContainer("Decimal field") {
        DSDecimalField(
            value = weight,
            onValueChange = { weight = it },
            label = "Weight",
            supportingText = "In kilograms",
            maxFractionDigits = 1
        )
    }
}
