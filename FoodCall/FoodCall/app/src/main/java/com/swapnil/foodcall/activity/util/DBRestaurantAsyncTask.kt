package com.swapnil.foodcall.activity.util

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.swapnil.foodcall.activity.database.Restaurant.RestaurantDatabase
import com.swapnil.foodcall.activity.database.Restaurant.RestaurantEntity

class DBRestaurantAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int): AsyncTask<Void, Void, Boolean>() {

    val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "favRestaurant_db").build()

    override fun doInBackground(vararg params: Void?): Boolean {

        when(mode) {
            1 -> {
                val restaurant: RestaurantEntity? = db.restaurantDao().getRestaurantById(restaurantEntity.id.toString())
                db.close()
                return restaurant != null
            }

            2 -> {
                db.restaurantDao().insertRestaurant(restaurantEntity)
                db.close()
                return true
            }

            3 -> {
                db.restaurantDao().deleteRestaurant(restaurantEntity)
                db.close()
                return true
            }
        }

        return false
    }

}