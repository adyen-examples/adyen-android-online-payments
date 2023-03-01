package com.example.adyen.checkout.ui.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.adyen.checkout.components.ActionComponentData
import com.adyen.checkout.redirect.RedirectComponent
import com.adyen.checkout.redirect.RedirectConfiguration
import com.example.adyen.checkout.MainActivity
import com.example.adyen.checkout.R
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.ComponentType
import com.example.adyen.checkout.ui.components.ComponentViewModel
import com.example.adyen.checkout.ui.components.ComponentViewModelFactory


class ResultActivity : AppCompatActivity() {
    companion object {
        const val RESULT_KEY = "payment_result"
        const val TYPE_KEY = "integration_type"

        fun start(context: Context, paymentResult: String) {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(RESULT_KEY, paymentResult)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this, ComponentViewModelFactory(CheckoutApiService.getInstance()))[ComponentViewModel::class.java]

        viewModel.fetchConfig()

        setContentView(R.layout.activity_result)

        val res = intent?.getStringExtra(RESULT_KEY) ?: "Processing"

        val txt: TextView = findViewById(R.id.result_text)
        txt.text = res

        val btnCheckout: Button = findViewById(R.id.btn_home)
        btnCheckout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val type = intent?.getStringExtra(TYPE_KEY)

        if (type != ComponentType.DROPIN.id) {
            // Redirection from payment is handled here as the flow redirects here

            viewModel.configData.observe(this) { c ->
                val redirectConfiguration = RedirectConfiguration.Builder(this, c.getString("clientPublicKey")).build()
                val redirectComponent = RedirectComponent.PROVIDER.get(this, application, redirectConfiguration)
                redirectComponent.observe(this) {
                    viewModel.submitDetails(ActionComponentData.SERIALIZER.serialize(it))
                }

                if (intent != null && intent.action == Intent.ACTION_VIEW) {
                    val data = intent.data
                    if (data != null && data.toString().startsWith("adyencheckoutcomp://")) {
                        redirectComponent.handleIntent(intent)
                    }
                }
            }

            viewModel.paymentResData.observe(this) {
                txt.text = it
            }
        }

    }
}
