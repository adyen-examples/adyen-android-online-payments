package com.example.adyen.checkout.service

import com.adyen.checkout.dropin.service.DropInService
import com.adyen.checkout.dropin.service.DropInServiceResult
import org.json.JSONObject

class DropinService : DropInService() {
    private var checkoutApiService = CheckoutApiService.getInstance()

    override fun makePaymentsCall(paymentComponentData: JSONObject): DropInServiceResult {
        return handlePaymentRequestResult(checkoutApiService.initPayment(paymentComponentData, ComponentType.DROPIN.id))
    }

    override fun makeDetailsCall(actionComponentData: JSONObject): DropInServiceResult {
        return handlePaymentRequestResult(checkoutApiService.submitAdditionalDetails(actionComponentData))
    }

    private fun handlePaymentRequestResult(response: JSONObject): DropInServiceResult {
        return try {
            if (response.isNull("action")) {
                DropInServiceResult.Finished(response.getString("resultCode"))
            } else {
                DropInServiceResult.Action(response.getString("action"))
            }
        } catch (e: Exception) {
            DropInServiceResult.Error(e.toString())
        }
    }
}