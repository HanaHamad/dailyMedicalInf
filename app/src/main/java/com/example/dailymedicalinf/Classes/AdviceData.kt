package com.example.dailymedicalinf.Classes

data class AdviceData (var adviceId: String = "",
                       var AdviceName: String = "",
                       var adviceImage: String = "",
                       var NumberOfVideos:Long = 0,
                       var NumberOfpatients:Long = 0,
                       var patientIDs:List<String> = listOf())
