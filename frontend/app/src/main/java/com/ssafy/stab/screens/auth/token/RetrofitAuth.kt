package com.ssafy.stab.screens.auth.token


import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitAuth {

    private const val BASE_URL = "https://s-tab.online/"
    private var retrofit: Retrofit? = null

    fun getToken(): Retrofit{
        if (retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
                .build()
        }
        return retrofit!!
    }
}