package cl.duoc.albalab.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun PantallaRegistro(modifier: Modifier) {
    Column(modifier = modifier.padding(16.dp)) {
        Text("Registro de Interés")
        Text("Nombre")
        Text("Teléfono")
        Text("Correo")
        Text("Dirección")
        Text("Contacto preferido")
        Text("Tipo de ayuda")
    }
}