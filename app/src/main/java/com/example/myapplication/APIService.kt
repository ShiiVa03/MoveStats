package com.example.myapplication

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface APIService {

    @JvmSuppressWildcards
    @POST("predict")
    fun predict(@Body postData: List<List<CollectedStats>>): Call<List<Int>>
}