package com.miapp.lotuz_2.utils

import android.content.Context

data class CartItem(val productId: Int, val quantity: Int)

class CartManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("LotuzLocal", Context.MODE_PRIVATE)

    fun getCart(): List<CartItem> {
        val json = prefs.getString("cart_items", null)
        if (json.isNullOrBlank()) return emptyList()
        val parts = json.split("|").filter { it.isNotBlank() }
        return parts.mapNotNull {
            val s = it.split(":")
            if (s.size == 2) CartItem(s[0].toInt(), s[1].toInt()) else null
        }
    }

    fun saveCart(items: List<CartItem>) {
        val str = items.joinToString("|") { "${it.productId}:${it.quantity}" }
        prefs.edit().putString("cart_items", str).apply()
    }

    fun add(productId: Int, qty: Int = 1) {
        val current = getCart().toMutableList()
        val idx = current.indexOfFirst { it.productId == productId }
        if (idx >= 0) current[idx] = CartItem(productId, current[idx].quantity + qty) else current.add(CartItem(productId, qty))
        saveCart(current)
    }

    fun remove(productId: Int) {
        saveCart(getCart().filter { it.productId != productId })
    }

    fun clear() {
        prefs.edit().remove("cart_items").apply()
    }
}

