package io.github.romulomga.dskit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import io.github.romulomga.dskit.components.R

/**
 * Campo de texto do Material 3, com contorno e rótulo que flutua para a borda ao focar. Abaixo dele, o
 * texto de apoio ou, se houver, o erro (na cor de erro, e o TalkBack anuncia).
 *
 * @param label rótulo do campo; é também o que o TalkBack lê.
 * @param placeholder dica dentro do campo vazio, depois que o rótulo flutuou.
 * @param supportingText linha de apoio ("Mínimo 6 caracteres").
 * @param errorText mensagem de erro; não nula, o campo fica em estado de erro.
 * @param leadingIcon ícone decorativo à esquerda (busca, e-mail).
 * @param password esconde o texto e mostra o olho para alternar. Força o teclado de senha (sem sugestão nem
 * aprendizado, mesmo com [keyboardOptions] próprio) e avisa o autofill que o campo é senha.
 * @param clearable mostra o botão de limpar enquanto há texto.
 * @param singleLine `false` para textos longos, que crescem até [maxLines].
 * @param contentType o que o campo guarda, para o autofill do sistema e os gerenciadores de senha
 * (`ContentType.EmailAddress`, `ContentType.Username`, `ContentType.NewPassword`…). Com [password] e sem
 * este, é `ContentType.Password`.
 */
@Composable
public fun DSTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    supportingText: String? = null,
    errorText: String? = null,
    leadingIcon: ImageVector? = null,
    password: Boolean = false,
    clearable: Boolean = false,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    contentType: ContentType? = null
) {
    var revealed by rememberSaveable { mutableStateOf(false) }
    val isError = errorText != null
    val autofillType = contentType ?: if (password) ContentType.Password else null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .then(if (errorText != null) Modifier.semantics { error(errorText) } else Modifier)
            .then(if (autofillType != null) Modifier.semantics { this.contentType = autofillType } else Modifier),
        enabled = enabled,
        readOnly = readOnly,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null) } },
        trailingIcon = when {
            password -> {
                {
                    val description = stringResource(if (revealed) R.string.dskit_hide_password else R.string.dskit_show_password)
                    IconButton(onClick = { revealed = !revealed }) {
                        Icon(if (revealed) DSIcons.VisibilityOff else DSIcons.Visibility, contentDescription = description)
                    }
                }
            }

            clearable && value.isNotEmpty() && enabled && !readOnly -> {
                {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(DSIcons.Cancel, contentDescription = stringResource(R.string.dskit_clear))
                    }
                }
            }

            isError -> {
                { Icon(DSIcons.Error, contentDescription = null) }
            }

            else -> null
        },
        supportingText = (errorText ?: supportingText)?.let { { Text(it) } },
        isError = isError,
        visualTransformation = if (password && !revealed) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (password) {
            keyboardOptions.copy(keyboardType = KeyboardType.Password, autoCorrectEnabled = false)
        } else {
            keyboardOptions
        },
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines
    )
}

@DSPreviews
@Composable
private fun DSTextFieldPreview() {
    DSPreviewContainer("Text field") {
        Column(verticalArrangement = Arrangement.spacedBy(DSSpacing.sm)) {
            DSTextField(value = "", onValueChange = {}, label = "Name", supportingText = "As it shows on your profile")

            DSTextField(value = "secret", onValueChange = {}, label = "Password", password = true)

            DSTextField(value = "broker@", onValueChange = {}, label = "Email", errorText = "Invalid email")

            DSTextField(value = "Ipanema", onValueChange = {}, label = "Search", leadingIcon = DSIcons.Search, clearable = true)
        }
    }
}
