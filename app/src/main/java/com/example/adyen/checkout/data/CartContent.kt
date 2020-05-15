package com.example.adyen.checkout.data

import java.util.ArrayList
import java.util.HashMap

/**
 * Helper class for providing cart items
 */
object CartContent {

    val ITEMS: MutableList<CartItem> = ArrayList()

    /**
     * A map of sample (cart) items, by ID.
     */
    private val ITEM_MAP: MutableMap<String, CartItem> = HashMap()

    init {
        addItem(CartItem("Sunglasses", 5.0, "sunglasses"))
        addItem(CartItem("Headphones", 5.0, "headphones"))
    }

    private fun addItem(item: CartItem) {
        ITEMS.add(item)
        ITEM_MAP[item.name] = item
    }

    /**
     * A cart item representing a product.
     */
    data class CartItem(val name: String, val price: Number, val image: String) {
        override fun toString(): String = name
    }
}
