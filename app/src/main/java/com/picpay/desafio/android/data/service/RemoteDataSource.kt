package com.picpay.desafio.android.data.service

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    private val url = "http://careers.picpay.com/tests/mobdev/"

    private val gson: Gson by lazy { GsonBuilder().create() }

    val service: PicPayService by lazy {
        retrofit.create(PicPayService::class.java)

    }

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}