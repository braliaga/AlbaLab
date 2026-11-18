package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModel
import cl.duoc.albalab.data.model.CasoExito
import cl.duoc.albalab.data.model.IndicadorImpacto
import cl.duoc.albalab.data.model.ProductoCatalogo
import cl.duoc.albalab.data.model.ProgramaInfo
import cl.duoc.albalab.repository.ContenidoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ContenidoState(
    val programas: List<ProgramaInfo> = emptyList(),
    val casosExito: List<CasoExito> = emptyList(),
    val catalogo: List<ProductoCatalogo> = emptyList(),
    val indicadores: List<IndicadorImpacto> = emptyList()
)

class ContenidoViewModel(
    private val repository: ContenidoRepository = ContenidoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContenidoState())
    val uiState: StateFlow<ContenidoState> = _uiState.asStateFlow()

    init {
        cargarContenido()
    }

    private fun cargarContenido() {
        _uiState.value = ContenidoState(
            programas = repository.obtenerProgramas(),
            casosExito = repository.obtenerCasosExito(),
            catalogo = repository.obtenerCatalogo(),
            indicadores = repository.obtenerIndicadores()
        )
    }
}