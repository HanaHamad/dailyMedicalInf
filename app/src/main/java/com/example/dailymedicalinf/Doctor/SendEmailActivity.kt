package com.example.dailymedicalinf.Doctor

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.dailymedicalinf.databinding.ActivitySendEmailBinding


class SendEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySendEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.sendEmailbtn.setOnClickListener {
            val recipient = binding.reciEt.text.toString().trim()
            val subject = binding.subjectEt.text.toString().trim()
            val message = binding.messageEt.text.toString().trim()

            sendEmail(recipient , subject , message)
        }

    }
    @SuppressLint("IntentReset")
    private fun sendEmail(recipient:String, subject:String, message:String){
        val mIntent = Intent(Intent.ACTION_SEND)
        mIntent.data = Uri.parse("mailTo:")
        mIntent.type = "text/plain"
        mIntent.putExtra(Intent.EXTRA_EMAIL , arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT , subject)
        mIntent.putExtra(Intent.EXTRA_TEXT , message)

        try {
            startActivity(Intent.createChooser(mIntent , "Choose email client"))

        }catch (e: Exception){
            Toast.makeText(this ,e.message , Toast.LENGTH_LONG).show()
        }

    }

    override fun onStop() {
        super.onStop()

    }



}