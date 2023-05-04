package com.example.dailymedicalinf.Doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailymedicalinf.Classes.*
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivityAdviceContentBinding
import com.example.dailymedicalinf.databinding.ActivityAdvicesUsersBinding
import com.google.firebase.firestore.ktx.toObjects

class AdvicesUsers : AppCompatActivity() {

    private lateinit var binding: ActivityAdvicesUsersBinding
    lateinit var const: Constans

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdvicesUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        const = Constans(this)

        val adviceId = intent.getStringExtra("AdviceId111").toString()
        Log.e("AdviceImage", "$adviceId")

        getUsers(adviceId)

        binding.Msg2Group.setOnClickListener {
            val i = Intent(this, ChattingActivity::class.java)
            i.putExtra("ReceiverEmail",adviceId)
            startActivity(i)
        }
    }

    private fun getUsers(adviceId: String) {

        val allUsers = ArrayList<Uuser>()

        const.db.collection("Advices").whereEqualTo("adviceId", adviceId).get()
            .addOnSuccessListener {
                val obj = it.toObjects<AdviceData>()
                obj[0].patientIDs.forEach { UEmail ->

                    const.db.collection("Users").whereEqualTo("Email", UEmail).get()
                        .addOnSuccessListener { usr ->
                            val ob = usr.toObjects<UserData>()[0]
                            allUsers.add(Uuser(ob.Email, ob.Image, ob.Name))

                            binding.adviceUsersRec.apply {
                                adapter = UsersChattedAdapter(this@AdvicesUsers, allUsers)
                                layoutManager = LinearLayoutManager(this@AdvicesUsers)
                            }
                        }
                }
            }
    }
}
