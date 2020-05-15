package com.example.adyen.checkout.ui.dropin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.adyen.checkout.base.model.payments.Amount
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.dropin.DropIn
import com.adyen.checkout.dropin.DropInConfiguration
import com.adyen.checkout.dropin.service.CallResult
import com.adyen.checkout.dropin.service.DropInService
import com.android.volley.Response
import com.example.adyen.checkout.R
import com.example.adyen.checkout.ui.result.ResultActivity
import com.example.adyen.checkout.service.ApiServicesUtil
import com.example.adyen.checkout.service.Utils
import org.json.JSONObject
import java.util.*


class DropinFragment : Fragment() {

    private var shopperLocale = Locale.ENGLISH
    private lateinit var dropInConfiguration: DropInConfiguration
    private lateinit var apiServicesUtil: ApiServicesUtil

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dropin, container, false)
        apiServicesUtil = ApiServicesUtil.getInstance(root.context)

        apiServicesUtil.getConfig(Response.Listener {
            val cardConfiguration =
                CardConfiguration.Builder(root.context, it.getString("clientPublicKey"))
                    .setShopperLocale(shopperLocale)
                    .build()
            val amount = Amount()
            // Optional. In this example, the Pay button will display 10 EUR.
            amount.currency = "EUR"
            amount.value = 1000

            val intent = Intent(root.context, ResultActivity::class.java)

            dropInConfiguration =
                DropInConfiguration.Builder(root.context, intent, DropinService::class.java)
                    // Optional. Use if you want to display the amount and currency on the Pay button.
                    .setAmount(amount)
                    // When you're ready to accept live payments, change the value to one of our live environments.
                    .setEnvironment(Environment.TEST)
                    // Optional. Use to set the language rendered in Drop-in, overriding the default device language setting. See list of Supported languages at https://github.com/Adyen/adyen-android/tree/master/card-ui-core/src/main/res
                    // Make sure that you have set the locale in the payment method configuration object as well.
                    .setShopperLocale(shopperLocale)
                    .addCardConfiguration(cardConfiguration)
                    .build()
        }, Response.ErrorListener {
            Utils.showError(root, "Error getting config! $it")
        })


        val btnCheckout: Button = root.findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener { view ->
            apiServicesUtil.getPaymentMethods(
                {
                    DropIn.startPayment(
                        root.context,
                        it,
                        dropInConfiguration
                    )
                }, Response.ErrorListener {
                    Utils.showError(view, "Error getting payment methods! $it")
                })
        }

        return root
    }

    class DropinService : DropInService() {
        override fun makePaymentsCall(paymentComponentData: JSONObject): CallResult {
            return ApiServicesUtil.getInstance(this.applicationContext)
                .initPayment(paymentComponentData)
        }

        override fun makeDetailsCall(actionComponentData: JSONObject): CallResult {
            return ApiServicesUtil.getInstance(this.applicationContext)
                .submitAdditionalDetails(actionComponentData)
        }
    }
}
