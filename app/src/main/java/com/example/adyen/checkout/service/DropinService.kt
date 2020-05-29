package com.example.adyen.checkout.service

import com.adyen.checkout.dropin.service.CallResult
import com.adyen.checkout.dropin.service.DropInService
import org.json.JSONObject

class DropinService : DropInService() {
    override fun makePaymentsCall(paymentComponentData: JSONObject): CallResult {
        return CheckoutApiService.getInstance(this.applicationContext)
            .initPayment(paymentComponentData)
    }

    override fun makeDetailsCall(actionComponentData: JSONObject): CallResult {
        return CheckoutApiService.getInstance(this.applicationContext)
            .submitAdditionalDetails(actionComponentData)
    }
}