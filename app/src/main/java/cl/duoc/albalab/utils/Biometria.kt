package cl.duoc.albalab.utils

import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

fun autenticarConBiometria(
    activity: FragmentActivity,
    titulo: String = "Acceso restringido",
    subtitulo: String = "Verifica tu identidad para ver los registros",
    onExito: () -> Unit,
    onError: (String) -> Unit
) {
    // STRONG + DEVICE_CREDENTIAL no está soportado en API 28-29 y lanza IllegalArgumentException; WEAK sí.
    val biometria = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        BiometricManager.Authenticators.BIOMETRIC_STRONG
    } else {
        BiometricManager.Authenticators.BIOMETRIC_WEAK
    }
    val autenticadores = biometria or BiometricManager.Authenticators.DEVICE_CREDENTIAL

    val biometricManager = BiometricManager.from(activity)
    when (biometricManager.canAuthenticate(autenticadores)) {
        BiometricManager.BIOMETRIC_SUCCESS -> {
            val executor = ContextCompat.getMainExecutor(activity)
            val prompt = BiometricPrompt(
                activity,
                executor,
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        onExito()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        onError(errString.toString())
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        onError("Huella no reconocida. Intenta nuevamente.")
                    }
                }
            )

            val promptInfo = BiometricPrompt.PromptInfo.Builder()
                .setTitle(titulo)
                .setSubtitle(subtitulo)
                .setAllowedAuthenticators(autenticadores)
                .build()

            prompt.authenticate(promptInfo)
        }
        BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
            onError("Configura una huella o PIN en el dispositivo para acceder.")
        }
        else -> {
            onError("Autenticación biométrica no disponible en este dispositivo.")
        }
    }
}