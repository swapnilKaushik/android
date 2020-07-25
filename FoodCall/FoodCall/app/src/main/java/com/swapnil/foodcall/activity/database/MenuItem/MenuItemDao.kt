package com.swapnil.foodcall.activity.database.MenuItem

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MenuItemDao {

    @Insert
    fun insertMenuItem( menuItemEntity: MenuItemEntity? )

    @Delete
    fun deleteMenuItem( menuItemEntity: MenuItemEntity? )

    @Query("SELECT * FROM MenuItems")
    fun getAllMenuItems(): List<MenuItemEntity>

    @Query("DELETE FROM MenuItems")
    fun deleteAllMenuItems()

    @Query( "SELECT * FROM MenuItems WHERE id = :item_id")
    fun getMenuItemById(item_id: String): MenuItemEntity
}