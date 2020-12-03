package com.example.adyen.checkout.ui.components

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.adyen.checkout.adyen3ds2.Adyen3DS2Component
import com.adyen.checkout.base.ActionComponentData
import com.adyen.checkout.base.model.paymentmethods.PaymentMethod
import com.adyen.checkout.base.model.payments.request.PaymentComponentData
import com.adyen.checkout.base.model.payments.response.RedirectAction
import com.adyen.checkout.base.model.payments.response.Threeds2ChallengeAction
import com.adyen.checkout.base.model.payments.response.Threeds2FingerprintAction
import com.adyen.checkout.card.CardComponent
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.core.api.Environment
import com.adyen.checkout.redirect.RedirectComponent
import com.example.adyen.checkout.R
import com.example.adyen.checkout.service.CheckoutApiService
import com.example.adyen.checkout.service.Utils
import com.example.adyen.checkout.ui.result.ResultActivity
import kotlinx.android.synthetic.main.activity_card.*
import kotlinx.android.synthetic.main.activity_ideal.*
import java.util.*

class CardActivity : AppCompatActivity() {
    private var shopperLocale = Locale.ENGLISH

    companion object {
        private const val PM_KEY = "payment_method"

        fun start(context: Context, paymentMethod: PaymentMethod) {
            val intent = Intent(context, CardActivity::class.java)
            intent.putExtra(PM_KEY, paymentMethod)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        val viewModel =
            ViewModelProviders.of(this, ComponentViewModelFactory(CheckoutApiService.getInstance()))
                .get(ComponentViewModel::class.java)

        viewModel.errorMsgData.observe(this, Observer {
            Utils.showError(this.card, it)
        })

        viewModel.fetchConfig()

        viewModel.configData.observe(this, Observer { c ->
            val config = CardConfiguration.Builder(shopperLocale, Environment.TEST, c.getString("clientPublicKey")).setHolderNameRequire(true).build()
            val paymentMethod = intent.getParcelableExtra<PaymentMethod>(PM_KEY)
            val cardComponent = CardComponent.PROVIDER.get(
                this,
                paymentMethod!!,
                config
            )

            card_view.attach(cardComponent, this)

            cardComponent.observe(this, Observer {
                // When the shopper proceeds to pay, pass the `it.data` to your server to send a /payments request
                if (it.isValid) {
                    card_pay_button.isEnabled = true
                    card_pay_button.setOnClickListener { _ ->
                        val paymentComponentData = PaymentComponentData.SERIALIZER.serialize(it.data)
                        viewModel.initPayment(paymentComponentData)
                        card_pay_button.isEnabled = false
                    }
                }
            })

            cardComponent.observeErrors(this, Observer {
                Utils.showError(this.ideal, "ERROR - ${it.errorMessage}")
            })
        })
        val redirectComponent = RedirectComponent.PROVIDER.get(this)
        val threedsComponent = Adyen3DS2Component.PROVIDER.get(this)
        // Handle 3DS2 authentication from payment
        threedsComponent.observe(this, Observer {
            viewModel.submitDetails(ActionComponentData.SERIALIZER.serialize(it))
        })

        viewModel.actionData.observe(this, Observer {
            when (it.type) {
                RedirectAction.ACTION_TYPE -> {
                    redirectComponent.handleAction(this, it)
                }
                Threeds2FingerprintAction.ACTION_TYPE, Threeds2ChallengeAction.ACTION_TYPE -> {
                    threedsComponent.handleAction(this, it)
                }
            }
        })

        viewModel.paymentResData.observe(this, Observer {
            // start result intent
            ResultActivity.start(this, it)
        })
    }
}
