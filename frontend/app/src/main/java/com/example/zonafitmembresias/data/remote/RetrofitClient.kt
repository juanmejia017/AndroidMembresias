package com.example.zonafitmembresias.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Punto unico de construccion del cliente Retrofit (patron Singleton),
 * igual que se hace con SessionLocal en el backend: evita crear un
 * OkHttpClient/Retrofit nuevo cada vez que se necesita llamar a la API.
 */
object RetrofitClient {

    // URL del backend desplegado en Render. El plan gratuito "duerme"
    // el servicio tras inactividad, por eso los timeouts de abajo son
    // generosos (60s) en vez del valor por defecto.
    private const val BASE_URL = "https://androidmembresias-1.onrender.com/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}