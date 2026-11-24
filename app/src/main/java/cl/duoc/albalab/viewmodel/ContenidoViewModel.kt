package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.model.CasoExito
import cl.duoc.albalab.data.model.DestinoAporte
import cl.duoc.albalab.data.model.IndicadorImpacto
import cl.duoc.albalab.data.model.ProductoCatalogo
import cl.duoc.albalab.data.model.ProgramaInfo
import cl.duoc.albalab.data.remote.RedAlbaLab
import cl.duoc.albalab.repository.ContenidoRepository
import cl.duoc.albalab.repository.Contenidos
import cl.duoc.albalab.repository.Feriado
import cl.duoc.albalab.repository.FeriadosRepository
import cl.duoc.albalab.repository.IndicadoresEconomicos
import cl.duoc.albalab.repository.IndicadoresRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContenidoState(
    val bannerInicioUrl: String = "",
    val propositoInstitucional: String = "",
    val programas: List<ProgramaInfo> = emptyList(),
    val casosExito: List<CasoExito> = emptyList(),
    val destinoAportes: List<DestinoAporte> = emptyList(),
    val catalogo: List<ProductoCatalogo> = emptyList(),
    val indicadores: List<IndicadorImpacto> = emptyList(),
    // false mientras se muestra la copia local (backend apagado o aún cargando).
    val desdeServidor: Boolean = false,
    // APIs externas: null mientras cargan; si fallan, el flag de error muestra un aviso sin afectar el resto.
    val economia: IndicadoresEconomicos? = null,
    val errorEconomia: Boolean = false,
    val proximoFeriado: Feriado? = null,
    val errorFeriado: Boolean = false
)

class ContenidoViewModel(
    private val repository: ContenidoRepository,
    private val indicadores: IndicadoresRepository,
    private val feriados: FeriadosRepository
) : ViewModel() {

    // Arranca con la copia local para que Inicio, Programas y Catálogo nunca aparezcan vacíos.
    private val _uiState = MutableStateFlow(conContenidos(ContenidoState(), repository.local()))
    val uiState: StateFlow<ContenidoState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            val contenidos = repository.obtener()
            _uiState.update { conContenidos(it, contenidos) }
        }
        viewModelScope.launch {
            indicadores.obtener()
                .onSuccess { e -> _uiState.update { it.copy(economia = e, errorEconomia = false) } }
                .onFailure { _uiState.update { it.copy(errorEconomia = true) } }
        }
        viewModelScope.launch {
            feriados.proximo()
                .onSuccess { f -> _uiState.update { it.copy(proximoFeriado = f, errorFeriado = false) } }
                .onFailure { _uiState.update { it.copy(errorFeriado = true) } }
        }
    }

    private fun conContenidos(s: ContenidoState, c: Contenidos) = s.copy(
        bannerInicioUrl = repository.obtenerBannerInicioUrl(),
        propositoInstitucional = repository.obtenerPropositoInstitucional(),
        programas = c.programas,
        casosExito = c.casosExito,
        catalogo = c.catalogo,
        indicadores = c.indicadores,
        destinoAportes = c.destinoAportes,
        desdeServidor = c.desdeServidor
    )

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                ContenidoViewModel(
                    ContenidoRepository(RedAlbaLab.api),
                    IndicadoresRepository(RedAlbaLab.mindicador),
                    FeriadosRepository(RedAlbaLab.feriados)
                )
            }
        }
    }
}
