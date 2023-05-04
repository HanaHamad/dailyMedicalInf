package com.example.dailymedicalinf.Doctor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivityAddAdviceBinding
import com.example.dailymedicalinf.databinding.ActivityAddAdviceBinding.inflate
import com.example.dailymedicalinf.databinding.ActivityAddVideoBinding
import java.util.*

const val TOPIC = "/topics/myTopic2"
class add_AdviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddAdviceBinding
    lateinit var const: Constans
    val TAG = "AddAdvice"

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    var imageUrl = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        const = Constans(this)

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Images/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            imageUrl = it.toString()
                            binding.selectImage.load(imageUrl)
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        binding.btn.setOnClickListener {
            when {
                binding.AdviceName.text.toString().isEmpty() -> {
                    Toast.makeText(this, "name is empty", Toast.LENGTH_SHORT).show()
                }
                imageUrl.isEmpty() -> {
                    Toast.makeText(this, "image is empty", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    newAdvice(binding.AdviceName.text.toString(), imageUrl)
                }
            }
        }

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }

        binding.selectImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }
    }

    private fun newAdvice(name: String, image: String) {
        val advice = mapOf(
            "adviceId" to UUID.randomUUID().toString(),
            "AdviceName" to name,
            "adviceImage" to image,
            "doctorEmail" to const.auth.currentUser!!.email,
            "NumberOfVideos" to 0,
            "NumberOfpatients" to 0,
            "patientIDs" to listOf<String>()
        )
        const.db.collection("Advices").add(advice).addOnSuccessListener {
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
            //--------------
            /*
            FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            Handler().postDelayed({
                PushNotification(
                    NotificationData("Advice App", "${advice.get("AdviceName").toString()} advice has added !!"), TOPIC
                ).also {
                    sendNotification(it)
                }
            },1500)
            //--------------
            onBackPressed()
            */
        }.addOnFailureListener {
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
        }

    }
/*
    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
*/
}




