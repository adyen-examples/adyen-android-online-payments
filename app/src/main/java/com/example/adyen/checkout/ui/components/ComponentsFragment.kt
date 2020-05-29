package com.example.adyen.checkout.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.adyen.checkout.R
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.ComponentType
import com.example.adyen.checkout.service.ComponentType.CARD
import com.example.adyen.checkout.service.ComponentType.IDEAL
import com.example.adyen.checkout.service.Utils

class ComponentsFragment(private val type: ComponentType) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_components, container, false)

        val viewModel =
            ViewModelProviders.of(this, ComponentViewModelFactory(CheckoutApiService.getInstance(root.context)))
                .get(ComponentViewModel::class.java)

        viewModel.errorMsgData.observe(this, Observer {
            Utils.showError(root, it)
        })

        viewModel.paymentMethodsData.observe(this, Observer { pm ->
            if (pm != null) {
                val btnCheckout: Button = root.findViewById(R.id.btn_comp_checkout)
                btnCheckout.isEnabled = true
                btnCheckout.setOnClickListener {
                    when (this.type) {
                        IDEAL -> {
                            IdealActivity.start(root.context, pm)
                        }
                        CARD -> TODO()
                    }
                }
            } else {
                Utils.showError(root, "Payment method not found!")
            }
        })

        viewModel.fetchPaymentMethod(this.type)

        return root
    }
}
