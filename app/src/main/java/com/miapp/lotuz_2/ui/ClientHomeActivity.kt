package com.miapp.lotuz_2.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.miapp.lotuz_2.databinding.ActivityClientHomeBinding
import com.miapp.lotuz_2.network.RetrofitClient
import com.miapp.lotuz_2.utils.SessionManager
import kotlinx.coroutines.launch

class ClientHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityClientHomeBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityClientHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        loadProducts()
        setupLogout()
    }

    private fun setupRecyclerView() {
        // Grid de 2 columnas para mostrar los productos
        binding.rvProducts.layoutManager = GridLayoutManager(this, 2)
    }

    private fun loadProducts() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(this@ClientHomeActivity).getProducts()
                if (response.isSuccessful && response.body() != null) {
                    val productList = response.body()!!

                    adapter = ProductAdapter(productList, { product ->
                        com.miapp.lotuz_2.utils.CartManager(this@ClientHomeActivity).add(product.id)
                        Toast.makeText(this@ClientHomeActivity, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                    })
                    binding.rvProducts.adapter = adapter
                } else {
                    val localList = loadLocalProducts()
                    if (localList.isNotEmpty()) {
                        adapter = ProductAdapter(localList, { product ->
                            com.miapp.lotuz_2.utils.CartManager(this@ClientHomeActivity).add(product.id)
                            Toast.makeText(this@ClientHomeActivity, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                        })
                        binding.rvProducts.adapter = adapter
                        Toast.makeText(this@ClientHomeActivity, "Modo local", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@ClientHomeActivity, "Error cargando productos", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                val localList = loadLocalProducts()
                if (localList.isNotEmpty()) {
                    adapter = ProductAdapter(localList, { product ->
                        com.miapp.lotuz_2.utils.CartManager(this@ClientHomeActivity).add(product.id)
                        Toast.makeText(this@ClientHomeActivity, "${product.name} agregado al carrito", Toast.LENGTH_SHORT).show()
                    })
                    binding.rvProducts.adapter = adapter
                    Toast.makeText(this@ClientHomeActivity, "Modo local", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@ClientHomeActivity, "Error de conexión", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun loadLocalProducts(): List<com.miapp.lotuz_2.model.Product> {
        val prefs = getSharedPreferences("LotuzLocal", MODE_PRIVATE)
        val json = prefs.getString("local_products", null)
        return if (json.isNullOrBlank()) emptyList() else Gson().fromJson(json, Array<com.miapp.lotuz_2.model.Product>::class.java).toList()
    }

    private fun setupLogout() {
        binding.btnLogout.setOnClickListener {
            SessionManager(this).logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }
}
