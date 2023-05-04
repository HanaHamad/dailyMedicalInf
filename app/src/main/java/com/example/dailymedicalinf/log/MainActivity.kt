package com.example.dailymedicalinf.log

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.dailymedicalinf.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val shared = getSharedPreferences("PalliativeMedicineApp", MODE_PRIVATE)
        val isLogged = shared.getBoolean("isLogged",false)
        if (isLogged){
            startActivity(Intent(this,logIn_activity::class.java))
            finish()
        }
        binding.btn1.setOnClickListener {
            shared.edit().putBoolean("isLogged",true).apply()
            startActivity(Intent(this,logIn_activity::class.java))
            finish()

        }
    }
}