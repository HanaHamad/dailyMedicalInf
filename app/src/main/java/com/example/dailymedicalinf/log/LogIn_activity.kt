package com.example.dailymedicalinf.log

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Classes.UserData
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivityLogInBinding
import com.example.dailymedicalinf.doctorMainActivity
import com.example.dailymedicalinf.patientMainActivity

import com.google.firebase.firestore.ktx.toObject

class logIn_activity : AppCompatActivity() {

    private lateinit var et_email_login: TextView
    private lateinit var et_password_login: TextView
    private lateinit var btn_login: Button
    private lateinit var binding: ActivityLogInBinding
    private lateinit var tv_sign_up: TextView

    lateinit var const: Constans

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btn_login = findViewById(R.id.btn_login)

        val kind = getSharedPreferences("shared", MODE_PRIVATE).getString("kind", "")

        const = Constans(this)

        if (const.auth.currentUser != null && kind == getString(R.string.doctor)) {
            startActivity(Intent(this, doctorMainActivity::class.java))
            finish()

        } else if (const.auth.currentUser != null && kind == getString(R.string.patient)) {

            startActivity(Intent(this, patientMainActivity::class.java))
            finish()
        }
        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, signUp_activity::class.java))
            finish()
        }

        btn_login.setOnClickListener {
            if (et_email_login.text!!.isNotEmpty() && et_password_login.text!!.isNotEmpty()) {
                if (et_email_login.text.toString().contains("@")) {
                    loginToUserAccount(et_email_login.text.toString(), et_password_login.text.toString())
                } else {
                    Toast.makeText(this, "email not contain @ character !!", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(baseContext, "You didn't fill all fields !!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
    private fun loginToUserAccount(email: String, password: String) {

        val shared = getSharedPreferences("shared", MODE_PRIVATE).edit()

        const.auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext, "Authentication Successful.", Toast.LENGTH_SHORT)
                        .show()
                    const.db.collection("Users").document(email).get()
                        .addOnSuccessListener { document ->
                            if (document != null) {
                                val userData = document.toObject<UserData>()!!

                                if (userData.Kind == getString(R.string.patient)) {
                                    startActivity(Intent(this, patientMainActivity::class.java))
                                    shared.putString("kind", getString(R.string.patient)).apply()
                                    finish()

                                } else if (userData.Kind == getString(R.string.doctor)) {
                                    startActivity(Intent(this, doctorMainActivity::class.java))
                                    shared.putString("kind", getString(R.string.doctor)).apply()
                                    finish()
                                }
                            } else {
                                Toast.makeText(this, "Document empty.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }
                        .addOnFailureListener { _ ->
                            Toast.makeText(this, "Document failed.", Toast.LENGTH_SHORT)
                                .show()
                        }
                } else {
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

