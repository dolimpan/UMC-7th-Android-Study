package com.example.sl

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.bumptech.glide.Glide


class MainActivity : AppCompatActivity() {


    lateinit var profile : ImageView
    lateinit var text : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)


        val kakaoLoginButton: Button = findViewById(R.id.kakao_login_button)
        val kakaoLogoutButton: Button = findViewById(R.id.kakao_logout_button)
        profile = findViewById(R.id.profile)
        text = findViewById(R.id.text)
        initalLoad()
        kakaoLoginButton.setOnClickListener {
            login()
        }

        kakaoLogoutButton.setOnClickListener {
            logout(this)
        }
    }

    private fun initalLoad()
    {
        profile.setImageResource(R.drawable.person)
        text.text = "not logined"
    }

    private val mCallback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
        if (error != null) {
            Log.e("ㅁ", "로그인 실패 $error")
            dataLoad()
        } else if (token != null) {
            Log.e("ㅁ", "로그인 성공 ${token.accessToken}")
            dataLoad()
        }
    }

    private fun login(){
        UserApiClient.instance.loginWithKakaoAccount(this, callback = mCallback) // 카카오 이메일 로그인
    }

    private fun dataLoad()
    {
        UserApiClient.instance.me { user, error ->
        if (error != null) {
            Log.e("TAG", "사용자 정보 요청 실패 $error")
        } else if (user != null) {
            Log.e("TAG", "사용자 정보 요청 성공 : $user")
            text.text = user.kakaoAccount?.profile?.nickname
            Glide.with(this)
                .load(user.kakaoAccount?.profile?.profileImageUrl) // 오류 시 표시할 이미지
                .into(profile)
        }
    }}

    private fun logout(context: Context) {
        UserApiClient.instance.logout { error ->

            if (error != null) {
                Toast.makeText(context, "로그아웃 실패: ${error?.message}", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
            }
        }
        initalLoad()
}
}
