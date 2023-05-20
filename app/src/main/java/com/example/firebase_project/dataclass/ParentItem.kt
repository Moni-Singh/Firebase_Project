package com.example.firebase_project.dataclass


data class ParentItem(
    val title  :String,
    val logo : Int ,
    val mList: List<ChildItem>

    )