package com.example.adyen.checkout.ui.cart

import java.util.*

/**
 * Helper class for providing cart items
 */
object CartContent {

    val ITEMS: MutableList<CartItem> = ArrayList()

    init {
        ITEMS.add(CartItem("Sunglasses", 5.0, "sunglasses"))
        ITEMS.add(CartItem("Headphones", 5.0, "headphones"))
    }

    /**
     * A cart item representing a product.
     */
    data class CartItem(val name: String, val price: Number, val image: String) {
        override fun toString(): String = name
    }
}
