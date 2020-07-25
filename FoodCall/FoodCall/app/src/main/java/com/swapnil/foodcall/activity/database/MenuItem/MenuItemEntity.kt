package com.swapnil.foodcall.activity.database.MenuItem

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "menuItems")
data class MenuItemEntity (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "cost_for_one") val costForOne: String,
    @ColumnInfo(name = "restaurant_id") val restaurant_id: Int
)