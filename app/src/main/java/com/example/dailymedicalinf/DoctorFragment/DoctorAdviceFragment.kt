package com.example.dailymedicalinf.DoctorFragment
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dailymedicalinf.Classes.*
import com.example.dailymedicalinf.Doctor.add_AdviceActivity
import com.example.dailymedicalinf.Doctor.advice_contentActivity
import com.example.dailymedicalinf.databinding.FragmentDoctorAdviceBinding
import com.example.dailymedicalinf.databinding.ItemAdvicesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class DoctorAdviceFragment : Fragment() {
    private lateinit var binding: FragmentDoctorAdviceBinding
    private var myAdapter: FirestoreRecyclerAdapter<AdviceData, ViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorAdviceBinding.inflate(inflater,container,false )
        return binding.root
    }
    override fun onViewCreated(view : View , savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onStart() {
        super.onStart()

        getAllAdvices()
        binding.add.setOnClickListener {
             val intent = Intent(context , add_AdviceActivity::class.java)
              startActivity(intent)
             //val intent = Intent (this@DoctorAdviceFragment, add_AdviceActivity::class.java)
            // getActivity()?.startActivity(intent)
            //val intent = Intent(requireActivity() , add_AdviceActivity::class.java)
            //        startActivity(intent)
            //        requireActivity().finish()
           // startActivity(intent)

        }

        myAdapter!!.startListening()
    }

    private fun getAllAdvices() {
        val const = Constans(requireActivity())

        val query = const.db.collection("Advices")
                .whereEqualTo("doctorEmail", const.auth.currentUser!!.email)
        Log.e("sss", const.auth.currentUser!!.email.toString())
        val option =
                FirestoreRecyclerOptions.Builder<AdviceData>().setQuery(query, AdviceData::class.java)
                        .build()
        myAdapter = object : FirestoreRecyclerAdapter<AdviceData, ViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val binding = ItemAdvicesBinding.inflate(LayoutInflater.from(context), parent, false)
                return ViewHolder(binding)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: AdviceData) {

                holder.binding.name.text = model.AdviceName.toString()
                holder.binding.num4videos.text = model.NumberOfVideos.toString()
                holder.binding.num4patient.text = model.NumberOfpatients.toString()

                if (model.adviceImage.isNotEmpty()) {
                     holder.binding.image.load(model.adviceImage)
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(requireContext(), advice_contentActivity::class.java)

                    Log.e("AdviceImage", model.adviceImage)
                    i.putExtra("AdviceId111", model.adviceId)
                    i.putExtra("name" , model.AdviceName)
                    i.putExtra("image11" , model.adviceImage)
                    startActivity(i)
                }

            }
        }
        binding.adviceRecycle.apply {
            layoutManager =
                    LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }


    }

    override fun onStop() {
        super.onStop()
        myAdapter!!.stopListening()
    }

    class ViewHolder(val binding: ItemAdvicesBinding) : RecyclerView.ViewHolder(binding.root)

}
