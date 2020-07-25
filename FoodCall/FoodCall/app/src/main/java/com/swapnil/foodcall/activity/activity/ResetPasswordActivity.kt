package com.swapnil.foodcall.activity.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
import kotlinx.android.synthetic.main.activity_forgot_password.*
import org.json.JSONException
import org.json.JSONObject

class ResetPasswordActivity : AppCompatActivity() {

    lateinit var edtOtp: EditText
    lateinit var edtNewPassword: EditText
    lateinit var edtCnfNewPassword: EditText
    lateinit var btnResetPassword: Button

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        edtOtp = findViewById(R.id.edtOtp)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtCnfNewPassword = findViewById(R.id.edtCnfNewPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)

        val mobile = intent.getStringExtra("mobile")

        btnResetPassword.setOnClickListener {

            if( edtNewPassword.text.toString() != edtCnfNewPassword.text.toString() ) {
                Toast.makeText(this, "Password misMatch!", Toast.LENGTH_SHORT)
                    .show()
            } else if( edtOtp.text.length < 4 ) {
                Toast.makeText(this, "Invalid OTP!", Toast.LENGTH_SHORT)
                    .show()
            } else {

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobile)
                jsonParams.put("password", edtNewPassword.text.toString())
                jsonParams.put("otp", edtOtp.text.toString())

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                if (ConnectionManager().checkConnectivity(this)) {
                    val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    Toast.makeText(this@ResetPasswordActivity, data.getString("successMessage"), Toast.LENGTH_SHORT).show()

                                    sharedPreferences.edit().clear().apply()

                                    startActivity( Intent( this, LoginActivity::class.java ) )
                                    finish()
                                } else {
                                    Toast.makeText(this, data.getString("errorMessage"), Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(this, "Some unexpected error occured!!!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }, Response.ErrorListener {
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
