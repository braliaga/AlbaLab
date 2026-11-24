package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.remote.RedAlbaLab
import cl.duoc.albalab.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginState(
    val email: String = "",
    val password: String = "",
    val errorEmail: String? = null,
    val errorPassword: String? = null,
    val cargando: Boolean = false,
    val error: String? = null,
    val ingresoOk: Boolean = false
)

// Login del gestor de vinculación contra ms-auth. Las reglas de cada campo viven en Validaciones.
class LoginViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _estado = MutableStateFlow(LoginState())
    val estado: StateFlow<LoginState> = _estado.asStateFlow()

    fun onEmail(v: String) = _estado.update { it.copy(email = v, errorEmail = Validaciones.correo(v.trim()), error = null) }

    fun onPassword(v: String) = _estado.update {
        it.copy(password = v, errorPassword = Validaciones.clave(v), error = null)
    }

    fun ingresar() {
        val s = _estado.value
        val errorEmail = Validaciones.correo(s.email.trim())
        val errorPassword = Validaciones.clave(s.password)
        if (errorEmail != null || errorPassword != null) {
            _estado.update { it.copy(errorEmail = errorEmail, errorPassword = errorPassword) }
            return
        }
        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            auth.login(s.email, s.password)
                .onSuccess { _estado.update { it.copy(cargando = false, ingresoOk = true, password = "") } }
                .onFailure { e -> _estado.update { it.copy(cargando = false, error = e.mensaje()) } }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { LoginViewModel(AuthRepository(RedAlbaLab.api)) }
        }
    }
}
