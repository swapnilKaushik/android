package com.swapnil.foodcall.activity.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.adaptor.FavRestaurantsRecyclerAdaptor
import com.swapnil.foodcall.activity.database.Restaurant.RestaurantDatabase
import com.swapnil.foodcall.activity.database.Restaurant.RestaurantEntity

class FavoriteFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdaptor: FavRestaurantsRecyclerAdaptor
    lateinit var favLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)

        recyclerView = view.findViewById(R.id.recyclerFavRestaurants)
        layoutManager = LinearLayoutManager(activity)

        favLayout = view.findViewById(R.id.favLayout)

        val favRestaurantList = RetrieveFavourites(activity as Context).execute().get()

        if( favRestaurantList.size == 0 )
            favLayout.visibility = View.VISIBLE
        else
            favLayout.visibility = View.GONE

        if( activity != null ) {
            recyclerAdaptor = FavRestaurantsRecyclerAdaptor(activity as Context, favRestaurantList)
            recyclerView.adapter = recyclerAdaptor
            recyclerView.layoutManager = layoutManager
        }

        return view
    }

    class RetrieveFavourites( val context: Context): AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "favRestaurant_db").build()

            return db.restaurantDao().getAllRestaurants()
        }
    }

}
