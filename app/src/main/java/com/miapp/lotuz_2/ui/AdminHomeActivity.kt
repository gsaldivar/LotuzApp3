package com.miapp.lotuz_2.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.miapp.lotuz_2.databinding.ActivityAdminHomeBinding
import com.miapp.lotuz_2.utils.SessionManager

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AdminAddProductActivity::class.java))
        }

        binding.btnOrders.setOnClickListener {
            startActivity(Intent(this, AdminOrdersActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            SessionManager(this).logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
    }
}
