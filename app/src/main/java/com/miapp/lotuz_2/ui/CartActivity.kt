package com.miapp.lotuz_2.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.lotuz_2.R
import com.miapp.lotuz_2.model.Order
import com.miapp.lotuz_2.model.OrderItem
import com.miapp.lotuz_2.model.Product
import com.miapp.lotuz_2.network.RetrofitClient
import com.miapp.lotuz_2.utils.CartManager
import com.miapp.lotuz_2.utils.LocalStore
import kotlinx.coroutines.launch

class CartActivity : AppCompatActivity() {

    private lateinit var rv: androidx.recyclerview.widget.RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var btnCheckout: Button
    private lateinit var btnClear: Button
    private lateinit var adapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        rv = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        btnCheckout = findViewById(R.id.btnCheckout)
        btnClear = findViewById(R.id.btnClear)
        findViewById<Button>(R.id.btnBack).setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        rv.layoutManager = LinearLayoutManager(this)

        loadCart()

        btnClear.setOnClickListener {
            CartManager(this).clear()
            loadCart()
        }
        btnCheckout.setOnClickListener {
            performCheckout()
        }
    }

    private fun loadCart() {
        lifecycleScope.launch {
            val cm = CartManager(this@CartActivity)
            val cart = cm.getCart()
            val products = try {
                val res = RetrofitClient.getInstance(this@CartActivity).getProducts()
                if (res.isSuccessful && res.body() != null) res.body()!! else LocalStore(this@CartActivity).getProducts()
            } catch (e: Exception) {
                LocalStore(this@CartActivity).getProducts()
            }
            val mapped = cart.mapNotNull { ci ->
                val p = products.find { it.id == ci.productId }
                if (p != null) CartViewItem(p, ci.quantity) else null
            }.toMutableList()

            adapter = CartAdapter(mapped) { items ->
                val newMap = items.map { com.miapp.lotuz_2.utils.CartItem(it.product.id, it.quantity) }
                cm.saveCart(newMap)
                updateTotal(items)
            }
            rv.adapter = adapter
            updateTotal(mapped)
        }
    }

    private fun updateTotal(items: List<CartViewItem>) {
        val total = items.sumOf { it.product.price * it.quantity }
        tvTotal.text = "Total: $${total}"
    }

    private fun performCheckout() {
        val cm = CartManager(this)
        val cart = cm.getCart()
        if (cart.isEmpty()) {
            Toast.makeText(this, "Carrito vacío", Toast.LENGTH_SHORT).show()
            return
        }
        lifecycleScope.launch {
            val products = try {
                val res = RetrofitClient.getInstance(this@CartActivity).getProducts()
                if (res.isSuccessful && res.body() != null) res.body()!! else LocalStore(this@CartActivity).getProducts()
            } catch (e: Exception) {
                LocalStore(this@CartActivity).getProducts()
            }
            val items = cart.mapNotNull { ci ->
                val p: Product? = products.find { it.id == ci.productId }
                if (p != null) OrderItem(p.id, ci.quantity, p.price) else null
            }
            val total = items.sumOf { it.quantity * it.unitPrice }
            val order = Order(
                id = (System.currentTimeMillis() / 1000).toInt(),
                items = items,
                total = total,
                status = "pending",
                createdAt = System.currentTimeMillis()
            )
            LocalStore(this@CartActivity).addOrder(order)
            cm.clear()
            Toast.makeText(this@CartActivity, com.miapp.lotuz_2.utils.MessageUtils.checkoutSuccessMessage(), Toast.LENGTH_LONG).show()
            finish()
        }
    }
}
