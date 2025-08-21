package com.example.loanmanagement.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // The Android emulator runs in a VM, so localhost (127.0.0.1) is the emulator's own loopback interface.
    // To access the host machine's localhost, you must use the special IP address 10.0.2.2.
    // Make sure your backend server is running on port 5000.
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}
