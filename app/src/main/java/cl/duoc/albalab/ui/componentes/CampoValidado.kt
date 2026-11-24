package cl.duoc.albalab.ui.componentes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun CampoValidado(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    error: String?,
    modifier: Modifier = Modifier,
    teclado: KeyboardOptions = KeyboardOptions.Default,
    // true para contraseñas: muestra puntos en vez del texto.
    ocultar: Boolean = false
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        isError = error != null,
        supportingText = {
            AnimatedVisibility(visible = error != null) {
                Text(error ?: "")
            }
        },
        trailingIcon = {
            if (error != null) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = error
                )
            }
        },
        keyboardOptions = teclado,
        visualTransformation = if (ocultar) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        modifier = modifier
    )
}
