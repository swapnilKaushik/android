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
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.swapnil.foodcall.R
import com.swapnil.foodcall.activity.activity.MainActivity
import com.swapnil.foodcall.activity.util.ConnectionManager
import kotlinx.android.synthetic.main.activity_signup.*
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var edtMobile: EditText
    lateinit var edtPassword: EditText
    lateinit var txtForgotPassword: TextView
    lateinit var txtRegister: TextView

    lateinit var btnLogIn: Button

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if(isLoggedIn) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        edtMobile = findViewById(R.id.edtLogInMobile)
        edtPassword = findViewById(R.id.edtLogInPassword)
        btnLogIn = findViewById(R.id.btnLogIn)
        txtForgotPassword = findViewById(R.id.txtForgetPassword)
        txtRegister = findViewById(R.id.txtSignUp)


        btnLogIn.setOnClickListener {

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", edtMobile.text.toString())
            jsonParams.put("password", edtPassword.text.toString())

            val queue = Volley.newRequestQueue(this)

            val url = "http://13.235.250.119/v2/login/fetch_result"

            if( ConnectionManager().checkConnectivity(this) ) {
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if(success) {
                            val data = data.getJSONObject("data")
                            val id = data.getString("user_id")
                            val name = data.getString("name")
                            val email = data.getString("email")
                            val mobile = data.getString("mobile_number")
                            val address = data.getString("address")

                            Toast.makeText(this, "Logged In...", Toast.LENGTH_SHORT).show()

                            savePreferences(id, name, email, address, mobile)
                            startActivity(Intent(this, MainActivity::class.java))
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

        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }

        txtRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, SignupActivity::class.java))
            finish()
        }

    }

    fun savePreferences(id: String, name: String, email: String, address: String, mobile: String) {
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
        sharedPreferences.edit().putString("user_id", id).apply()
        sharedPreferences.edit().putString("name", name).apply()
        sharedPreferences.edit().putString("email", email).apply()
        sharedPreferences.edit().putString("address", address).apply()
        sharedPreferences.edit().putString("mobile", mobile).apply()
    }

}
