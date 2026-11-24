package cl.duoc.albalab.repository

import cl.duoc.albalab.data.remote.ApiMindicador
import cl.duoc.albalab.data.remote.llamarApi

data class IndicadoresEconomicos(val uf: Double, val dolar: Double)

// API externa mindicador.cl: UF y dólar del día, como referencia para empresas y donantes.
class IndicadoresRepository(private val api: ApiMindicador) {
    suspend fun obtener(): Result<IndicadoresEconomicos> = llamarApi {
        val r = api.indicadores()
        IndicadoresEconomicos(uf = r.uf.valor, dolar = r.dolar.valor)
    }
}
