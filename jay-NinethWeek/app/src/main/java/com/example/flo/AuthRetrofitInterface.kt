package com.example.flo

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthRetrofitInterface {
    @POST("/join")
    fun signUp(@Body user:UserTable): Call<AuthResponse>

    @POST("/login")
    fun signIn(@Body user:UserLogin): Call<LoginResponse>
}