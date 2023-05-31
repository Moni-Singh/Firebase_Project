package com.example.firebase_project.dataclass

data class Register(
    val name:String ="",
    val email:String = "",
    val image: String = "",
    val bioinfo: String = "",
    val gender: String = "",
    val birthday: String = "",
    val education: String = "",
    val role :String = "User"

)
