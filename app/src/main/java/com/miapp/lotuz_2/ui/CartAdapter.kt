package com.miapp.lotuz_2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miapp.lotuz_2.R
import com.miapp.lotuz_2.model.Product

data class CartViewItem(val product: Product, var quantity: Int)

class CartAdapter(
    private val items: MutableList<CartViewItem>,
    private val onChange: (List<CartViewItem>) -> Unit
) : RecyclerView.Adapter<CartAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvQty: TextView = itemView.findViewById(R.id.tvQty)
        val btnMinus: Button = itemView.findViewById(R.id.btnMinus)
        val btnPlus: Button = itemView.findViewById(R.id.btnPlus)
        val btnRemove: Button = itemView.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.tvName.text = item.product.name
        holder.tvPrice.text = "$${item.product.price}"
        holder.tvQty.text = item.quantity.toString()

        holder.btnMinus.setOnClickListener {
            val current = items[holder.adapterPosition]
            if (current.quantity > 1) {
                current.quantity -= 1
                holder.tvQty.text = current.quantity.toString()
                onChange(items.toList())
            }
        }
        holder.btnPlus.setOnClickListener {
            val current = items[holder.adapterPosition]
            current.quantity += 1
            holder.tvQty.text = current.quantity.toString()
            onChange(items.toList())
        }
        holder.btnRemove.setOnClickListener {
            items.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            onChange(items.toList())
        }
    }
}
