package com.example.adyen.checkout.service

import com.adyen.checkout.dropin.service.CallResult
import com.adyen.checkout.dropin.service.DropInService
import org.json.JSONObject

class DropinService : DropInService() {
    private var checkoutApiService = CheckoutApiService.getInstance(this.applicationContext)

    override fun makePaymentsCall(paymentComponentData: JSONObject): CallResult {
        return handlePaymentRequestResult(checkoutApiService.initPayment(paymentComponentData))
    }

    override fun makeDetailsCall(actionComponentData: JSONObject): CallResult {
        return handlePaymentRequestResult(checkoutApiService.submitAdditionalDetails(actionComponentData))
    }

    private fun handlePaymentRequestResult(response: JSONObject): CallResult {
        return try {
            if (response.isNull("action")) {
                CallResult(CallResult.ResultType.FINISHED, response.getString("resultCode"))
            } else {
                CallResult(CallResult.ResultType.ACTION, response.getString("action"))
            }
        } catch (e: Exception) {
            CallResult(CallResult.ResultType.ERROR, e.toString())
        }
    }
}