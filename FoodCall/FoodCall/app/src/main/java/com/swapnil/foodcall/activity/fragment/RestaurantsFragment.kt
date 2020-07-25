package com.swapnil.foodcall.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.util.ConnectionManager
import com.swapnil.foodcall.activity.adaptor.RestaurantsRecyclerAdaptor
import com.swapnil.foodcall.activity.model.Restaurants
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class RestaurantsFragment : Fragment() {

    lateinit var recyclerRestaurant: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdaptor: RestaurantsRecyclerAdaptor

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var RestaurantList: ArrayList<Restaurants> = arrayListOf<Restaurants>()

    var ratingComparator = Comparator<Restaurants> { res1, res2 ->

        if( res1.rating.compareTo(res2.rating, true) == 0 ) {
            res1.name.compareTo(res2.name, true)
        } else {
            res1.rating.compareTo(res2.rating, true)
        }
    }

    var costComparator = Comparator<Restaurants> { res1, res2 ->

        if( res1.cost_for_one.compareTo(res2.cost_for_one, true) == 0 ) {
            res1.name.compareTo(res2.name, true)
        } else {
            res1.cost_for_one.compareTo(res2.cost_for_one, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_restaurants, container, false)

        setHasOptionsMenu(true)

        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurants)
        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        if( ConnectionManager().checkConnectivity(activity as Context) ) {
            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE

                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if(success) {
                        val data = data.getJSONArray("data")
                        for( i in 0 until data.length()) {
                            val restaurantJsonObject = data.getJSONObject(i)
                            val restaurantObject = Restaurants(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )
                            RestaurantList.add(restaurantObject)

                            recyclerAdaptor = RestaurantsRecyclerAdaptor(activity as Context, RestaurantList)
                            recyclerRestaurant.adapter = recyclerAdaptor
                            recyclerRestaurant.layoutManager = layoutManager
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_sort_by, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId

        when(id) {
            R.id.rating -> {
                Toast.makeText(context as Context, "Sort by Rating", Toast.LENGTH_SHORT).show()
                Collections.sort(RestaurantList, ratingComparator)
                RestaurantList.reverse()
            }
            R.id.cost_high_to_low -> {
                Toast.makeText(context as Context, "Sort by Price (High to Low)", Toast.LENGTH_SHORT).show()
                Collections.sort(RestaurantList, costComparator)
                RestaurantList.reverse()
            }
            R.id.cost_low_to_high -> {
                Toast.makeText(context as Context, "Sort by Price (Low to High)", Toast.LENGTH_SHORT).show()
                Collections.sort(RestaurantList, costComparator)
            }
        }

        recyclerAdaptor.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}
