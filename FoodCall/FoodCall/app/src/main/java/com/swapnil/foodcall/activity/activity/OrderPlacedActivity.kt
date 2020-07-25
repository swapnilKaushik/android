package com.swapnil.foodcall.activity.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.swapnil.foodcall.R

class OrderPlacedActivity : AppCompatActivity() {

    lateinit var btnOK: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        btnOK = findViewById(R.id.btnOK)
        btnOK.setOnClickListener {
            finishAffinity()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

}
