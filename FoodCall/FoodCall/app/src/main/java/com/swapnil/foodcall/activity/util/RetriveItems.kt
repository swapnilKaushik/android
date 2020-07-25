package com.swapnil.foodcall.activity.util

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.swapnil.foodcall.activity.database.MenuItem.MenuItemDatabase
import com.swapnil.foodcall.activity.database.MenuItem.MenuItemEntity

class RetriveItems(val context: Context): AsyncTask<Void, Void, List<MenuItemEntity>>() {
    override fun doInBackground(vararg params: Void?): List<MenuItemEntity> {
        val db = Room.databaseBuilder(context, MenuItemDatabase::class.java, "menuItem_db").build()

        return db.menuItemDao().getAllMenuItems()
    }

}