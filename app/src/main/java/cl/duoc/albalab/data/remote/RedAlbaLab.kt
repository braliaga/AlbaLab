package cl.duoc.albalab.data.remote

import cl.duoc.albalab.BuildConfig
import java.util.concurrent.TimeUnit
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Un cliente Retrofit por servicio. Las APIs externas usan su propio cliente, separado de los microservicios.
object RedAlbaLab {
    // Agrega "Authorization: Bearer <token>" si el gestor inició sesión; el gateway lo exige para gestionar solicitudes.
    private val interceptorToken = Interceptor { cadena ->
        val token = Sesion.token
        val peticion = if (token != null) {
            cadena.request().newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            cadena.request()
        }
        cadena.proceed(peticion)
    }

    private val log = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }

    private fun cliente(conToken: Boolean) = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .apply { if (conToken) addInterceptor(interceptorToken) }
        .addInterceptor(log)
        .build()

    private fun retrofit(baseUrl: String, conToken: Boolean = false) = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(cliente(conToken))
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiAlbaLab by lazy { retrofit(BuildConfig.API_BASE_URL, conToken = true).create(ApiAlbaLab::class.java) }

    val mindicador: ApiMindicador by lazy { retrofit("https://mindicador.cl/").create(ApiMindicador::class.java) }

    val feriados: ApiFeriados by lazy { retrofit("https://date.nager.at/").create(ApiFeriados::class.java) }
}
