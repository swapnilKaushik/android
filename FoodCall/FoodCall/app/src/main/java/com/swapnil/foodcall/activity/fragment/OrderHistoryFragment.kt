package com.swapnil.foodcall.activity.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.adaptor.OrderHistoryRecyclerAdaptor
import com.swapnil.foodcall.activity.database.MenuItem.MenuItemEntity
import com.swapnil.foodcall.activity.model.OrderHistory
import com.swapnil.foodcall.activity.util.ConnectionManager
import org.json.JSONException


class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrder: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdaptor: OrderHistoryRecyclerAdaptor

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var sharedPreferences: SharedPreferences? = null

    var orderList: ArrayList<OrderHistory> = arrayListOf<OrderHistory>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        sharedPreferences = context?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        val userID = sharedPreferences?.getString("user_id", null)

        recyclerOrder = view.findViewById(R.id.viewOrder)
        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/orders/fetch_result/" + userID
        if( ConnectionManager().checkConnectivity(activity as Context) ) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                try {
                    progressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success) {
                        val data = data.getJSONArray("data")
                        for( i in 0 until data.length()) {
                            val orderJsonObject = data.getJSONObject(i)

                            val food = orderJsonObject.getJSONArray("food_items")
                            var foodList = listOf<MenuItemEntity>()
                            for( i in 0 until food.length() ) {
                                val item = food.getJSONObject(i)
                                val itemEntity = MenuItemEntity(
                                    item.getInt("food_item_id"),
                                    item.getString("name"),
                                    item.getString("cost"),
                                    0
                                )
                                foodList += itemEntity
                            }

                            val orderObject = OrderHistory(
                                orderJsonObject.getString("order_id"),
                                orderJsonObject.getString("restaurant_name"),
                                orderJsonObject.getString("total_cost"),
                                orderJsonObject.getString("order_placed_at"),
                                foodList
                            )

                            orderList.add(orderObject)

                            recyclerAdaptor = OrderHistoryRecyclerAdaptor(activity as Context, orderList)
                            recyclerOrder.adapter = recyclerAdaptor
                            recyclerOrder.layoutManager = layoutManager
                        }

                    } else {
                        Toast.makeText(activity as Context, "Some error occured!!!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(activity as Context, "Some unexpected error occured!!!", Toast.LENGTH_SHORT)
                        .show()
                }

            }, Response.ErrorListener {
                if( activity!= null ) {
                    Toast.makeText(activity as Context, "Volley error occured!!!", Toast.LENGTH_SHORT).show()
                }
            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "3a19289853e0b7"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        } else {
            //Internet NOT available
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection NOT Found!")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }
}
