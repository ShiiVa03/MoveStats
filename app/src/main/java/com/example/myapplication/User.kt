package com.example.myapplication

import java.util.UUID

data class User(val uniqueId: String? = null, val name: String? = null, val email: String? = null, val height: Int? = null,
                val weight: Float? = null, val WalkingTime: Int? = null, val RunningTime: Int? = null,
                val UpStairsTime: Int? = null, val DownStairsTime: Int? = null, val IdleTime: Int? = null)
