package com.miapp.lotuz_2.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miapp.lotuz_2.R
import com.miapp.lotuz_2.model.Order

class AdminOrdersAdapter(
    private val items: MutableList<Order>,
    private val onAction: (Order, String) -> Unit
) : RecyclerView.Adapter<AdminOrdersAdapter.VH>() {

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        val tvStatus: TextView = itemView.findViewById(R.id.tvOrderStatus)
        val btnAccept: Button = itemView.findViewById(R.id.btnAccept)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
        val btnShip: Button = itemView.findViewById(R.id.btnShip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return VH(v)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val o = items[position]
        holder.tvId.text = "Orden #${o.id}"
        holder.tvTotal.text = "Total: $${o.total}"
        holder.tvStatus.text = o.status

        holder.btnAccept.visibility = if (o.status == "pending") View.VISIBLE else View.GONE
        holder.btnReject.visibility = if (o.status == "pending") View.VISIBLE else View.GONE
        holder.btnShip.visibility = if (o.status == "accepted") View.VISIBLE else View.GONE

        holder.btnAccept.setOnClickListener { onAction(o, "accepted") }
        holder.btnReject.setOnClickListener { onAction(o, "rejected") }
        holder.btnShip.setOnClickListener { onAction(o, "shipped") }
    }
}

