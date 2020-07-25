package com.swapnil.foodcall.activity.model

import com.swapnil.foodcall.activity.database.MenuItem.MenuItemEntity


data class OrderHistory (
    val order_id: String,
    val restaurant_name: String,
    val total_cost: String,
    val order_placed_at: String,
    val food_items: List<MenuItemEntity>
)