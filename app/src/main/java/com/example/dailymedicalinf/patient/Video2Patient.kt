package com.example.dailymedicalinf.patient

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dailymedicalinf.Classes.AdviceData
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Classes.VideoData
import com.example.dailymedicalinf.Doctor.ShowVideo
import com.example.dailymedicalinf.Doctor.advice_contentActivity
import com.example.dailymedicalinf.DoctorFragment.DoctorAdviceFragment
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.*
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.storage.FirebaseStorage

class Video2Patient : AppCompatActivity() {

    private lateinit var binding: ActivityVideo2PatientBinding
    lateinit var const: Constans

    var adviceId = ""
    var VideoId = ""
    var regstered = false
    private var myAdapter: FirestoreRecyclerAdapter<VideoData, ViewHolder>? =
        null
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideo2PatientBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adviceId = intent.getStringExtra("AdviceId111").toString()

        const = Constans(this)

        const = Constans(this)

        checkJoin()
        getAllDataOf()

        resultLauncher =
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
                            uploadFile(
                                it.toString(),
                                const.auth.currentUser!!.email.toString(),
                                VideoId,
                                adviceId
                            )
                            Toast.makeText(this, "UploadDone", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        binding.joinBtn.setOnClickListener {
            const.db.collection("Advices").whereEqualTo("adviceId", adviceId)
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val user = const.auth.currentUser
                        val email = user!!.email
                        var exist = false
                        val dataRes = document.get("patientIDs").toString()
                            .substring(1, (document.get("patientIDs").toString().length - 1))
                        var ar = dataRes.split(",").map { it.trim() }
                        for (e in ar) {
                            if (e == email) {
                                exist = true
                            }
                        }
                        if (!exist) {
                            val next = arrayListOf<String>()
                            next.add(email.toString())
                            for (e in ar) {
                                if (e.isNotEmpty()) {
                                    next.add(e)
                                }
                            }
                            ar = next
                            const.db.collection("Advices").document(document.id)
                                .update(
                                    "patientIDs",
                                    ar
                                )
                            const.db.collection("Advices").document(document.id)
                                .update(
                                    "NumberOfpatients",
                                    (document.get("NumberOfpatients").toString().toLong() + 1)
                                )

                            const.db.collection("Users").document(email.toString())
                                .get().addOnSuccessListener {
                                    val oldHash: HashMap<String, HashMap<String, *>> =
                                        it.get("Advices") as HashMap<String, HashMap<String, *>>
                                    val newHash: HashMap<String, HashMap<String, *>> = hashMapOf(
                                        adviceId to hashMapOf(
                                            "advice_progress" to 0,
                                            "advice_done" to false
                                        )
                                    )
                                    for (key in oldHash.keys) {
                                        newHash.put(key, oldHash[key]!!)
                                    }
                                    const.db.collection("Users").document(email.toString())
                                        .update(
                                            "Advices",
                                            newHash
                                        )
                                }
                            regstered = true
                            binding.unjoinBtn.visibility = View.VISIBLE
                            binding.joinBtn.visibility = View.INVISIBLE
                        }

                    }
                }
        }
        binding.unjoinBtn.setOnClickListener {
            const.db.collection("Advices").whereEqualTo("adviceId", adviceId)
                .get().addOnSuccessListener { documents ->
                    for (document in documents) {
                        val user = const.auth.currentUser
                        val email = user!!.email
                        val dataRes = document.get("patientIDs").toString()
                            .substring(1, (document.get("patientIDs").toString().length - 1))
                        var ar = dataRes.split(",").map { it.trim() }

                        val next = arrayListOf<String>()
                        for (e in ar) {
                            if (e.isNotEmpty() && e != email) {
                                next.add(e)
                            }
                        }
                        ar = next
                        const.db.collection("Advices").document(document.id)
                            .update(
                                "patientIDs",
                                ar
                            )
                        const.db.collection("Advices").document(document.id)
                            .update(
                                "NumberOfpatients",
                                (document.get("NumberOfpatients").toString().toLong() - 1)
                            )
                        const.db.collection("Users").document(email.toString())
                            .get().addOnSuccessListener {
                                val oldHash: HashMap<String, HashMap<String, *>> =
                                    it.get("Advices") as HashMap<String, HashMap<String, *>>
                                val newHash: HashMap<String, HashMap<String, *>> = hashMapOf()
                                for (key in oldHash.keys) {
                                    if (key != adviceId) {
                                        newHash.put(key, oldHash[key]!!)
                                    }

                                }
                                const.db.collection("Users").document(email.toString())
                                    .update(
                                        "Advices",
                                        newHash
                                    )
                            }
                        regstered = false
                        binding.unjoinBtn.visibility = View.INVISIBLE
                        binding.joinBtn.visibility = View.VISIBLE
                    }
                }
        }
    }

    private fun getAllDataOf() {

        const.db.collection("Advices").whereEqualTo("adviceId", adviceId).get()
            .addOnSuccessListener {
                binding.AdviceName.text = it.documents[0].get("AdviceName").toString()

                if (it.documents[0].get("adviceImage").toString().isNotEmpty()) {
                    binding.AdviceImage.load(it.documents[0].get("adviceImage").toString())
                }
            }

        val query =
            const.db.collection("Videos").whereEqualTo("adviceId", adviceId).orderBy("VideoNumber")
        val option =
            FirestoreRecyclerOptions.Builder<VideoData>().setQuery(query, VideoData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<VideoData, ViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val binding = PatientVideoTempletBinding.inflate(LayoutInflater.from(this@Video2Patient), parent, false)
                return ViewHolder(binding)
            }

                @RequiresApi(Build.VERSION_CODES.M)
                override fun onBindViewHolder(holder:ViewHolder, position: Int, model: VideoData) {

                    holder.binding.name.text = model.VideoName
                    VideoId = model.VideoId

                    if (model.VideoImage.isNotEmpty()) {
                        holder.binding.image.load(model.VideoImage)
                    }

                    holder.binding.desc.setOnClickListener {
                        val dialog = Dialog(this@Video2Patient)
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialog.setCancelable(false)
                        dialog.setContentView(R.layout.description_video_dialog)

                        val viewText = dialog.findViewById<TextView>(R.id.desc_view)

                        val closeButton = dialog.findViewById<Button>(R.id.close_btn)

                        viewText.text = model.VideoDesc

                        closeButton.setOnClickListener {
                            dialog.dismiss()
                        }
                        dialog.show()
                    }

                    holder.itemView.setOnClickListener {
                        if (regstered) {

                            val email = const.auth.currentUser!!.email

                            const.db.collection("Users").document(email.toString())
                                .get().addOnSuccessListener { itt ->
                                    val mmm: HashMap<String, HashMap<String, *>> =
                                        itt.get("Advices") as HashMap<String, HashMap<String, *>>
                                    val m = mmm[adviceId]
//                                if (m != null) {
                                    val progress = m!!["advice_progress"].toString().toLong()

                                    if (model.VideoNumber < progress ||
                                        model.VideoNumber == progress + 1
                                    ) {
                                        const.db.collection("Videos")
                                            .whereEqualTo("VideoId", model.VideoId)
                                            .get().addOnSuccessListener {
                                                val vidNum =
                                                    it.documents[0].get("VideoNumber").toString()
                                                        .toLong()

                                                const.db.collection("Users")
                                                    .document(email.toString())
                                                    .get().addOnSuccessListener { ittt ->
                                                        val oldHash: HashMap<String, HashMap<String, *>> =
                                                            ittt.get("Advices") as HashMap<String, HashMap<String, *>>
                                                        val newHash: HashMap<String, HashMap<String, *>> =
                                                            hashMapOf()
                                                        val nId = oldHash[adviceId]

                                                        if (nId!!["advice_progress"].toString()
                                                                .toLong() < model.VideoNumber.toLong()
                                                        ) {
                                                            for (key in oldHash.keys) {
                                                                if (key == adviceId) {
                                                                    newHash.put(
                                                                        key, hashMapOf(
                                                                            "advice_progress" to vidNum,
                                                                            "advice_done" to false
                                                                        )
                                                                    )
                                                                    continue
                                                                } else {
                                                                    newHash.put(
                                                                        key,
                                                                        oldHash[key]!!
                                                                    )
                                                                }
                                                            }
                                                            const.db.collection("Users")
                                                                .document(email.toString())
                                                                .update(
                                                                    "Advices",
                                                                    newHash
                                                                )
                                                        }
                                                    }
                                            }
                                        val i =
                                            Intent(this@Video2Patient, ShowVideo::class.java)
                                        i.putExtra("VideoUrl", model.VideoUrl)
                                        startActivity(i)
                                    } else {
                                        Toast.makeText(
                                            this@Video2Patient,
                                            "احضر الي قبله يا وجه الدبس",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
//                                } else {
//                                    Toast.makeText(
//                                        this@Video2Student,
//                                        "Not registered yet",
//                                        Toast.LENGTH_SHORT
//                                    )
//                                        .show()
//                                }
                                }

                        } else {
                            Toast.makeText(
                                this@Video2Patient,
                                "not regestered yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    holder.binding.uploadFile.setOnClickListener {
                        if (regstered) {
                            val intent = Intent()
                                .setType("*/*")
                                .setAction(Intent.ACTION_GET_CONTENT)
                            resultLauncher.launch(Intent.createChooser(intent, "Select File"))

                        } else {
                            Toast.makeText(
                                this@Video2Patient,
                                "not regestered yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                    holder.binding.downloadFile.setOnClickListener {
                        if (regstered) {
                            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                requestPermissions(
                                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                    1000
                                )
                            } else {
                                val storageRef =
                                    FirebaseStorage.getInstance().reference.child(model.VideoFile)
                                storageRef.downloadUrl.addOnSuccessListener {
                                    Toast.makeText(
                                        this@Video2Patient,
                                        it.toString(),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    startDownloading(it.toString(), model.VideoFile)
                                }
                            }
                        } else {
                            Toast.makeText(
                                this@Video2Patient,
                                "not regestered yet",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }


                }
            }

        binding.customRecycle.apply {
            layoutManager =
                LinearLayoutManager(this@Video2Patient, LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }
}
    private fun checkJoin() {
        const.db.collection("Advices").whereEqualTo("adviceId", adviceId)
            .get().addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = const.auth.currentUser
                    val email = user!!.email
                    val dataRes = document.get("NumberOfpatients").toString()
                        .substring(1, (document.get("NumberOfpatients").toString().length - 1))
                    val ar = dataRes.split(",").map { it.trim() }
                    for (e in ar) {
                        if (e == email) {
                            regstered = true
                        }
                    }
                    if (!regstered) {
                        binding.joinBtn.visibility = View.VISIBLE
                    } else {
                        binding.unjoinBtn.visibility = View.VISIBLE
                    }
                }
            }
    }

    private fun uploadFile(
        fileUrl: String,
        studentEmail: String,
        videoId: String,
        adviceId1: String
    ) {

        val file = mapOf(
            "FileUrl" to fileUrl,
            "StudentEmail" to studentEmail,
            "VideoId" to videoId,
            "adviceId" to adviceId1,

            )
        const.db.collection("Tasks").add(file).addOnSuccessListener {
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startDownloading(url: String, name: String) {
        Toast.makeText(this, url, Toast.LENGTH_SHORT).show()
        val request = DownloadManager.Request(Uri.parse(url))
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle(name)
            .setDescription("the file is  downloading")
            .allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                name
            )

        val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Click again to download ", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permissions Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onStart() {
        super.onStart()
        myAdapter!!.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }
    class ViewHolder(val binding: PatientVideoTempletBinding) : RecyclerView.ViewHolder(binding.root)


}