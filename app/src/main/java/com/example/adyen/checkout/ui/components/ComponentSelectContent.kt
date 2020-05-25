package com.example.adyen.checkout.ui.components

import com.example.adyen.checkout.service.ComponentType
import java.util.*

object ComponentSelectContent {
    val ITEMS: MutableList<ComponentSelectItem> = ArrayList()

    init {
        ITEMS.add(ComponentSelectItem(ComponentType.IDEAL, "iDeal"))
        ITEMS.add(ComponentSelectItem(ComponentType.CARD, "Credit Card"))
    }

    /**
     * A component item.
     */
    data class ComponentSelectItem(val id: ComponentType, val content: String) {
        override fun toString(): String = content
    }
}
