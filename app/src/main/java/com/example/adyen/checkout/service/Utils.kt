package com.example.adyen.checkout.service

import android.view.View
import com.google.android.material.snackbar.Snackbar

object Utils {
    fun showInfo(view: View, text: String) {
        Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_LONG
        ).setAction("Action", null).show()
    }

    fun showError(view: View, text: String) {
        Snackbar.make(
            view,
            text,
            Snackbar.LENGTH_LONG
        ).setAction("Action", null).show()
        println(text)
    }
}