package com.example.dailymedicalinf.Classes

import android.provider.Settings.Global.getString
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailymedicalinf.R
import com.example.dailymedicalinf.databinding.ActivityAdviceContentBinding
import com.example.dailymedicalinf.databinding.FragmentDoctorAdviceBinding

class ViewHolder{


    class ContentViewHolder( val binding: ActivityAdviceContentBinding) : RecyclerView.ViewHolder(binding.root) {
        var name = binding.AdviceName.findViewById<TextView>(R.id.VideoName)!!
        var image = binding.AdviceImage.findViewById<ImageView>(R.id.image)!!
    }
    class viewVideoEdit( val binding: ActivityAdviceContentBinding) : RecyclerView.ViewHolder(binding.root) {
        var name = binding.AdviceName.findViewById<TextView>(R.id.AdviceName)!!
        var image = binding.AdviceImage.findViewById<ImageView>(R.id.AdviceImage)!!
    }

    class advicefragment( val binding: FragmentDoctorAdviceBinding) : RecyclerView.ViewHolder(binding.root) {

    }


}