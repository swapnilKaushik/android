package com.swapnil.foodcall.activity.activity

import android.app.AlertDialog
import android.content.Intent
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
import com.swapnil.foodcall.activity.adaptor.MenuItemRecyclerAdaptor
import com.swapnil.foodcall.activity.model.MenuItems
import com.swapnil.foodcall.activity.util.ConnectionManager
import org.json.JSONException

class MenuItemActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    lateinit var title: TextView
    lateinit var btnCart: Button
    lateinit var imgBack: ImageView

    val menuList: ArrayList<MenuItems> = arrayListOf<MenuItems>()

    lateinit var recyclerAdaptor: MenuItemRecyclerAdaptor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_item)

        recyclerView = findViewById(R.id.menuRecycleView)
        imgBack = findViewById(R.id.imgMenuItemBack)
        layoutManager = LinearLayoutManager(this)

        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        DBMenuAsyncTask(this, null, 4).execute().get()

        val restaurant_id = intent.getIntExtra("id", 0)
        val name = intent.getStringExtra("name")

        title = findViewById(R.id.txtActTitle)
        title.text = name

        btnCart = findViewById(R.id.btnCart)

        imgBack.setOnClickListener {
            onBackPressed()
        }

        btnCart.setOnClickListener {

            val itemCount = RetriveItems(this).execute().get().size

            if(itemCount > 0) {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this@MenuItemActivity, "First add Item to cart!", Toast.LENGTH_SHORT).show()
            }
        }

        val queue = Volley.newRequestQueue(this)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/" + restaurant_id

        if( ConnectionManager().checkConnectivity(this) ) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                try {
                    progressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success) {
                        val data = data.getJSONArray("data")
                        for( i in 0 until data.length()) {
                            val menuItemJsonObject = data.getJSONObject(i)
                            val menuObject = MenuItems(
                                menuItemJsonObject.getString("id"),
                                menuItemJsonObject.getString("name"),
                                menuItemJsonObject.getString("cost_for_one"),
                                menuItemJsonObject.getString("restaurant_id")
                            )
                            menuList.add(menuObject)

                            recyclerAdaptor = MenuItemRecyclerAdaptor(this, menuList)
                            recyclerView.adapter = recyclerAdaptor
                            recyclerView.layoutManager = layoutManager
                        }

                    } else {
                        Toast.makeText(this@MenuItemActivity, "Some error occured!!!", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(this@MenuItemActivity, "Some unexpected error occured!!!", Toast.LENGTH_SHORT)
                        .show()
                }

            }, Response.ErrorListener {
                Toast.makeText(this@MenuItemActivity, "Volley error occured!!!", Toast.LENGTH_SHORT).show()
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

    override fun onBackPressed() {
        if( RetriveItems(this).execute().get().size > 0 ) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Confirmation")
            dialog.setMessage("All items in the cart will be lost, are you sure you want to go back?")
            dialog.setPositiveButton("Yes") { text, listener ->
                super.onBackPressed()
            }
            dialog.setNegativeButton("No") { text, listener ->
            }
            dialog.create()
            dialog.show()
        } else {
            super.onBackPressed()
        }

    }

}
