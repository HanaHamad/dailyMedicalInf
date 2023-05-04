package com.example.dailymedicalinf.Doctor

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Classes.VideoData
import com.example.dailymedicalinf.Classes.ViewHolder
import com.example.dailymedicalinf.DoctorFragment.DoctorAdviceFragment
import com.example.dailymedicalinf.databinding.ActivityAdviceContentBinding
import com.example.dailymedicalinf.databinding.FragmentDoctorAdviceBinding
import com.example.dailymedicalinf.databinding.ItemAdvicesBinding
import com.example.dailymedicalinf.databinding.VideoItemBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class advice_contentActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdviceContentBinding
    lateinit var const: Constans
    private var myAdapter: FirestoreRecyclerAdapter<VideoData, ViewH>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdviceContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        const = Constans(this)

        val adviceId = intent.getStringExtra("AdviceId111").toString()
        val name = intent.getStringExtra("name").toString()
        val image = intent.getStringExtra("image11").toString()

        Log.e("advices","image $image id $adviceId" )


        getAllDataOf(adviceId , name , image)

        binding.addVideoBtn.setOnClickListener {
            val i = Intent(this, AddVideo::class.java)
            i.putExtra("AdviceId", adviceId)
            i.putExtra("nameadd", name)
            i.putExtra("imagevedio", image)
            startActivity(i)
        }

        binding.showPatient.setOnClickListener {
            val i = Intent(this, AdvicesUsers::class.java)
            i.putExtra("AdviceId111", adviceId)
            startActivity(i)
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

    private fun getTest(adviceId: String ){
        const.db.collection("Advices").whereEqualTo("AdviceId", adviceId).get().addOnFailureListener {

        }.addOnSuccessListener {

        }

    }

    private fun getAllDataOf(adviceId: String , name:String , image:String) {

        const.db.collection("Advices").whereEqualTo("adviceId", adviceId).get()
            .addOnSuccessListener {
                Log.e("aasss",it.documents[0].get("AdviceName").toString())
                binding.AdviceName.text = it.documents[0].get("AdviceName").toString()
                Log.e(" message","access")
                Log.e("image","${it.documents[0].get("adviceImage")}")
                if (it.documents[0].get("adviceImage").toString().isNotEmpty()) {
                    binding.AdviceImage.load(it.documents[0].get("adviceImage").toString())
                }
            }.addOnFailureListener {
                    Log.e("error message","AndACustomTag")
                }

        val query = const.db.collection("Videos").whereEqualTo("adviceId", adviceId).orderBy("NumberOfVideos")
        val option =
            FirestoreRecyclerOptions.Builder<VideoData>().setQuery(query, VideoData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<VideoData, ViewH>(option) {
            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ):ViewH {
                val binding = VideoItemBinding.inflate(LayoutInflater.from(this@advice_contentActivity), parent, false)
                return ViewH(binding)
            }

            override fun onBindViewHolder(holder: ViewH, position: Int, model: VideoData) {
                holder.binding.name.text = model.VideoName
                Log.e("vvvvvv" , "${model.toString()}")
                if (model.VideoImage.isNotEmpty()) {
                    holder.binding.image.load(model.VideoImage)
                }
                holder.itemView.setOnClickListener {
                    val i = Intent(this@advice_contentActivity, ShowVideo::class.java)
                    i.putExtra("AdviceId" , model.VideoId)
                    i.putExtra("Video234", model.VideoUrl)
                    i.putExtra("nameadd" , model.VideoName)
                    i.putExtra("imagevedio" , model.VideoImage)
                    startActivity(i)
                }
            }
        }
        binding.customRecycle.apply {
            layoutManager =
                GridLayoutManager(context,2)
            adapter = myAdapter
        }

    }

    class ViewH(val binding: VideoItemBinding) : RecyclerView.ViewHolder(binding.root)
}
