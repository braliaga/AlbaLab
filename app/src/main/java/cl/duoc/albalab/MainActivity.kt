package cl.duoc.albalab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.albalab.ui.navegacion.AppNavegacion
import cl.duoc.albalab.ui.theme.AlbaLabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AlbaLabTheme {
                AppNavegacion()
            }
        }
    }
}
