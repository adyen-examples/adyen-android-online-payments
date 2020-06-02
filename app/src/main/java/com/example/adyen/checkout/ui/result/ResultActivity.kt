package com.example.adyen.checkout.ui.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adyen.checkout.base.ActionComponentData
import com.adyen.checkout.redirect.RedirectComponent
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
            val viewModel = ViewModelProviders.of(this, ComponentViewModelFactory(CheckoutApiService.getInstance()))
                .get(ComponentViewModel::class.java)

            val redirectComponent = RedirectComponent.PROVIDER.get(this)
            redirectComponent.observe(this, Observer {
                viewModel.submitDetails(ActionComponentData.SERIALIZER.serialize(it))
            })

            if (intent != null && intent.action == Intent.ACTION_VIEW) {
                val data = intent.data
                if (data != null && data.toString().startsWith("adyencheckoutcomp://")) {
                    redirectComponent.handleRedirectResponse(data)
                }
            }

            viewModel.paymentResData.observe(this, Observer {
                txt.text = it
            })
        }

    }
}
