package com.example.adyen.checkout.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.adyen.checkout.base.model.PaymentMethodsApiResponse
import com.adyen.checkout.base.model.payments.request.IssuerListPaymentMethod
import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.ideal.IdealComponent
import com.adyen.checkout.ideal.IdealConfiguration
import com.adyen.checkout.ideal.IdealSpinnerView
import com.adyen.checkout.issuerlist.IssuerListComponent
import com.adyen.checkout.issuerlist.IssuerListConfiguration
import com.android.volley.Response
import com.example.adyen.checkout.R
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.ComponentType
import com.example.adyen.checkout.service.ComponentType.CARD
import com.example.adyen.checkout.service.ComponentType.IDEAL
import com.example.adyen.checkout.service.Utils
import java.util.*

class ComponentsFragment(private val type: ComponentType) : Fragment() {
    private var shopperLocale = Locale.ENGLISH
    private lateinit var checkoutApiService: CheckoutApiService
    private var compConfiguration: IssuerListConfiguration? = null
    private lateinit var component: IssuerListComponent<IssuerListPaymentMethod>
    private var paymentMethodResp: PaymentMethodsApiResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_components, container, false)
        checkoutApiService = CheckoutApiService.getInstance(root.context)

        checkoutApiService.getPaymentMethods(
            {
                paymentMethodResp = it
                when (this.type) {
                    IDEAL -> {
                        compConfiguration =
                            IdealConfiguration.Builder(root.context)
                                .setEnvironment(Environment.TEST)
                                // When you're ready to accept live payments, change the value to one of our live environments.
                                .build()

                        component = IdealComponent.PROVIDER.get(
                            this,
                            checkoutApiService.filterPaymentMethodByType(paymentMethodResp!!.paymentMethods, IDEAL)!!,
                            compConfiguration as IdealConfiguration
                        ) as IssuerListComponent<IssuerListPaymentMethod>

                        component.observe(this, Observer { itm ->
                            if (itm?.isValid == true) {
                                // When the shopper proceeds to pay, pass the `it.data` to your server to send a /payments request
                                CheckoutApiService.getInstance(root.context)
                                    .initPayment(PaymentComponentData.SERIALIZER.serialize(itm.data))
                            }
                        })
                    }
                    CARD -> {
                        // first get config securely from backend
                        checkoutApiService.getConfig(Response.Listener {
                            TODO()
                        }, Response.ErrorListener {
                            Utils.showError(root, "Error getting config! $it")
                        })
                    }
                }
            }, Response.ErrorListener {
                Utils.showError(root, "Error getting payment methods! $it")
            })


        // handle button click on cart view
        val btnCheckout: Button = root.findViewById(R.id.btn_comp_checkout)
        btnCheckout.setOnClickListener {
            if (paymentMethodResp != null && compConfiguration != null) {
                when (this.type) {
                    IDEAL -> {
                        val idealSpinnerView: IdealSpinnerView = root.findViewById(R.id.ideal)
                        idealSpinnerView.attach(component as IdealComponent, this)
                    }
                    CARD -> TODO()
                }

            } else {
                Utils.showError(root, "Payment Methods not found!")
            }
        }

        return root
    }
}
