package com.example.dailymedicalinf.patient

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.dailymedicalinf.Classes.AdviceData
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Doctor.advice_contentActivity
import com.example.dailymedicalinf.DoctorFragment.DoctorAdviceFragment
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.FragmentAllAdvicesBinding
import com.example.dailymedicalinf.databinding.FragmentRegesteredAdvicesBinding
import com.example.dailymedicalinf.databinding.ItemAdvicesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


class RegesteredAdvicesFragment : Fragment() {

    private lateinit var binding: FragmentRegesteredAdvicesBinding
    lateinit var const: Constans
    private var myAdapter: FirestoreRecyclerAdapter<AdviceData, DoctorAdviceFragment.ViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegesteredAdvicesBinding.inflate(inflater,container,false )
        return binding.root
    }
    override fun onStart() {
        super.onStart()

        const = Constans(requireContext())
        getAllAdvices()

        myAdapter!!.startListening()

    }
    private fun getAllAdvices() {
        val currentUEmail = const.auth.currentUser!!.email
        val query =
            const.db.collection("Advices").whereArrayContains("patientIDs", currentUEmail!!)

        val option =
            FirestoreRecyclerOptions.Builder<AdviceData>().setQuery(query, AdviceData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<AdviceData, DoctorAdviceFragment.ViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAdviceFragment.ViewHolder {
                val binding =
                    ItemAdvicesBinding.inflate(LayoutInflater.from(context), parent, false)
                return DoctorAdviceFragment.ViewHolder(binding)
            }
            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder: DoctorAdviceFragment.ViewHolder, position: Int, model: AdviceData) {
                holder.binding.name.text = model.AdviceName.toString()
                holder.binding.num4videos.text = model.NumberOfVideos.toString()
                holder.binding.num4patient.text = model.NumberOfpatients.toString()

                if (model.adviceImage.isNotEmpty()) {
                    holder.binding.image.load(model.adviceImage)
                }

                holder.itemView.setOnClickListener {
                    val i = Intent(requireContext(), advice_contentActivity::class.java)
                    i.putExtra("AdviceId111", model.adviceId)
                    i.putExtra("name" , model.AdviceName)
                    i.putExtra("image11" , model.adviceImage)
                    startActivity(i)
                }

            }
        }
        binding.regesterdRecycle.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }
    }

}