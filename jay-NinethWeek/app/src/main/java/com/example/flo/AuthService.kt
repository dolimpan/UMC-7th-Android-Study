package com.example.flo

import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthService {
    private lateinit var singUpView: SignUpView

    fun setSignUpView(signUpView: SignUpView)
    {
        this.singUpView = signUpView
    }
    fun signUp(user : UserTable)
    {
            val authService = getRetrofit().create(AuthRetrofitInterface::class.java)
            authService.signUp(user).enqueue(object: Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    val resp : AuthResponse? = response.body()
                    if(resp?.code == "COMMON200")
                    {
                        singUpView.onSignUpSuccess()
                    }
                    else
                    {
                        singUpView.onSignUpFail()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.d("FAIL!", t.message.toString())
                    Log.d("FAILURE", "StackTrace: ${Log.getStackTraceString(t)}")
                }
            })
            Log.d("a", "HELLO");
    }
}