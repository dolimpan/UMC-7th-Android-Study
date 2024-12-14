package com.example.sl
import android.app.Application
import android.util.Log
import com.kakao.sdk.common.KakaoSdk


class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Log.d("hi","씨3발")
        KakaoSdk.init(this, "a6ade9bf9b58bf1515c64f7a767e4953")
    }
}
