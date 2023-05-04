package com.example.dailymedicalinf.Doctor

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivityAdviceContentBinding
import com.example.dailymedicalinf.databinding.ActivityEditAdviceBinding
import java.util.*

class edit_adviceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditAdviceBinding
    lateinit var const: Constans
    var adviceId =""
    var imageUrl = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdviceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        const = Constans(this)

        val adviceId = intent.getStringExtra("Advice123").toString()
        val name = intent.getStringExtra("name123").toString()
        val image = intent.getStringExtra("image123").toString()

        Log.e("advices","image $image id $adviceId , name $name" )
        getAdviceData(adviceId , name, image)

        val resultLauncher =
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
                /*imageUrl.isEmpty() -> {
                    Toast.makeText(this, "image is empty", Toast.LENGTH_SHORT).show()
                }*/
                else -> {
                    updateAdvice(binding.AdviceName.text.toString(), imageUrl , adviceId)
                }
            }
        }
        binding.selectImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }
    }
    private fun getAdviceData(adviceId: String , name: String , imageUrl: String){
      /*  const.db.collection("Advices").whereEqualTo("adviceId",adviceId).get()
            .addOnSuccessListener {
                Log.e("aasss",it.documents[0].get("AdviceName").toString())
                binding.AdviceName.setText(it.documents[0].get("AdviceName").toString())
           // binding.AdviceName.setText(it.documents[0].get("AdviceName").toString())
            imageUrl = it.documents[0].get("adviceImage").toString()
            binding.selectImage.load(imageUrl)
        }*/
        const.db.collection("Advices").whereEqualTo("adviceId", adviceId).get()
            .addOnSuccessListener {
                Log.e("aasss",it.documents[0].get("AdviceName").toString())
                binding.AdviceName.setText(it.documents[0].get("AdviceName").toString())

                if (it.documents[0].get("adviceImage").toString().isNotEmpty()) {
                    binding.selectImage.load(it.documents[0].get("adviceImage").toString())
                }
            }.addOnFailureListener {
                Log.e("error message","AndACustomTag")
            }
    }

    private fun updateAdvice(adviceId23456 :String ,name: String, imageUrl: String) {
        val advice = mapOf(
            "AdviceName" to name,
            "adviceImage" to imageUrl,
            "adviceId" to adviceId23456
        )
        Log.e("mmmm","$name , $adviceId23456")
        const.db.collection("Advices").whereEqualTo("adviceId",adviceId23456)
            .get()
            .addOnSuccessListener {
            const.db.collection("Advices")
                .document(it.documents[0].id)
                .update(advice)

            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
            onBackPressed()
        }.addOnFailureListener {
            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
        }
    }

}