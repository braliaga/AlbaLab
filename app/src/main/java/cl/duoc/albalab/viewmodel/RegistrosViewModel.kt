package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.data.model.TipoAyuda
import cl.duoc.albalab.repository.RegistroRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class RegistrosState(
    val total: Int = 0,
    val filtrados: List<RegistroInteres> = emptyList(),
    // false hasta la primera lectura de Room, para no mostrar "Aún no hay registros" mientras carga.
    val cargado: Boolean = false
)

// Filtro dinámico: se recalcula en cada letra o chip. Separado como función pura para testearlo sin flows.
internal fun filtrar(lista: List<RegistroInteres>, busqueda: String, tipo: TipoAyuda?): List<RegistroInteres> {
    val texto = busqueda.trim()
    return lista.filter { r ->
        (texto.isEmpty() || r.nombre.contains(texto, ignoreCase = true)) && (tipo == null || r.tipoAyuda == tipo)
    }
}

class RegistrosViewModel(repo: RegistroRepository) : ViewModel() {
    private val _busqueda = MutableStateFlow("")
    private val _tipo = MutableStateFlow<TipoAyuda?>(null)

    // El campo de texto lee directo de este flow (sin pasar por combine) para no perder letras al escribir rápido.
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()
    val tipo: StateFlow<TipoAyuda?> = _tipo.asStateFlow()

    val estado: StateFlow<RegistrosState> = combine(repo.obtenerTodos(), _busqueda, _tipo) { todos, busqueda, tipo ->
        RegistrosState(total = todos.size, filtrados = filtrar(todos, busqueda, tipo), cargado = true)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), RegistrosState())

    fun onBusqueda(texto: String) {
        _busqueda.value = texto
    }

    fun onTipo(tipo: TipoAyuda?) {
        _tipo.value = tipo
    }

    fun limpiarFiltros() {
        _busqueda.value = ""
        _tipo.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { RegistrosViewModel(repositorio()) }
        }
    }
}
