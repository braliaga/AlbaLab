package cl.duoc.albalab.data.remote

import cl.duoc.albalab.data.remote.dto.CambioEstadoDto
import cl.duoc.albalab.data.remote.dto.CasoExitoDto
import cl.duoc.albalab.data.remote.dto.DestinoAporteDto
import cl.duoc.albalab.data.remote.dto.FeriadoDto
import cl.duoc.albalab.data.remote.dto.IndicadorDto
import cl.duoc.albalab.data.remote.dto.LoginDto
import cl.duoc.albalab.data.remote.dto.MindicadorDto
import cl.duoc.albalab.data.remote.dto.ProductoDto
import cl.duoc.albalab.data.remote.dto.ProgramaDto
import cl.duoc.albalab.data.remote.dto.SolicitudDto
import cl.duoc.albalab.data.remote.dto.SolicitudRequestDto
import cl.duoc.albalab.data.remote.dto.SolicitudUpdateDto
import cl.duoc.albalab.data.remote.dto.TokenDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Microservicios propios, siempre a través del api-gateway (puerto 9090).
interface ApiAlbaLab {
    @POST("auth/login")
    suspend fun login(@Body datos: LoginDto): TokenDto

    @GET("solicitudes")
    suspend fun listarSolicitudes(): List<SolicitudDto>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body datos: SolicitudRequestDto): SolicitudDto

    @PUT("solicitudes/{id}")
    suspend fun actualizarSolicitud(@Path("id") id: Int, @Body datos: SolicitudUpdateDto): SolicitudDto

    @PATCH("solicitudes/{id}/estado")
    suspend fun cambiarEstado(@Path("id") id: Int, @Body datos: CambioEstadoDto): SolicitudDto

    // Response<Unit> porque el DELETE responde 204 sin cuerpo.
    @DELETE("solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Int): Response<Unit>

    @GET("programas")
    suspend fun programas(): List<ProgramaDto>

    @GET("casos")
    suspend fun casos(): List<CasoExitoDto>

    @GET("productos")
    suspend fun productos(): List<ProductoDto>

    @GET("indicadores")
    suspend fun indicadores(): List<IndicadorDto>

    @GET("destinos")
    suspend fun destinos(): List<DestinoAporteDto>
}

// API externa 1: indicadores económicos de Chile (https://mindicador.cl/api), sin clave.
interface ApiMindicador {
    @GET("api")
    suspend fun indicadores(): MindicadorDto
}

// API externa 2: feriados de Chile (https://date.nager.at), sin clave.
interface ApiFeriados {
    @GET("api/v3/PublicHolidays/{anio}/CL")
    suspend fun feriados(@Path("anio") anio: Int): List<FeriadoDto>
}
