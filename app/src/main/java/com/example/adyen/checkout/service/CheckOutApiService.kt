package com.example.adyen.checkout.service

import com.adyen.checkout.base.model.PaymentMethodsApiResponse
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.example.adyen.checkout.BuildConfig
import org.json.JSONObject
import java.io.File
import java.util.concurrent.TimeUnit

class CheckoutApiService() {
    companion object {
        @Volatile
        private var INSTANCE: CheckoutApiService? = null
        fun getInstance() =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: CheckoutApiService().also {
                    INSTANCE = it
                }
            }
    }

    private val queue: RequestQueue by lazy {
        // Instantiate the cache
        val cache = DiskBasedCache(File("volley")) // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())

        // Instantiate the RequestQueue with the cache and network. Start the queue.
        RequestQueue(cache, network).apply {
            start()
        }

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

        // Request a json response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            resultListener,
            errorListener
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    // This is asynchronous request
    fun getPaymentMethods(
        resultListener: (resp: PaymentMethodsApiResponse) -> Unit,
        errorListener: Response.ErrorListener
    ) {
        val url = "$baseURL/getPaymentMethods"

        // Request a json response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.POST, url, null,
            Response.Listener { response ->
                resultListener(PaymentMethodsApiResponse.SERIALIZER.deserialize(response))
            },
            errorListener
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    fun filterPaymentMethodByType(ls: MutableList<PaymentMethod>?, type: ComponentType): PaymentMethod? {
        return if (ls != null) {
            ls.filter { it.type == type.id }[0]
        } else null
    }

    // This is asynchronous request
    fun initPayment(
        req: JSONObject,
        resultListener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener
    ) {
        val url = "$baseURL/initiatePayment"

        // Request a json response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.POST, url, req.getJSONObject("paymentMethod"),
            resultListener,
            errorListener
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    // This is synchronous request
    fun initPayment(req: JSONObject): JSONObject {
        return makeSyncRequest(
            "$baseURL/initiatePayment",
            req.getJSONObject("paymentMethod")
        )
    }

    // This is synchronous request
    fun submitAdditionalDetails(
        req: JSONObject,
        resultListener: Response.Listener<JSONObject>,
        errorListener: Response.ErrorListener
    ) {
        val url = "$baseURL/submitAdditionalDetails"

        // Request a json response from the provided URL.
        val request = JsonObjectRequest(
            Request.Method.POST, url, req,
            resultListener,
            errorListener
        )

        // Add the request to the RequestQueue.
        queue.add(request)
    }

    // This is asynchronous request
    fun submitAdditionalDetails(req: JSONObject): JSONObject {
        return makeSyncRequest("$baseURL/submitAdditionalDetails", req)
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