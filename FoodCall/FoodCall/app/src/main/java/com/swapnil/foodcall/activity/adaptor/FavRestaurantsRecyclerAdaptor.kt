package com.swapnil.foodcall.activity.adaptor

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.swapnil.foodcall.R
import com.squareup.picasso.Picasso
import com.swapnil.foodcall.activity.util.DBRestaurantAsyncTask
import com.swapnil.foodcall.activity.activity.MenuItemActivity
import com.swapnil.foodcall.activity.database.Restaurant.RestaurantEntity

class FavRestaurantsRecyclerAdaptor(val context: Context, val itemList: List<RestaurantEntity>): RecyclerView.Adapter<FavRestaurantsRecyclerAdaptor.RestaurantsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurants_single_row, parent, false)

        return RestaurantsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: RestaurantsViewHolder, position: Int) {
        val restaurant = itemList[position]
        holder.textView.text = restaurant.name
        holder.txtPrice.text = restaurant.costForOne
        holder.txtRating.text = restaurant.rating
        Picasso.get().load(restaurant.image_url)
            .error(R.mipmap.default_book_cover).into(holder.imgMenuLogo)

        val restaurantEntity = RestaurantEntity(
            restaurant.id?.toInt() as Int,
            restaurant.name.toString(),
            restaurant.rating.toString(),
            restaurant.costForOne.toString(),
            restaurant.image_url
        )

        val isFav = DBRestaurantAsyncTask(context, restaurantEntity, 1).execute().get()

        if(isFav) {
            holder.imgFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            holder.imgFavorite.setImageResource(R.drawable.ic_un_favorite)
        }

        holder.imgFavorite.setOnClickListener {

            if(!DBRestaurantAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBRestaurantAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if(result) {
                    holder.imgFavorite.setImageResource(R.drawable.ic_favorite)
                    Toast.makeText(context, "Added To Favorites ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBRestaurantAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if(result) {
                    holder.imgFavorite.setImageResource(R.drawable.ic_un_favorite)
                    Toast.makeText(context, "Removed from Favorites ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()
                }
            }
        }

        holder.crdView.setOnClickListener {
            val intent = Intent(context, MenuItemActivity::class.java)
            intent.putExtra("id", restaurant.id?.toInt() as Int)
            intent.putExtra("name", restaurant.name.toString())
            context.startActivity(intent)
        }
    }

    class RestaurantsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.txtTitle)
        val txtPrice: TextView = view.findViewById(R.id.txtPrice)
        val txtRating: TextView = view.findViewById(R.id.txtRating)
        val imgMenuLogo: ImageView = view.findViewById(R.id.imgMenuLogo)
        val imgFavorite: ImageView = view.findViewById(R.id.imgFavorite)
        val crdView: CardView = view.findViewById(R.id.crdView)
    }

}