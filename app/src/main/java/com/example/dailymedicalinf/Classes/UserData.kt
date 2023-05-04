package com.example.dailymedicalinf.Classes

data class UserData (

    var Email: String = "",
    var Name: String = "",
    var Image: String = "",
    var Phone: String = "",
    var Password: String = "",
    var Birthdate: String = "",
    var Kind: String = "",
    var advice:HashMap<String,HashMap<String,*>> = hashMapOf()

)
