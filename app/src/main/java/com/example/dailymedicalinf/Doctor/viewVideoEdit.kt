package com.example.dailymedicalinf.Doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Classes.VideoData
import com.example.dailymedicalinf.Classes.ViewHolder
import com.example.dailymedicalinf.DoctorFragment.DoctorAdviceFragment
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivityAdviceContentBinding
import com.example.dailymedicalinf.databinding.ActivityViewVideoEditBinding
import com.example.dailymedicalinf.databinding.ItemAdvicesBinding
import com.example.dailymedicalinf.databinding.VideoItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class viewVideoEdit : AppCompatActivity() {

    private lateinit var binding: ActivityViewVideoEditBinding
    private var myAdapter: FirestoreRecyclerAdapter<VideoData,ViewHolder>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewVideoEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adviceId = intent.getStringExtra("AdviceId111")!!
        val namme = intent.getStringExtra("name")!!
        val image = intent.getStringExtra("image11")!!

        getAllDataOf(adviceId)
    }
    private fun getAllDataOf(adviceId: String) {

        val const = Constans(this)

        const.db.collection("Advices").whereEqualTo("adviceId", adviceId).get()
            .addOnSuccessListener {
                binding.AdviceName.text = it.documents[0].get("AdviceName").toString()

                if (it.documents[0].get("adviceImage").toString().isNotEmpty()) {
                    binding.AdviceImage.load(it.documents[0].get("adviceImage").toString())
                }
            }


        val query = const.db.collection("Videos").whereEqualTo("AdviceId", adviceId).orderBy("VideoNumber")
        val option =
            FirestoreRecyclerOptions.Builder<VideoData>().setQuery(query, VideoData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<VideoData, ViewHolder>(option) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): ViewHolder {
                val binding = VideoItemBinding.inflate(LayoutInflater.from(this@viewVideoEdit), parent, false)
                return ViewHolder(binding)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: VideoData) {
                holder.binding.name.text = model.VideoName
                holder.binding.DeleteVideo.visibility = View.VISIBLE
                holder.binding.image.setOnClickListener {
                    const.db.collection("Videos").whereEqualTo("VideoId", model.VideoId).get()
                        .addOnSuccessListener {

                            val d = AlertDialog.Builder(this@viewVideoEdit)
                            d.setTitle("Delete Video")
                            d.setMessage(" do you wanna delete this Video !?!")
                            d.setPositiveButton("Delete") { _, _ ->
                                Toast.makeText(this@viewVideoEdit, "Deleted", Toast.LENGTH_SHORT)
                                    .show()
                                const.db.collection("Videos").document(it.documents[0].id).delete()
                                const.db.collection("Advices").whereEqualTo("AdviceId", adviceId)
                                    .get().addOnSuccessListener { documents ->
                                        for (document in documents) {
                                            const.db.collection("Advices").document(document.id)
                                                .update("NumberOfVideos",
                                                    (document.get("NumberOfVideos").toString()
                                                        .toLong() - 1)
                                                )
                                        }
//                                        getAllDataOf(courseId) // for refresh
                                    }
                            }.setNegativeButton("cancel") { _d, _ ->
                                _d.dismiss()
                            }.create().show()

                        }
                }

                if (model.VideoImage.isNotEmpty()) {
                    holder.binding.image.load(model.VideoImage)
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(this@viewVideoEdit, editVideo::class.java)
                    i.putExtra("VideoId", model.VideoId)
                    startActivity(i)
                }


            }
        }
        binding.editRecycle.apply {
            layoutManager =
                GridLayoutManager(this@viewVideoEdit, 2)
            adapter = myAdapter
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
    class ViewHolder(val binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root)

}