package com.swapnil.foodcall.activity.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.swapnil.foodcall.R


class ProfileFragment : Fragment() {

    lateinit var txtUserName: TextView
    lateinit var txtMobile: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView

    var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = context?.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        txtUserName = view.findViewById(R.id.txtUserName)
        txtMobile = view.findViewById(R.id.txtMobile)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtAddress)

        txtUserName.text = sharedPreferences?.getString("name", "name")
        txtMobile.text = "+91-" + sharedPreferences?.getString("mobile", "9638527410")
        txtEmail.text = sharedPreferences?.getString("email", "abc@example.com")
        txtAddress.text = sharedPreferences?.getString("address", "address")

        return view
    }

}
