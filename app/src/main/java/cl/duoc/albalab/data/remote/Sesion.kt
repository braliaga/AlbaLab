package cl.duoc.albalab.data.remote

// Token JWT del gestor. Vive en memoria: al cerrar la app hay que volver a ingresar (más seguro para un equipo compartido).
object Sesion {
    @Volatile
    var token: String? = null
        private set
    var nombre: String? = null
        private set

    val activa: Boolean get() = token != null

    fun iniciar(token: String, nombre: String) {
        this.token = token
        this.nombre = nombre
    }

    fun cerrar() {
        token = null
        nombre = null
    }
}
