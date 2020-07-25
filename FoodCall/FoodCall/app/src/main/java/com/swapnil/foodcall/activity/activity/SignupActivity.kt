package com.swapnil.foodcall.activity.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
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
import com.swapnil.foodcall.activity.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class SignupActivity : AppCompatActivity() {

    lateinit var edtName: EditText
    lateinit var edtEmail: EditText
    lateinit var edtMobile: EditText
    lateinit var edtAddress: EditText
    lateinit var edtPassword: EditText
    lateinit var edtCnfPassword: EditText
    lateinit var btnRegister: Button
    lateinit var txtLogin: TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        edtName = findViewById(R.id.edtUserName)
        edtEmail = findViewById(R.id.edtEmail)
        edtMobile = findViewById(R.id.edtRegMobile)
        edtAddress = findViewById(R.id.edtAddress)
        edtPassword = findViewById(R.id.edtPassword)
        edtCnfPassword = findViewById(R.id.edtCnfPassword)
        btnRegister = findViewById(R.id.btnRegister)
        txtLogin = findViewById(R.id.txtLogin)

        btnRegister.setOnClickListener {
            if( edtName.text.length < 3 ) {
                Toast.makeText(this@SignupActivity, "Invalid Name!", Toast.LENGTH_SHORT).show()
            } else if( !Patterns.EMAIL_ADDRESS.matcher(edtEmail.text).matches() ) {
                Toast.makeText(this@SignupActivity, "Invalid Email!", Toast.LENGTH_SHORT).show()
            }else if( edtMobile.text.length!=10 ) {
                Toast.makeText(this@SignupActivity, "Invalid Number!", Toast.LENGTH_SHORT).show()
            } else if( edtAddress.text.length < 6 ) {
                Toast.makeText(this@SignupActivity, "Invalid Address!", Toast.LENGTH_SHORT).show()
            } else if( edtPassword.text.length < 4 ) {
                Toast.makeText(this@SignupActivity, "Invalid Password!", Toast.LENGTH_SHORT).show()
            } else if( edtPassword.text.toString() != edtCnfPassword.text.toString() ) {
                Toast.makeText(this@SignupActivity, "Password Mismatch!", Toast.LENGTH_SHORT).show()
            } else {

                val jsonParams = JSONObject()
                jsonParams.put("name", edtName.text.toString())
                jsonParams.put("mobile_number", edtMobile.text.toString())
                jsonParams.put("password", edtPassword.text.toString())
                jsonParams.put("address", edtAddress.text.toString())
                jsonParams.put("email", edtEmail.text.toString())

                val queue = Volley.newRequestQueue(this)

                val url = "http://13.235.250.119/v2/register/fetch_result"

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

                                Toast.makeText(this, "Profile created successfully!", Toast.LENGTH_SHORT).show()

                                savePreferences(id, name, email, address, mobile)
                                startActivity(Intent(this@SignupActivity, MainActivity::class.java))
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

        txtLogin.setOnClickListener {
            startActivity(Intent(this@SignupActivity, LoginActivity::class.java))
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
