package com.example.dailymedicalinf.Doctor

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Classes.VideoData
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivityAdviceContentBinding
import com.example.dailymedicalinf.databinding.ActivityEditVideoBinding
import com.google.firebase.firestore.ktx.toObject

class editVideo : AppCompatActivity() {

    private lateinit var binding: ActivityEditVideoBinding
    lateinit var const: Constans

    private var videoId = ""
    private var newVideoUrl = ""
    private var VideoImage = ""
    private var VideoFile = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        const = Constans(this)


        videoId = intent.getStringExtra("VideoId").toString()

        getVideoData()

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
                            newVideoUrl = it.toString()
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
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
                            VideoImage = it.toString()
                            binding.newImage.load(VideoImage)
                            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
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
                            VideoFile = it.toString()
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        binding.VSave.setOnClickListener {
            updateVideo()
        }

        binding.newVideo.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select Video"))
        }

        binding.newImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher2.launch(Intent.createChooser(intent, "Select image"))
        }

        binding.VFile.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher3.launch(Intent.createChooser(intent, "Select File"))
        }

    }

    private fun updateVideo() {
        when {
            binding.VName.text.toString().isEmpty() -> {
                Toast.makeText(this, "Name is Empty", Toast.LENGTH_SHORT).show()
            }
            binding.VDesc.text.toString().isEmpty() -> {
                Toast.makeText(this, "Description is Empty", Toast.LENGTH_SHORT).show()
            }
            newVideoUrl.isEmpty() -> {
                Toast.makeText(this, "VideoUrl is Empty", Toast.LENGTH_SHORT).show()
            }
            VideoImage.isEmpty() -> {
                Toast.makeText(this, "VideoImage is Empty", Toast.LENGTH_SHORT).show()
            }
            VideoFile.isEmpty() -> {
                Toast.makeText(this, "VideoFile is Empty", Toast.LENGTH_SHORT).show()
            }
            else -> {
                val video = mapOf(
                    "VideoName" to binding.VName.text.toString(),
                    "VideoDesc" to binding.VDesc.text.toString(),
                    "VideoUrl" to newVideoUrl,
                    "VideoFile" to VideoFile,
                    "VideoImage" to VideoImage,
                )
                const.db.collection("Videos").whereEqualTo("VideoId", videoId).get()
                    .addOnSuccessListener {
                        const.db.collection("Videos").document(it.documents[0].id).update(video)
                            .addOnSuccessListener {
                                onBackPressed()
                                Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show()
                            }
                    }
            }
        }
    }
    private fun getVideoData() {

        const.db.collection("Videos").whereEqualTo("VideoId", videoId).get().addOnSuccessListener {
            val video = it.documents[0].toObject<VideoData>()!!
            binding.VName.setText(video.VideoName)
            binding.VDesc.setText(video.VideoDesc)
            binding.newImage.load(video.VideoImage)

            binding.VUrl.setOnClickListener {
                copyThis(video.VideoUrl)
            }

            newVideoUrl = video.VideoUrl
            VideoImage = video.VideoImage
            VideoFile = video.VideoFile

        }
    }
    fun copyThis(txt:String) {

        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", txt)
        clipboardManager.setPrimaryClip(clipData)

        Toast.makeText(this, "Copied", Toast.LENGTH_SHORT).show()

    }



}

