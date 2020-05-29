package com.example.adyen.checkout.ui.components

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adyen.checkout.base.component.Configuration
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.adyen.checkout.base.model.payments.response.Action
import com.android.volley.Response
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.ComponentType
import org.json.JSONObject
import java.util.*


class ComponentViewModel(private val checkoutApiService: CheckoutApiService) : ViewModel() {

    private var shopperLocale = Locale.ENGLISH
    val paymentMethodsData: MutableLiveData<PaymentMethod> = MutableLiveData()
    val errorMsgData: MutableLiveData<String> = MutableLiveData()
    val paymentResData: MutableLiveData<String> = MutableLiveData()
    val actionData: MutableLiveData<Action> = MutableLiveData()

    fun fetchPaymentMethod(type: ComponentType) {
        checkoutApiService.getPaymentMethods(
            {
                paymentMethodsData.value = checkoutApiService.filterPaymentMethodByType(it.paymentMethods, type)!!
            }, Response.ErrorListener {
                errorMsgData.value = "Error getting payment methods! $it"
            })
    }

//    fun fetchCardPaymentConfig() {
//        // first get config securely from backend
//        checkoutApiService.getConfig(Response.Listener { c ->
//            compConfigurationData.value = Pair(
//                CardConfiguration.Builder(shopperLocale, Environment.TEST, c.getString("clientPublicKey")).build(),
//                checkoutApiService.filterPaymentMethodByType(it.paymentMethods, ComponentType.CARD)!!
//            )
//        }, Response.ErrorListener { err ->
//            errorMsgData.value = "Error getting config! $err"
//        })
//    }

    fun initPayment(paymentComponentData: JSONObject) {
        checkoutApiService.initPayment(paymentComponentData, Response.Listener {
            handleResponse(it)
        }, Response.ErrorListener {
            errorMsgData.value = "Error making payment! $it"
        })
    }

    fun submitDetails(actionComponentData: JSONObject) {
        checkoutApiService.submitAdditionalDetails(actionComponentData, Response.Listener {
            handleResponse(it)
        }, Response.ErrorListener {
            errorMsgData.value = "Error making payment! $it"
        })
    }

    private fun handleResponse(it: JSONObject) {
        if (it.has("action")) {
            actionData.value = Action.SERIALIZER.deserialize(it.getJSONObject("action"))
        } else {
            paymentResData.value = it.optString("resultCode", "NONE")
        }
    }

}

@Suppress("UNCHECKED_CAST")
class ComponentViewModelFactory(private val checkoutApiService: CheckoutApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ComponentViewModel(checkoutApiService) as T
    }
}