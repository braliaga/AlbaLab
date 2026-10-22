package cl.duoc.albalab.ui

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cl.duoc.albalab.utils.ImagenStorage

@Composable
fun SelectorFoto(
    onFotoSeleccionada: (Uri) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imagenStorage = remember { ImagenStorage(context) }
    var uriTemporal by remember { mutableStateOf<Uri?>(null) }

    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { exito ->
        if (exito && uriTemporal != null) {
            onFotoSeleccionada(uriTemporal!!)
        }
    }

    val launcherGaleria = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onFotoSeleccionada(uri)
        }
    }

    val launcherPermisoCamara = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            uriTemporal = imagenStorage.crearArchivoImagen()
            launcherCamara.launch(uriTemporal!!)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(
            onClick = { launcherPermisoCamara.launch(Manifest.permission.CAMERA) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = "Cámara")
            Spacer(Modifier.width(8.dp))
            Text("Cámara")
        }

        OutlinedButton(
            onClick = { launcherGaleria.launch("image/*") },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.PhotoLibrary, contentDescription = "Galería")
            Spacer(Modifier.width(8.dp))
            Text("Galería")
        }
    }
}