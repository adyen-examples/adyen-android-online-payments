package com.example.adyen.checkout.ui.components

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adyen.checkout.components.model.PaymentMethodsApiResponse
import com.adyen.checkout.components.model.paymentmethods.PaymentMethod
import com.adyen.checkout.dropin.service.DropInServiceResult
import com.android.volley.Response
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.ComponentType
import org.json.JSONObject


class ComponentViewModel(private val checkoutApiService: CheckoutApiService) : ViewModel() {

    val paymentMethodsData: MutableLiveData<PaymentMethod> = MutableLiveData()
    val configData: MutableLiveData<JSONObject> = MutableLiveData()
    val errorMsgData: MutableLiveData<String> = MutableLiveData()
    val paymentResData: MutableLiveData<String> = MutableLiveData()
    val actionData: MutableLiveData<DropInServiceResult.Action> = MutableLiveData()

    fun fetchPaymentMethod(type: ComponentType) {
        checkoutApiService.getPaymentMethods(
            Response.Listener {
                val res = PaymentMethodsApiResponse.SERIALIZER.deserialize(it)
                paymentMethodsData.value = checkoutApiService.filterPaymentMethodByType(res.paymentMethods, type)!!
            }, Response.ErrorListener {
                errorMsgData.value = "Error getting payment methods! $it"
            })
    }

    fun fetchConfig() {
        checkoutApiService.getConfig(Response.Listener { configData.value = it },
            Response.ErrorListener { err ->
                errorMsgData.value = "Error getting config! $err"
            })
    }

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
            actionData.value = DropInServiceResult.Action(it.getJSONObject("action").toString()) // TODO : Working?
        } else {
            paymentResData.value = it.optString("resultCode", "NONE")
        }
    }

}

@Suppress("UNCHECKED_CAST")
class ComponentViewModelFactory(private val checkoutApiService: CheckoutApiService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ComponentViewModel(checkoutApiService) as T
    }
}