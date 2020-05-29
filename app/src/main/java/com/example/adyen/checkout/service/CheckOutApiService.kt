package com.example.adyen.checkout.service

import android.content.Context
import com.adyen.checkout.base.model.PaymentMethodsApiResponse
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.adyen.checkout.dropin.service.CallResult
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.Volley
import com.example.adyen.checkout.BuildConfig
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class CheckoutApiService(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: CheckoutApiService? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CheckoutApiService(context).also {
                    INSTANCE = it
                }
            }
    }

    private val queue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }

    private val baseURL: String by lazy {
        "${BuildConfig.SERVER_URL}/api"
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        queue.add(req)
    }

    // This is asynchronous request
    fun getConfig(
        resultListener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener
    ) {
        val url = "$baseURL/getConfig"

        // Request a string response from the provided URL.
        val stringRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            resultListener,
            errorListener
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    // This is asynchronous request
    fun getPaymentMethods(
        resultListener: (resp: PaymentMethodsApiResponse) -> Unit,
        errorListener: Response.ErrorListener
    ) {
        val url = "$baseURL/getPaymentMethods"

        // Request a string response from the provided URL.
        val stringRequest = JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                resultListener(PaymentMethodsApiResponse.SERIALIZER.deserialize(response))
            },
            errorListener
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun filterPaymentMethodByType(ls: MutableList<PaymentMethod>?, type: ComponentType): PaymentMethod? {
        return if (ls != null) {
            ls.filter { it.type == type.id }[0]
        } else null
    }

    // This is synchronous request
    fun initPayment(req: JSONObject): CallResult {
        return makeSyncPaymentRequest(
            "$baseURL/initiatePayment",
            req.getJSONObject("paymentMethod")
        )
    }

    // This is synchronous request
    fun submitAdditionalDetails(req: JSONObject): CallResult {
        return makeSyncPaymentRequest("$baseURL/submitAdditionalDetails", req)
    }

    private fun makeSyncPaymentRequest(url: String, req: JSONObject): CallResult {
        return try {
            val response = makeSyncRequest(url, req) // this will block
            if (response.isNull("action")) {
                CallResult(CallResult.ResultType.FINISHED, response.getString("resultCode"))
            } else {
                CallResult(CallResult.ResultType.ACTION, response.getString("action"))
            }
        } catch (e: Exception) {
            CallResult(CallResult.ResultType.ERROR, e.toString())
        }
    }


    private fun makeSyncRequest(url: String, req: JSONObject?): JSONObject {
        val future = RequestFuture.newFuture<JSONObject>()
        val request = JsonObjectRequest(
            Request.Method.POST,
            url,
            req,
            future,
            future
        )
        queue.add(request)

        return future.get(5, TimeUnit.SECONDS)
    }
}