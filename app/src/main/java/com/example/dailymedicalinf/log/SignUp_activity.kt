package com.example.dailymedicalinf.log

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivitySignUpBinding
import com.example.dailymedicalinf.doctorMainActivity
import com.example.dailymedicalinf.patientMainActivity

import java.util.*
import kotlin.collections.HashMap

class signUp_activity : AppCompatActivity(), DatePickerDialog.OnDateSetListener{

    lateinit var const: Constans
    var day = 0
    var month: Int = 0
    var year: Int = 0
    var currentDay = 0
    var currentMonth: Int = 0
    var currentYear: Int = 0
    lateinit var btnsign :Button
    lateinit var log:TextView
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnsign = findViewById(R.id.btn_sign_up)
        log = findViewById(R.id.tv_login)
        const = Constans(this)

        log.setOnClickListener {
            startActivity(Intent(this, logIn_activity::class.java))
            finish()
        }

        binding.etBirthDate.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)

            val datePickerDialog =
                DatePickerDialog(this, this, year, month,day)
            datePickerDialog.show()
        }


        btnsign.setOnClickListener {

            if (binding.etFName.text!!.isNotEmpty() &&
                binding.etLName.text!!.isNotEmpty() &&
                binding.etMName.text!!.isNotEmpty() &&
                binding.etEmail.text!!.isNotEmpty() &&
                binding.etPassword.text!!.isNotEmpty() &&
                binding.etBirthDate.text.isNotEmpty() &&
                binding.etMobile.text!!.isNotEmpty()
            ) {
                            val choice = binding.radioGroup.checkedRadioButtonId
                            val radioButton = findViewById<RadioButton>(choice)
                            saveUserData(
                                binding.etFName.text.toString(),
                                binding.etLName.text.toString(),
                                binding.etMName.text.toString(),
                                binding.etEmail.text.toString(),
                                binding.etPassword.text.toString(),
                                binding.etMobile.text.toString(),
                                binding.etBirthDate.text.toString(),
                                binding.etAddress.text.toString(),
                                radioButton.text.toString()
                            )
            }

        }
    }

    private fun saveUserData(
        et_fName: String,
        et_lName:String,
        et_mName: String,
        email: String,
        password: String,
        Phone_number: String,
        birth: String,
        et_address: String,
        kind: String,

    ) {

        val const = Constans(this)
        const.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = hashMapOf(
                        "fname" to et_fName,
                        "mname" to et_mName,
                        "lname" to et_lName,
                        "Email" to email,
                        "address" to et_address,
                        "Phone" to Phone_number,
                        "Password" to password,
                        "Kind" to kind,
                        "Birthdate" to birth,
                        "Users" to hashMapOf<String, HashMap<String, *>>()
                    )

                    val shared = getSharedPreferences("shared", MODE_PRIVATE).edit()
                    const.db.collection("Users")
                        .document(email)
                        .set(user)
                        .addOnSuccessListener {
                            if (kind == getString(R.string.doctor)) {
                                Toast.makeText(
                                    baseContext,
                                    "Sign up Successful.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                shared.putString("kind", getString(R.string.doctor)).apply()
                                val i = Intent(this, doctorMainActivity::class.java)
                                startActivity(i)
                                finish()
                            } else if (kind == getString(R.string.patient)) {
                                Toast.makeText(
                                    baseContext,
                                    "Sign up Successful.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                shared.putString("kind", getString(R.string.patient)).apply()
                                startActivity(Intent(this, patientMainActivity::class.java))
                                finish()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(
                                baseContext,
                                "Store user data failed.",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                } else {
                    Toast.makeText(baseContext, task.exception.toString(), Toast.LENGTH_SHORT).show()
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        currentDay = p3
        currentYear = p1
        currentMonth = p2+1

        binding.etBirthDate.setText(String.format("%02d",currentDay)+"-"+String.format("%02d",currentMonth)+"-"+currentYear)
    }
}