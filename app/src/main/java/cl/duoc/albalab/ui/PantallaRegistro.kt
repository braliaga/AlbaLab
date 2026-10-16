package cl.duoc.albalab.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun PantallaRegistro(modifier: Modifier) {
    //  validación campo nombre
    var yaEscribio by remember { mutableStateOf( false) }
    var nombre by remember { mutableStateOf( "") }
    val estaVacio = nombre.isBlank()
    val mostrarError = estaVacio && yaEscribio

    // validacion campo telefono
    var telefono by remember { mutableStateOf( "") }
    var yaEscribioTelefono by remember { mutableStateOf( false) }
    val tieneNueveDigitos = telefono.length == 9
    val tieneSoloDigitos = telefono.all { it.isDigit() }
    val mostrarErrorTelefono = (!tieneNueveDigitos || !tieneSoloDigitos) && yaEscribioTelefono

    var correo by remember { mutableStateOf( "") }
    var direccion by remember { mutableStateOf( "") }

    Column(modifier = modifier.padding(16.dp)) {
        Text("Registro de Interés")
        OutlinedTextField(
            value = nombre,
            onValueChange = {
                nombre = it
                yaEscribio = true},
            label = { Text("Nombre y apellido") },
            isError = mostrarError,
            supportingText = {if (mostrarError) Text("El campo no puede estar vacío")},
            trailingIcon = {
                if(mostrarError) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "El campo no puede estar vacío",
                    )
                }
            }
        )

        OutlinedTextField(
            value = telefono,
            onValueChange = {
                telefono = it
                yaEscribioTelefono = true },
            label = { Text("Teléfono") },
            isError = mostrarErrorTelefono,
            supportingText = {if (mostrarErrorTelefono) Text("Solo se permiten 9 dígitos, sin espacios vacíos ni símbolos ni carácteres")},
            trailingIcon = {
                if (mostrarErrorTelefono) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = "Solo se permiten 9 dígitos, sin espacios vacíos ni símbolos ni carácteres"
                    )
                }
            }
        )

        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo") }
        )

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección") }
        )
    }
}