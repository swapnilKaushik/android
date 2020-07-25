package com.swapnil.foodcall.activity.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var edtMobile: EditText
    lateinit var edtEmail: EditText
    lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        edtMobile = findViewById(R.id.edtMobile)
        edtEmail = findViewById(R.id.edtEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {

            if( edtMobile.text.length!=10 ) {
                Toast.makeText(this, "Invalid Number!", Toast.LENGTH_SHORT)
                    .show()
            } else if( edtEmail.text.length < 4 ) {
                Toast.makeText(this, "Invalid Email!", Toast.LENGTH_SHORT)
                    .show()
            } else {

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", edtMobile.text.toString())
                jsonParams.put("email", edtEmail.text.toString())

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                if (ConnectionManager().checkConnectivity(this)) {
                    val jsonObjectRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    Toast.makeText(
                                        this,
                                        "OTP has been sent on your registered eMailID",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()

                                    val intent =Intent(this@ForgotPasswordActivity, ResetPasswordActivity::class.java )
                                    intent.putExtra("mobile", jsonParams.getString("mobile_number"))
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, data.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(this, "Some unexpected error occured!!!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }, Response.ErrorListener {
                            if( this!= null )
                                Toast.makeText(this, "Volley error occured!!!", Toast.LENGTH_SHORT).show()
                        }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "3a19289853e0b7"
                                return headers
                            }
                        }

                    queue.add(jsonObjectRequest)
                } else {
                    //Internet NOT available
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection NOT Found!")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        this.finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
            }

        }

    }
}
