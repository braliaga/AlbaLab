package cl.duoc.albalab.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cl.duoc.albalab.data.model.RegistroInteres
import cl.duoc.albalab.repository.RegistroRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class RegistrosViewModel(repo: RegistroRepository) : ViewModel() {
    val registros: StateFlow<List<RegistroInteres>> = repo.obtenerTodos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer { RegistrosViewModel(repositorio()) }
        }
    }
}
