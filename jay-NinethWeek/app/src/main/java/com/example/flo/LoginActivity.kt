package com.example.flo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.room.Room
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_table"
        ).build()

        val loginButton = findViewById<Button>(R.id.login)
        val idEditText1 = findViewById<EditText>(R.id.email_field)
        val idEditText2 = findViewById<EditText>(R.id.email_field_2)
        val passwordEditText = findViewById<EditText>(R.id.password_field)

        val authService = getRetrofit().create(AuthRetrofitInterface::class.java)

        loginButton.setOnClickListener {
            val id = (idEditText1.text.toString() + '@' + idEditText2.text.toString())
            val password = passwordEditText.text.toString()
            authService.signIn(UserLogin(id,password)).enqueue(object: Callback<LoginResponse>{
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>
                ) {
                    Log.e("err", "오류1")
                    val resp : LoginResponse? = response.body()
                    if (resp != null) {
                        if(resp.isSuccess) {
                            Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        } else {

                        }
                    }
                    else
                    {
                        Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                        Log.e("err", "오류2")
                        Log.e("err", "Error Body: ${response.errorBody()?.string()}")

                    }

                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("err", "오류")
                }

            })

            /*
            CoroutineScope(Dispatchers.IO).launch {

                Log.e("help", "${id} ${password}")

                val allUsers = db.UserDao().getAllUsers()
                for (user in allUsers) {
                    Log.e("help","User: id=${user.id}, password=${user.password}")
                }

                val user = db.UserDao().login(id, password)
                if (user != null) {
                    // 로그인 성공 - JWT 생성 및 저장
                    val token = JwtUtils.generateToken(user.id)
                    saveToken(token)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()
                        // 이동할 화면 설정
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "로그인 실패", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun saveToken(token: String) {
        val sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("jwt_token", token)
            apply()
        }*/
        }
    }
}
