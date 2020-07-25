package com.swapnil.foodcall.activity.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.util.DBMenuAsyncTask
import com.swapnil.foodcall.activity.util.RetriveItems
import com.swapnil.foodcall.activity.adaptor.CartRecyclerAdaptor
import com.swapnil.foodcall.activity.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var itemRecyclerView: RecyclerView
    lateinit var layoutManager: LinearLayoutManager
    lateinit var recyclerAdaptor: CartRecyclerAdaptor

    lateinit var sharedPreferences: SharedPreferences

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var btnCheckout: Button
    lateinit var imgBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        btnCheckout = findViewById(R.id.btnCheckout)
        itemRecyclerView = findViewById(R.id.itemRecycleView)
        layoutManager = LinearLayoutManager(this)
        imgBack = findViewById(R.id.imgCartBack)

        val itemList = RetriveItems(this).execute().get()

        var totalCost = 0
        for( i in itemList) {
            totalCost += i.costForOne.toInt()
        }

        btnCheckout.text = "Place Order (Total: ₹" + totalCost + ")"

        recyclerAdaptor = CartRecyclerAdaptor(this, itemList)
        itemRecyclerView.adapter = recyclerAdaptor
        itemRecyclerView.layoutManager = layoutManager

        imgBack.setOnClickListener {
            finish()
        }

        btnCheckout.setOnClickListener {

            progressLayout = findViewById(R.id.progressLayout)
            progressBar = findViewById(R.id.progressBar)
            progressLayout.visibility = View.VISIBLE

            val itemList = RetriveItems(this).execute().get()

            var foodJsonArray = JSONArray()
            var totalCost = 0
            for( i in itemList) {
                totalCost += i.costForOne.toInt()
                val jsonObj = JSONObject()
                jsonObj.put("food_item_id", i.id)
                foodJsonArray.put(jsonObj)
            }

            val jsonParams = JSONObject()
            jsonParams.put("user_id", sharedPreferences.getString("user_id", null))
            jsonParams.put("restaurant_id", itemList[0].restaurant_id)
            jsonParams.put("total_cost", totalCost)
            jsonParams.put("food", foodJsonArray)

            val queue = Volley.newRequestQueue(this)

            val url = "http://13.235.250.119/v2/place_order/fetch_result/"

            if (ConnectionManager().checkConnectivity(this)) {
                val jsonObjectRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                        try {
                            progressLayout.visibility = View.GONE
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                Toast.makeText(
                                    this,
                                    "Order has been placed!",
                                    Toast.LENGTH_SHORT
                                ).show()

                                DBMenuAsyncTask(this, null, 4).execute().get()

                                startActivity(Intent(this, OrderPlacedActivity::class.java))
                                finish()

                            } else {
                                Toast.makeText(this, data.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: JSONException) {
                            Toast.makeText(this, "Some unexpected error occured!!!", Toast.LENGTH_SHORT)
                                .show()
                        }

                    }, Response.ErrorListener {
                        if( this!= null )
                            Toast.makeText(this, "Volley error occured!!!", Toast.LENGTH_SHORT).show()
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
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection NOT Found!")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    this.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        }

    }

}
