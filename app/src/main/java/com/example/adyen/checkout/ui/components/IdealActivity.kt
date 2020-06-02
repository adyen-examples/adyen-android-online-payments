package com.example.adyen.checkout.ui.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.ideal.IdealComponent
import com.adyen.checkout.ideal.IdealConfiguration
import com.adyen.checkout.redirect.RedirectComponent
import com.example.adyen.checkout.R
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.Utils
import com.example.adyen.checkout.ui.result.ResultActivity
import kotlinx.android.synthetic.main.activity_ideal.*
import java.util.*

class IdealActivity : AppCompatActivity() {
    private var shopperLocale = Locale.ENGLISH

    companion object {
        private const val PM_KEY = "payment_method"

        fun start(context: Context, paymentMethod: PaymentMethod) {
            val intent = Intent(context, IdealActivity::class.java)
            intent.putExtra(PM_KEY, paymentMethod)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ideal)

        val viewModel =
            ViewModelProviders.of(this, ComponentViewModelFactory(CheckoutApiService.getInstance()))
                .get(ComponentViewModel::class.java)

        viewModel.errorMsgData.observe(this, Observer {
            Utils.showError(this.ideal, it)
        })

        val config = IdealConfiguration.Builder(shopperLocale, Environment.TEST).build()
        val paymentMethod = intent.getParcelableExtra<PaymentMethod>(PM_KEY)
        val idealComponent = IdealComponent.PROVIDER.get(
            this,
            paymentMethod!!,
            config
        )

        ideal_view.attach(idealComponent, this)

        idealComponent.observe(this, Observer {
            // When the shopper proceeds to pay, pass the `it.data` to your server to send a /payments request
            if (it.isValid) {
                ideal_pay_button.isEnabled = true
                ideal_pay_button.setOnClickListener { _ ->
                    val paymentComponentData = PaymentComponentData.SERIALIZER.serialize(it.data)
                    viewModel.initPayment(paymentComponentData)
                    ideal_pay_button.isEnabled = false
                }
            }
        })

        idealComponent.observeErrors(this, Observer {
            Utils.showError(this.ideal, "ERROR - ${it.errorMessage}")
        })

        viewModel.actionData.observe(this, Observer {
            val redirectComponent = RedirectComponent.PROVIDER.get(this)
            redirectComponent.handleAction(this, it)
        })

        viewModel.paymentResData.observe(this, Observer {
            // start result intent
            ResultActivity.start(this, it)
        })
    }
}
