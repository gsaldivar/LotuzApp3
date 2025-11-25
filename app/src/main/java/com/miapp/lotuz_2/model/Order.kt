package com.miapp.lotuz_2.model

import java.io.Serializable

data class OrderItem(
    val productId: Int,
    val quantity: Int,
    val unitPrice: Int
) : Serializable

data class Order(
    val id: Int,
    val items: List<OrderItem>,
    val total: Int,
    val status: String, // pending, accepted, rejected, shipped
    val createdAt: Long
) : Serializable

