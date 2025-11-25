package com.miapp.lotuz_2.network

import com.miapp.lotuz_2.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface XanoApi {

    // --- AUTENTICACIÓN ---

    @POST("auth/login")
    suspend fun login(@Body credentials: Map<String, String>): Response<AuthResponse>

    @POST("login")
    suspend fun loginPlain(@Body credentials: Map<String, String>): Response<AuthResponse>

    @POST("auth/signup")
    suspend fun signup(@Body userData: Map<String, String>): Response<AuthResponse>

    // Endpoint autenticado para obtener datos del usuario (rol, nombre, etc.)
    @GET("auth/me")
    suspend fun getMe(): Response<User>

    @POST("auth/forgot_password")
    suspend fun forgotPassword(@Body payload: Map<String, String>): Response<Any>


    // --- PRODUCTOS ---

    @GET("product")
    suspend fun getProducts(): Response<List<Product>>

    // Subida de producto con imagen (Multipart)
    @Multipart
    @POST("product")
    suspend fun createProduct(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Product>

    @Multipart
    @POST("products")
    suspend fun createProductAlt(
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("price") price: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part image: MultipartBody.Part?
    ): Response<Product>

    @DELETE("product/{id}")
    suspend fun deleteProduct(@Path("id") id: Int): Response<Any>

    


    // --- ÓRDENES ---

    @POST("order")
    suspend fun createOrder(@Body orderData: Map<String, Any>): Response<Any>

    @GET("order")
    suspend fun getOrders(): Response<List<Order>>
}
