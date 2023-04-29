package com.example.myapplication

data class User(val name: String? = null, val email: String? = null, val height: Int? = null,
                val weight: Float? = null, val WalkingTime: Int? = null, val RunningTime: Int? = null,
                val UpStairsTime: Int? = null, val DownStairsTime: Int? = null, val IdleTime: Int? = null)
