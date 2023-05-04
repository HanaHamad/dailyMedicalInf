package com.example.dailymedicalinf.DoctorFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.dailymedicalinf.Classes.AdviceData
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Doctor.edit_adviceActivity
import com.example.dailymedicalinf.Doctor.viewVideoEdit
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.FragmentDoctorEditBinding
import com.example.dailymedicalinf.databinding.ItemAdvicesBinding

import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class DoctorEditFragment : Fragment() {

    private lateinit var binding: FragmentDoctorEditBinding
    private var myAdapter: FirestoreRecyclerAdapter<AdviceData, ViewHolder>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorEditBinding.inflate(inflater,container,false )
        return binding.root
    }
    override fun onViewCreated(view : View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        getAllAdvices()

        myAdapter!!.startListening()
    }

    private fun getAllAdvices() {
     val const = Constans(requireActivity())

     val query =
       const.db.collection("Advices").whereEqualTo("doctorEmail", const.auth.currentUser!!.email)
        val option =
          FirestoreRecyclerOptions.Builder<AdviceData>().setQuery(query, AdviceData::class.java)
           .build()
        myAdapter = object : FirestoreRecyclerAdapter<AdviceData,ViewHolder>(option) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
         val binding = ItemAdvicesBinding.inflate(LayoutInflater.from(context), parent, false)
           return ViewHolder(binding)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: ViewHolder, position: Int, model: AdviceData) {

         holder.binding.name.text = model.AdviceName.toString()
         holder.binding.num4videos.text = model.NumberOfVideos.toString()
         holder.binding.num4patient.text = model.NumberOfpatients.toString()
           if (model.adviceImage.isNotEmpty()) {
            holder.binding.image.load(model.adviceImage)
           }
         holder.binding.editBtn.visibility = View.VISIBLE


         holder.binding.editBtn.setOnClickListener {
          val pop = PopupMenu(this@DoctorEditFragment.requireContext(), holder.binding.editBtn)
           pop.menuInflater.inflate(R.menu.editadvicemenu, pop.menu)
           pop.setOnMenuItemClickListener { x ->
            when (x.itemId) {

             R.id.EditAdvice -> {
              val i = Intent(requireContext(), edit_adviceActivity::class.java)
              i.putExtra("Advice123", model.adviceId)
                 i.putExtra("name123" , model.AdviceName)
                 i.putExtra("image123" , model.adviceImage)
                 Log.e("sasasa","adviceId")
                     startActivity(i)
            }

            R.id.DeleteAdvice -> {

             val dialog = AlertDialog.Builder(this@DoctorEditFragment.requireContext())
             dialog.apply { setTitle("warning")
             setMessage("Will delete all videos ")
             setPositiveButton("Ok") { _, _ ->
             const.db.collection("Advices").whereEqualTo("AdviceId", model.adviceId).get()
              .addOnSuccessListener {
             const.db.collection("AdviceId").document(it.documents[0].id).delete()
              .addOnSuccessListener {
             const.db.collection("Videos").whereEqualTo("AdviceId", model.adviceId).get()
              .addOnSuccessListener { videos ->
                 if (videos.size() > 0) {
                   for (i in 0 until videos.size()) {
                     const.db.collection("Videos")
                     .document(videos.documents[i].id)
                     .delete()
                     .addOnSuccessListener {
                        Toast.makeText(this@DoctorEditFragment.requireContext(), "$i deleted", Toast.LENGTH_SHORT).show()
                          }

                   }
                 }
              }
             }
           }
         }
               setNegativeButton("Cancel") { d, _ ->
                   d.cancel()
               }
                   create().show()
                }

             }

             R.id.HideAdvice -> {

              }
            }
                true
          }
            pop.show()
         }

         holder.itemView.setOnClickListener {
         val i = Intent(requireContext(), viewVideoEdit::class.java)
             Log.e("AdviceImage", model.adviceImage)
             i.putExtra("AdviceId111", model.adviceId)
             i.putExtra("name" , model.AdviceName)
             i.putExtra("image11" , model.adviceImage)
             startActivity(i)
                }
            }
        }
        binding.editRecycle.apply {
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

