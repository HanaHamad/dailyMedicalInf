package com.example.dailymedicalinf.patient

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dailymedicalinf.Classes.AdviceData
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Doctor.advice_contentActivity
import com.example.dailymedicalinf.DoctorFragment.DoctorAdviceFragment
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.FragmentAllAdvicesBinding
import com.example.dailymedicalinf.databinding.FragmentDoctorAdviceBinding
import com.example.dailymedicalinf.databinding.ItemAdvicesBinding
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.toObject


class AllAdvicesFragment : Fragment(), TextWatcher {

    private lateinit var binding: FragmentAllAdvicesBinding
    lateinit var const: Constans
    private var myAdapter: FirestoreRecyclerAdapter<AdviceData, DoctorAdviceFragment.ViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllAdvicesBinding.inflate(inflater,container,false )
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        const = Constans(requireContext())
        getAllAdvices()

        binding.searchTxt.addTextChangedListener(this)

        myAdapter!!.startListening()
    }


    private fun getAllAdvices() {

        val const = Constans(requireActivity())

        val query = const.db.collection("Advices")
        val option =
            FirestoreRecyclerOptions.Builder<AdviceData>().setQuery(query, AdviceData::class.java)
                .build()
        myAdapter = object : FirestoreRecyclerAdapter<AdviceData, DoctorAdviceFragment.ViewHolder>(option) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorAdviceFragment.ViewHolder {
                val binding = ItemAdvicesBinding.inflate(LayoutInflater.from(context), parent, false)
                return DoctorAdviceFragment.ViewHolder(binding)
            }

            @SuppressLint("SetTextI18n")
            override fun onBindViewHolder(holder:DoctorAdviceFragment.ViewHolder, position: Int, model: AdviceData) {

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
        binding.AllAdvicesRecycle.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = myAdapter
        }

    }

    private fun searchFor(text: String) {
        val array = ArrayList<AdviceData>()

        const.db.collection("Advices").get().addOnSuccessListener { ar ->
            ar.forEach { co ->
                val advice = co.toObject<AdviceData>()

                if (advice.AdviceName.contains(text)) {
                    array.add(advice)

                }
            }
        }

        binding.AllAdvicesRecycle.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = adapterr(requireContext(), array)

        }
    }

    private fun gatAll() {
        val array = ArrayList<AdviceData>()
        Thread {
            const.db.collection("Advices").get().addOnSuccessListener { ar ->
                ar.forEach { co ->
                    val advice = co.toObject<AdviceData>()
                    array.add(advice)
                }
            }
        }.start()

        binding.AllAdvicesRecycle.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL, false
            )
            adapter = adapterr(requireContext(), array)
        }
    }

    class adapterr(val context: Context, val List: ArrayList<AdviceData>) :
        RecyclerView.Adapter<adapterr.ViewHolder>() {

        class ViewHolder(val binding: ItemAdvicesBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val binding = ItemAdvicesBinding.inflate(LayoutInflater.from(context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, pos: Int ) {
            holder.binding.name.text = List[pos].AdviceName
            holder.binding.num4videos.text = List[pos].NumberOfVideos.toString()
            holder.binding.num4patient.text = List[pos].NumberOfpatients.toString()

            if (List[pos].adviceImage.isNotEmpty())
                holder.binding.image.load(List[pos].adviceImage)

            holder.itemView.setOnClickListener {
                val i = Intent(context, Video2Patient::class.java)
                i.putExtra("AdviceId111", List[pos].adviceId)

                context.startActivity(i)
            }

        }
        override fun getItemCount(): Int {
            return List.size

        }
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(txt: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (txt!!.isNotEmpty()) {
            searchFor(txt.toString())
        } else {
            gatAll()
        }

    }

    override fun afterTextChanged(p0: Editable?) {

    }

}