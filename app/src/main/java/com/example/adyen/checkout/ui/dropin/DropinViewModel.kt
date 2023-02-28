package com.example.adyen.checkout.ui.dropin

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.payments.Amount
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.dropin.DropInConfiguration
import com.android.volley.Response
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.ComponentType
import com.example.adyen.checkout.service.DropinService
import com.example.adyen.checkout.ui.result.ResultActivity
import java.util.*


class DropinViewModel(private val checkoutApiService: CheckoutApiService) : ViewModel() {

    private var shopperLocale = Locale.ENGLISH
    val paymentMethodsResponseData: MutableLiveData<PaymentMethodsApiResponse> = MutableLiveData()
    val dropinConfigData: MutableLiveData<DropInConfiguration> = MutableLiveData()
    val errorMsgData: MutableLiveData<String> = MutableLiveData()

    fun fetchPaymentMethods() {
        checkoutApiService.getPaymentMethods(
            Response.Listener {
                paymentMethodsResponseData.value = PaymentMethodsApiResponse.SERIALIZER.deserialize(it)
            }, Response.ErrorListener {
                errorMsgData.value = "Error getting payment methods! $it"
            })
    }

    fun fetchDropinConfig(ctx: Context) {
        checkoutApiService.getConfig({
            val cardConfiguration =
                CardConfiguration.Builder(ctx, it.getString("clientPublicKey"))
                    .setHolderNameRequired(true)
                    .setShopperLocale(shopperLocale)
                    .build()
            val amount = Amount()
            // Optional. In this example, the Pay button will display 10 EUR.
            amount.currency = "EUR"
            amount.value = 1000

            // Activity launch on result
            val intent = Intent(ctx, ResultActivity::class.java).apply {
                putExtra(ResultActivity.TYPE_KEY, ComponentType.DROPIN.id)
            }

            dropinConfigData.value =
//                DropInConfiguration.Builder(ctx, intent, DropinService::class.java)
                    DropInConfiguration.Builder(ctx, DropinService::class.java, it.getString("clientPublicKey"))
                    // Optional. Use if you want to display the amount and currency on the Pay button.
                    .setAmount(amount)
                    // When you're ready to accept live payments, change the value to one of our live environments.
                    .setEnvironment(Environment.TEST)
                    // Optional. Use to set the language rendered in Drop-in, overriding the default device language setting. See list of Supported languages at https://github.com/Adyen/adyen-android/tree/master/card-ui-core/src/main/res
                    // Make sure that you have set the locale in the payment method configuration object as well.
                    .setShopperLocale(shopperLocale)
                    .addCardConfiguration(cardConfiguration)
                    .build()
        }, {
            errorMsgData.value = "Error getting config! $it"
        })
    }
}

@Suppress("UNCHECKED_CAST")
class DropinViewModelFactory(private val checkoutApiService: CheckoutApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DropinViewModel(checkoutApiService) as T
    }
}