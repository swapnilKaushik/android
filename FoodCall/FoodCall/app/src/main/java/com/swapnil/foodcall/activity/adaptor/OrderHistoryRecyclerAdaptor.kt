package com.swapnil.foodcall.activity.adaptor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.model.OrderHistory

class OrderHistoryRecyclerAdaptor(val context: Context, val orderList: ArrayList<OrderHistory>)
    :RecyclerView.Adapter<OrderHistoryRecyclerAdaptor.OrderHistoryViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_single_row, parent, false)

        return OrderHistoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        val order = orderList[position]
        holder.txtOrderTitle.text = order.restaurant_name
        val date = order.order_placed_at
        holder.txtOrderDate.text = date[0] + date[1].toString() + "/" + date[3] + date[4]+ "/20" + date[6] + date[7]

        val adaptor = CartRecyclerAdaptor(context, order.food_items)
        holder.recyclerView.adapter = adaptor
        var layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        holder.recyclerView.layoutManager = layoutManager
    }

    class OrderHistoryViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtOrderTitle = view.findViewById<TextView>(R.id.txtOrderTitle)
        val txtOrderDate = view.findViewById<TextView>(R.id.txtOrderDate)
        val recyclerView = view.findViewById<RecyclerView>(R.id.viewItems)
    }
}