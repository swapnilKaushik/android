package com.swapnil.foodcall.activity.adaptor

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.util.DBMenuAsyncTask
import com.swapnil.foodcall.activity.database.MenuItem.MenuItemEntity
import com.swapnil.foodcall.activity.model.MenuItems

class MenuItemRecyclerAdaptor(val context: Context, val menuList: ArrayList<MenuItems>): RecyclerView.Adapter<MenuItemRecyclerAdaptor.MenuItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_menu_items_single_row, parent, false)


        return MenuItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val item = menuList[position]
        holder.txtSrNo.text = (position+1).toString()
        holder.textView.text = item.name
        holder.txtPrice.text = "₹" + item.cost_for_one

        val menuItemEntity = MenuItemEntity(
            item.id?.toInt() as Int,
            item.name.toString(),
            item.cost_for_one.toString(),
            item.restaurant_id.toInt()
        )

        holder.btnAddToCart.setOnClickListener {

            if( !DBMenuAsyncTask(context, menuItemEntity, 1).execute().get() ) {
                val result = DBMenuAsyncTask(context, menuItemEntity, 2).execute().get()
                if(result) {
                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                    val btnRemoveColor = ContextCompat.getColor(context, R.color.btnRemoveColor)
                    holder.btnAddToCart.setBackgroundColor(btnRemoveColor)
                    holder.btnAddToCart.text = "Remove"
                } else {
                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()
                }
            } else {
                val result = DBMenuAsyncTask(context, menuItemEntity, 3).execute().get()
                if(result){
                    Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                    val btnColor = ContextCompat.getColor(context, R.color.colorPrimary)
                    holder.btnAddToCart.setBackgroundColor(btnColor)
                    holder.btnAddToCart.text = "Add"
                } else {
                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    class MenuItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtSrNo: TextView = view.findViewById(R.id.txtSrNo)
        val textView: TextView = view.findViewById(R.id.txtMenuName)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val btnAddToCart: Button = view.findViewById(R.id.btnAddToCart)
    }
}