package com.miapp.lotuz_2.utils

import android.content.Context
import com.google.gson.Gson
import com.miapp.lotuz_2.model.Product

class LocalStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("LotuzLocal", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getProducts(): List<Product> {
        val json = prefs.getString("local_products", null)
        return if (json.isNullOrBlank()) emptyList() else gson.fromJson(json, Array<Product>::class.java).toList()
    }

    fun saveProducts(list: List<Product>) {
        prefs.edit().putString("local_products", gson.toJson(list)).apply()
    }

    fun addProduct(p: Product) {
        val list = getProducts().toMutableList()
        list.add(p)
        saveProducts(list)
    }

    fun deleteProduct(id: Int) {
        val list = getProducts().filter { it.id != id }
        saveProducts(list)
    }

    fun getOrders(): List<com.miapp.lotuz_2.model.Order> {
        val json = prefs.getString("local_orders", null)
        return if (json.isNullOrBlank()) emptyList() else gson.fromJson(json, Array<com.miapp.lotuz_2.model.Order>::class.java).toList()
    }

    fun saveOrders(list: List<com.miapp.lotuz_2.model.Order>) {
        prefs.edit().putString("local_orders", gson.toJson(list)).apply()
    }

    fun addOrder(o: com.miapp.lotuz_2.model.Order) {
        val list = getOrders().toMutableList()
        list.add(o)
        saveOrders(list)
    }
}
