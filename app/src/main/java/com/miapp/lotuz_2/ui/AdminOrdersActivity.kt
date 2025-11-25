package com.miapp.lotuz_2.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.miapp.lotuz_2.R
import com.miapp.lotuz_2.model.Order
import com.miapp.lotuz_2.utils.LocalStore

class AdminOrdersActivity : AppCompatActivity() {
    private lateinit var rv: androidx.recyclerview.widget.RecyclerView
    private lateinit var adapter: AdminOrdersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_orders)

        rv = findViewById(R.id.rvOrders)
        findViewById<android.widget.Button>(R.id.btnBack).setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        rv.layoutManager = LinearLayoutManager(this)
        loadOrders()
    }

    private fun loadOrders() {
        val ls = LocalStore(this)
        val orders = ls.getOrders().toMutableList()
        adapter = AdminOrdersAdapter(orders) { order, action ->
            val updated = orders.map {
                if (it.id == order.id) it.copy(status = action) else it
            }
            ls.saveOrders(updated)
            Toast.makeText(this, "Orden ${order.id} → ${action}", Toast.LENGTH_SHORT).show()
            adapter = AdminOrdersAdapter(updated.toMutableList(), this::onAction)
            rv.adapter = adapter
        }
        rv.adapter = adapter
    }

    private fun onAction(order: Order, action: String) {
        val ls = LocalStore(this)
        val current = ls.getOrders().toMutableList()
        val updated = current.map { if (it.id == order.id) it.copy(status = action) else it }
        ls.saveOrders(updated)
        adapter = AdminOrdersAdapter(updated.toMutableList(), this::onAction)
        rv.adapter = adapter
        Toast.makeText(this, "Orden ${order.id} → ${action}", Toast.LENGTH_SHORT).show()
    }
}
