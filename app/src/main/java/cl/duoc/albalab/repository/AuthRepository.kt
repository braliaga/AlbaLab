package cl.duoc.albalab.repository

import cl.duoc.albalab.data.remote.ApiAlbaLab
import cl.duoc.albalab.data.remote.Sesion
import cl.duoc.albalab.data.remote.dto.LoginDto
import cl.duoc.albalab.data.remote.llamarApi

// Login contra ms-auth (vía gateway). El token queda en Sesion y el interceptor lo agrega a cada llamada.
class AuthRepository(private val api: ApiAlbaLab) {
    suspend fun login(email: String, password: String): Result<String> = llamarApi {
        val respuesta = api.login(LoginDto(email.trim(), password))
        Sesion.iniciar(respuesta.token, respuesta.nombre)
        respuesta.nombre
    }

    fun cerrarSesion() = Sesion.cerrar()
}
