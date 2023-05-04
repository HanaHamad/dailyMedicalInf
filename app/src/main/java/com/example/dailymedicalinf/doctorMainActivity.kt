package com.example.dailymedicalinf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.dailymedicalinf.Classes.Constans

import com.example.dailymedicalinf.DoctorFragment.DoctorAdviceFragment
import com.example.dailymedicalinf.DoctorFragment.DoctorEditFragment
import com.example.dailymedicalinf.DoctorFragment.DoctorChattingFragment
import com.example.dailymedicalinf.DoctorFragment.DoctorProfileFragment
import com.example.dailymedicalinf.databinding.ActivityAddAdviceBinding
import com.example.dailymedicalinf.databinding.ActivityDoctorMainBinding
import com.example.dailymedicalinf.patient.ProfileFragment

class doctorMainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDoctorMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDoctorMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(DoctorAdviceFragment())

        binding.logOutBtn.setOnClickListener {
            Constans(this).logOut()
        }

        binding.DoctorNavigationView.setOnItemSelectedListener{
            when(it.itemId){
                R.id.advice ->{
                    replaceFragment(DoctorAdviceFragment())
                }
                R.id.Edit ->{
                    replaceFragment(DoctorEditFragment())
                }
                R.id.Chat ->{
                    replaceFragment(DoctorChattingFragment())
                }
                R.id.profile ->{
                    replaceFragment(DoctorProfileFragment())
                }

            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.DoctorFrameLayout,fragment)
        transaction.commit()
    }
}