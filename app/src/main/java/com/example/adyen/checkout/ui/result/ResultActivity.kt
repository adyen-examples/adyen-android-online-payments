package com.example.adyen.checkout.ui.result

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.adyen.checkout.dropin.DropIn
import com.example.adyen.checkout.MainActivity
import com.example.adyen.checkout.R


class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val res = intent.getStringExtra(DropIn.RESULT_KEY)

        val txt: TextView = findViewById(R.id.result_text)
        txt.text = res

        val btnCheckout: Button = findViewById(R.id.btn_home);
        btnCheckout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }
    }
}
