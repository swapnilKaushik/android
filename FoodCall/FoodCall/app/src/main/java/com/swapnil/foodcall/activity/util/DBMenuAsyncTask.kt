package com.swapnil.foodcall.activity.util

import android.content.Context
import android.os.AsyncTask
import androidx.room.Room
import com.swapnil.foodcall.activity.database.MenuItem.MenuItemDatabase
import com.swapnil.foodcall.activity.database.MenuItem.MenuItemEntity

class DBMenuAsyncTask(val context: Context, val menuItemEntity: MenuItemEntity?, val mode: Int): AsyncTask<Void, Void, Boolean>() {

    val db = Room.databaseBuilder(context, MenuItemDatabase::class.java, "menuItem_db").build()

    override fun doInBackground(vararg params: Void?): Boolean {

        when(mode) {
            1 -> {
                val menuItemEntity: MenuItemEntity? = db.menuItemDao().getMenuItemById(menuItemEntity?.id.toString())
                db.close()
                return menuItemEntity != null
            }

            2 -> {
                db.menuItemDao().insertMenuItem(menuItemEntity)
                db.close()
                return true
            }

            3 -> {
                db.menuItemDao().deleteMenuItem(menuItemEntity)
                db.close()
                return true
            }

            4 -> {
                db.menuItemDao().deleteAllMenuItems()
                db.close()
                return true
            }
        }

        return false
    }

}