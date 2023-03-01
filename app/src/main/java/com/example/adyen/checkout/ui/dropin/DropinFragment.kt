package com.example.adyen.checkout.ui.dropin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.example.adyen.checkout.R
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.Utils

class DropinFragment : Fragment() {

    private var dropInConfiguration: DropInConfiguration? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dropin, container, false)
        val viewModel =
            ViewModelProviders.of(this, DropinViewModelFactory(CheckoutApiService.getInstance()))[DropinViewModel::class.java]

        viewModel.errorMsgData.observe(viewLifecycleOwner) {
            Utils.showError(root, it)
        }

        viewModel.dropinConfigData.observe(viewLifecycleOwner) {
            dropInConfiguration = it
        }

        viewModel.paymentMethodsResponseData.observe(viewLifecycleOwner) { pmr: PaymentMethodsApiResponse ->
            val btnCheckout: Button = root.findViewById(R.id.btn_checkout)
            btnCheckout.isEnabled = true
            btnCheckout.setOnClickListener {
                if (dropInConfiguration != null) {

                    DropIn.startPayment(this@DropinFragment,
                        pmr,
                        dropInConfiguration!!)
                } else {
                    Utils.showError(root, "Payment Methods not found!")
                }
            }
        }

        viewModel.fetchPaymentMethods()
        viewModel.fetchDropinConfig(root.context)

        return root
    }
}
