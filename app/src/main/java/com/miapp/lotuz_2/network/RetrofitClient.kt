package com.miapp.lotuz_2.network

import android.content.Context
import com.miapp.lotuz_2.BuildConfig // IMPORTANTE: Debe ser tu paquete real
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private var apiInstance: XanoApi? = null

    fun getInstance(context: Context): XanoApi {
        if (apiInstance == null) {

            // Configurar Logs
            val logging = HttpLoggingInterceptor().apply {
                level = if (com.miapp.lotuz_2.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

            // Configurar Cliente OKHTTP con Timeouts de 30s
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context)) // Agregar nuestro interceptor
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            // Construir Retrofit
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.XANO_BASE_URL) // Usa la URL definida en build.gradle
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            apiInstance = retrofit.create(XanoApi::class.java)
        }
        return apiInstance!!
    }
}
