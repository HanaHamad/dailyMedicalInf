package com.example.dailymedicalinf.DoctorFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Classes.MsgClass
import com.example.dailymedicalinf.Classes.Uuser

import com.example.dailymedicalinf.databinding.FragmentDoctorChattingBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class DoctorChattingFragment : Fragment() {

    private lateinit var binding: FragmentDoctorChattingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDoctorChattingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()

        getAllUsers()

    }

    private fun getAllUsers() {

        val const = Constans(requireContext())
        val db = const.rtdb.child("Chats")

        val arra = ArrayList<Uuser>()
        val currentUserEmail = const.auth.currentUser!!.email

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                arra.clear()
                snapshot.children.forEach {

                    val obj = it.getValue(MsgClass::class.java)!!
                    if (obj.receiver == currentUserEmail) {

                        const.db.collection("Users").document(obj.sender).get()
                            .addOnSuccessListener { i ->
                                if (!arra.contains(
                                        Uuser(
                                            obj.sender,
                                            i.get("Image").toString(),
                                            i.get("Name").toString()
                                        )
                                    )
                                )
                                    arra.add(
                                        Uuser(
                                            obj.sender,
                                            i.get("Image").toString(),
                                            i.get("Name").toString()
                                        )
                                    )

                                binding.chattingRecycleDoctor.apply {
                                    adapter = UsersChattedAdapter(requireContext(), arra)
                                    layoutManager = LinearLayoutManager(requireContext())
                                }
                            }
                    } else if (obj.sender == currentUserEmail) {

                        const.db.collection("Users").document(obj.receiver).get()
                            .addOnSuccessListener { i ->
                                if (!arra.contains(
                                        Uuser(
                                            obj.receiver,
                                            i.get("Image").toString(),
                                            i.get("Name").toString()
                                        )
                                    )
                                )
                                    arra.add(
                                        Uuser(
                                            obj.receiver,
                                            i.get("Image").toString(),
                                            i.get("Name").toString()
                                        )
                                    )

                                binding.chattingRecycleDoctor.apply {
                                    adapter = UsersChattedAdapter(requireContext(), arra)
                                    layoutManager = LinearLayoutManager(requireContext())
                                }
                            }

                    } else {
                        const.db.collection("Advices").get().addOnSuccessListener { courses ->
                            courses.forEach { currentCourse ->
                                if (currentUserEmail == currentCourse.getString("doctorEmail")) {
                                    if (!arra.contains(
                                            Uuser(
                                                currentCourse.get("AdviceId").toString(),
                                                currentCourse.get("adviceImage").toString(),
                                                currentCourse.get("AdviceName").toString()
                                            )
                                        )
                                    )
                                        arra.add(
                                            Uuser(
                                                currentCourse.get("AdviceId").toString(),
                                                currentCourse.get("adviceImage").toString(),
                                                currentCourse.get("AdviceName").toString()
                                            )
                                        )

                                    binding.chattingRecycleDoctor.apply {
                                        adapter = UsersChattedAdapter(requireContext(), arra)
                                        layoutManager = LinearLayoutManager(requireContext())
                                    }
                                }
                            }
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
}






