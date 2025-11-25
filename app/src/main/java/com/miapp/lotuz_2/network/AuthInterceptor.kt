package com.miapp.lotuz_2.network

import android.content.Context
import com.miapp.lotuz_2.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        // Recuperamos el token de la sesión
        val session = SessionManager(context)
        val token = session.getToken()

        // Si existe el token, lo agregamos a la cabecera Authorization
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}