package com.swapnil.foodcall.activity.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.database.MenuItem.MenuItemEntity

class CartRecyclerAdaptor(val context: Context, val itemEntity: List<MenuItemEntity>):
    RecyclerView.Adapter<CartRecyclerAdaptor.CartViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row, parent, false)

        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemEntity.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = itemEntity[position]
        holder.txtFoodName.text = item.name
        holder.txtFoodPrice.text = "₹" + item.costForOne
    }

    class CartViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtFoodName = view.findViewById<TextView>(R.id.txtFoodName)
        val txtFoodPrice = view.findViewById<TextView>(R.id.txtFoodPrice)
    }

}