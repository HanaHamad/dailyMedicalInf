package com.example.dailymedicalinf.patient

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import coil.load
import com.example.dailymedicalinf.Classes.Constans
import com.example.dailymedicalinf.Classes.UserData
import com.example.dailymedicalinf.Doctor.SendEmailActivity
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.FragmentDoctorAdviceBinding
import com.example.dailymedicalinf.databinding.FragmentProfileBinding
import com.google.firebase.firestore.ktx.toObject
import java.util.*

class ProfileFragment : Fragment() , DatePickerDialog.OnDateSetListener{

    private lateinit var binding: FragmentProfileBinding
    lateinit var const: Constans
    private var newImage = ""
    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    const.progressDialog.show()
                    // There are no request codes
                    val intent: Intent? = result.data
                    val uri = intent?.data  //The uri with the location of the file
                    val file = const.getFile(requireContext(), uri!!)
                    val new_uri = Uri.fromFile(file)

                    val reference = const.storage.child("Images/${new_uri.lastPathSegment}")
                    val uploadTask = reference.putFile(new_uri)

                    uploadTask.addOnFailureListener { e ->
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                            const.progressDialog.dismiss()
                            newImage = it.toString()
                           binding.UserImage.load(newImage)
                        }
                    }
                }
            }

      //  return inflater.inflate(R.layout.fragment_profile, container, false)
        binding = FragmentProfileBinding.inflate(inflater,container,false )
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        const = Constans(requireContext())

        getUserInfo()

        binding.UserImage.setOnClickListener {
            val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT)
            resultLauncher.launch(Intent.createChooser(intent, "Select image"))
        }

        binding.birthDate.setOnClickListener {
            val datePikerDialog = DatePickerDialog(
                requireContext(), this,
                2010,
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
            )
            datePikerDialog.show()
        }

        binding.UpdateButton.setOnClickListener {
            updateUserInfo(
                binding.NameEdit.text.toString(),
                binding.birthDate.text.toString(),
                binding.PhoneNumberEdit.text.toString()
            )
        }
        binding.sendBtn.setOnClickListener {
            startActivity(Intent(requireContext(), SendEmailActivity::class.java))
        }

    }
    private fun updateUserInfo(Name: String, Birthdate: String, Phone: String) {
        val email = const.auth.currentUser!!.email
        if (newImage.isNotEmpty()) {
            const.db.collection("Users").document(email.toString())
                .update(
                    "Name", Name,
                    "Image", newImage,
                    "Birthdate", Birthdate,
                    "Phone", Phone,
                ).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                }
        } else {
            const.db.collection("Users").document(email.toString())
                .update(
                    "Name", Name,
                    "Birthdate", Birthdate,
                    "Phone", Phone,
                ).addOnSuccessListener {
                    Toast.makeText(requireContext(), "Updated", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun getUserInfo() {
        val email = const.auth.currentUser!!.email.toString()
        const.db.collection("Users").document(email).get().addOnSuccessListener { document ->
            if (document != null) {
                val userData = document.toObject<UserData>()
                binding.NameEdit.setText(userData!!.Name)
                binding.emailEdit.setText(userData.Email)
                binding.PhoneNumberEdit.setText(userData.Phone)
                binding.birthDate.text = userData.Birthdate
                binding.UserImage.load(userData.Image)
            }
        }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        binding.birthDate.text = "${p2 + 1}/$p3/$p1"
    }


}