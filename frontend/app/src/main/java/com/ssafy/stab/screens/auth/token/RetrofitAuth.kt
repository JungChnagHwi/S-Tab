package com.ssafy.stab.screens.auth.token


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAuth {

    private const val BASE_URL = "https://s-tab.online/"
    private var retrofit: Retrofit? = null

    fun getToken(): Retrofit{
        if (retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit!!
    }
}