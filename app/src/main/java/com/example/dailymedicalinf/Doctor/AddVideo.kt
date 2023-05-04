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
import com.example.dailymedicalinf.databinding.ActivityAddAdviceBinding
import com.example.dailymedicalinf.databinding.ActivityAddVideoBinding
import java.util.*

class AddVideo : AppCompatActivity() {


    lateinit var const: Constans
    private var adviceId12 = ""
    private lateinit var binding: ActivityAddVideoBinding

    private var videoFile = "Files/sample.pdf"
    private var videoUrl = "https://firebasestorage.googleapis.com/v0/b/courses-app-5c3b2.appspot.com/o/Videos%2FVID-20220429-WA0007.mp4?alt=media&token=f45c9e43-d495-4d1f-9977-c83a3e507a85"
    private var videoImage = "https://firebasestorage.googleapis.com/v0/b/courses-app-5c3b2.appspot.com/o/Images%2FScreenshot_2022-05-04-14-41-19-869_com.android.vending.jpg?alt=media&token=14b78afa-9474-4547-83be-d0da507008a6"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        const = Constans(this)

        adviceId12 = intent.getStringExtra("AdviceId").toString()
        var name = intent.getStringExtra("nameadd").toString()
        var image = intent.getStringExtra("imagevedio").toString()

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Videos/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            videoUrl = it.toString()
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        val resultLauncher2 =
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
                            videoImage = it.toString()
                            binding.selectImage.load(videoImage)
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        val resultLauncher3 =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(this, uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Files/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)
                    uploadTask.addOnFailureListener { e ->
                        Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            videoFile = "Files/${new_uri.lastPathSegment}"
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                            Toast.makeText(this, "${new_uri.lastPathSegment}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        binding.btnn.setOnClickListener {
            newVideo(adviceId12 , name , image)

        }

        binding.selectVideo.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select Video"))
        }

        binding.selectImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher2.launch(Intent.createChooser(intent, "Select Image"))
        }

        binding.btnFile.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher3.launch(Intent.createChooser(intent, "Select File"))
        }

    }

    private fun newVideo(adviceId12 : String , name :String , image:String) {

        when {
            binding.VideoName.text.toString().isEmpty() -> {
                Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show()
            }
            binding.VideoDesc.text.toString().isEmpty() -> {
                Toast.makeText(this, "Description is Empty", Toast.LENGTH_SHORT).show()
            }
            videoUrl.isEmpty() -> {
                Toast.makeText(this, "VideoUrl is Empty", Toast.LENGTH_SHORT).show()
            }
            videoImage.isEmpty() -> {
                Toast.makeText(this, "VideoImage is Empty", Toast.LENGTH_SHORT).show()
            }
            videoFile.isEmpty() -> {
                Toast.makeText(this, "VideoFile is Empty", Toast.LENGTH_SHORT).show()
            }
            else -> {
                Log.e("vedd","$adviceId12")
                const.db.collection("Advices").whereEqualTo("adviceId", adviceId12).get().addOnSuccessListener {
                    val videoNumber =
                        it.documents[0].get("NumberOfVideos").toString().toLong() + 1
                    Log.e("rrrr","$adviceId12")
                    val video = mapOf(
                        "VideoId" to UUID.randomUUID().toString(),
                        "VideoName" to binding.VideoName.text.toString(),
                        "VideoDesc" to binding.VideoDesc.text.toString(),
                        "VideoNumber" to videoNumber,
                        "VideoUrl" to videoUrl,
                        "VideoImage" to videoImage,
                        "VideoFile" to videoFile,
                        "AdviceId" to adviceId12
                    )

                    const.db.collection("Videos").add(video).addOnSuccessListener {
                        Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
                        const.db.collection("Advices").whereEqualTo("adviceId", adviceId12)
                            .get().addOnSuccessListener { documents ->
                                for (document in documents) {
                                    const.db.collection("Advices").document(document.id)
                                        .update(
                                            "NumberOfVideos",
                                            (document.get("NumberOfVideos").toString().toLong() + 1)
                                        )
                                }
                            }
                        onBackPressed()
                    }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show()
                        }
                }

            }
        }


    }


}
