package com.miapp.lotuz_2.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.miapp.lotuz_2.databinding.ItemProductBinding
import com.miapp.lotuz_2.model.Product
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private val products: List<Product>,
    private val onAddClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.binding.tvProductName.text = product.name

        val format = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
        holder.binding.tvProductPrice.text = format.format(product.price)

        val url = product.images?.firstOrNull()?.url ?: product.image?.url
        if (url != null) {
            Glide.with(holder.itemView.context)
                .load(url)
                .into(holder.binding.ivProductImage)
        }

        val out = product.stock <= 0
        holder.binding.tvOutOfStock.visibility = if (out) android.view.View.VISIBLE else android.view.View.GONE
        holder.binding.btnAddToCart.isEnabled = !out
        holder.binding.btnAddToCart.text = if (out) "Agotado" else "Agregar"
        holder.binding.btnAddToCart.setOnClickListener { if (!out) onAddClick(product) }
    }

    override fun getItemCount(): Int = products.size
}
