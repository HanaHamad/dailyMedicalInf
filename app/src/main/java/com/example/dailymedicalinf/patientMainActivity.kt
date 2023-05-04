package com.example.dailymedicalinf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.databinding.ActivityDoctorMainBinding
import com.example.dailymedicalinf.databinding.ActivityPatientMainBinding
import com.example.dailymedicalinf.patient.AllAdvicesFragment
import com.example.dailymedicalinf.patient.PatientChattingFragment
import com.example.dailymedicalinf.patient.ProfileFragment
import com.example.dailymedicalinf.patient.RegesteredAdvicesFragment

class patientMainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPatientMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPatientMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.logOutBtn.setOnClickListener {
            Constans(this).logOut()
        }

        replaceFragment(AllAdvicesFragment())

        binding.patientNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.home ->{
                    replaceFragment(AllAdvicesFragment())
                }
                R.id.advices ->{
                    replaceFragment(RegesteredAdvicesFragment())
                }
                R.id.ChatS ->{
                    replaceFragment(PatientChattingFragment())
                }
                R.id.profile ->{
                    replaceFragment(ProfileFragment())
                }

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.patientFrameLayout,fragment)
        transaction.commit()
    }
}