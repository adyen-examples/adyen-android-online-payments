package com.example.adyen.checkout.ui.components

import java.util.*

object ComponentSelectContent {

    val ITEMS: MutableList<ComponentSelectItem> = ArrayList()

    init {
        ITEMS.add(ComponentSelectItem("ideal", "iDeal"))
        ITEMS.add(ComponentSelectItem("scheme", "Credit Card"))
    }

    /**
     * A component item.
     */
    data class ComponentSelectItem(val id: String, val content: String) {
        override fun toString(): String = content
    }
}
