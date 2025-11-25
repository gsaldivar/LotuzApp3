package com.miapp.lotuz_2.model

import java.io.Serializable

data class Product(
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: Int, // Precio en CLP
    val stock: Int,
    val image: ImageObj? = null,
    val images: List<ImageObj>? = null
) : Serializable

// Clase auxiliar para mapear la estructura de imagen de Xano
data class ImageObj(
    val url: String
)
