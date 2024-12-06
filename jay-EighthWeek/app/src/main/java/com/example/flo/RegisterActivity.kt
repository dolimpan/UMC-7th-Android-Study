package com.example.flo

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_table"
        ).build()

        val registerButton = findViewById<Button>(R.id.submit)
        val idEditText1 = findViewById<EditText>(R.id.email_field)
        val idEditText2 = findViewById<EditText>(R.id.email_field_2)
        val passwordEditText = findViewById<EditText>(R.id.password_field)
        val passwordEditText2 = findViewById<EditText>(R.id.password_field_2)

        registerButton.setOnClickListener {
            if(idEditText1.text.toString() == "" || idEditText2.text.toString() == "" || passwordEditText.text.toString() == ""||passwordEditText2.text.toString() == "") {
                Toast.makeText(this@RegisterActivity, "빈 영역이 존재합니다", Toast.LENGTH_SHORT).show()
            }
            else{

            if(passwordEditText.text.toString() != passwordEditText2.text.toString())
            {
                Toast.makeText(this@RegisterActivity, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val id = idEditText1.text.toString() + '@' + idEditText2.text.toString()
                val password = passwordEditText.text.toString()

                CoroutineScope(Dispatchers.IO).launch {
                    Log.e("help", "${id} ${password}")
                    db.UserDao().insertUser(UserTable(id, password))
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
        }
    }
}
