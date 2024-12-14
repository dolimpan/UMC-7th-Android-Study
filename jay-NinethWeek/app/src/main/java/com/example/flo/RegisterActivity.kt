package com.example.flo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Call

class RegisterActivity : AppCompatActivity(), SignUpView {

    private lateinit var db: AppDatabase
    private lateinit var idEditText1 : EditText
    private lateinit var idEditText2 : EditText
    private lateinit var passwordEditText : EditText
    private lateinit var passwordEditText2 : EditText
    private lateinit var nameEditText : EditText
    private lateinit var alert : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_table"
        ).build()

        val registerButton = findViewById<Button>(R.id.submit)
        idEditText1 = findViewById<EditText>(R.id.email_field)
        idEditText2 = findViewById<EditText>(R.id.email_field_2)
        passwordEditText = findViewById<EditText>(R.id.password_field)
        passwordEditText2 = findViewById<EditText>(R.id.password_field_2)
        nameEditText = findViewById<EditText>(R.id.name_field)
        alert = findViewById<TextView>(R.id.alert)

        registerButton.setOnClickListener {
            signUp()
        }

    }

    private fun getUser(): UserTable
    {
        val email: String = idEditText1.text.toString() + '@' + idEditText2.text.toString()
        val password: String = passwordEditText2.text.toString()
        val name: String = nameEditText.text.toString()
        Log.e("aa","$email $password $name")
        return UserTable(name, email, password)
    }
    private fun signUp() {
        val authService = AuthService()
        authService.setSignUpView(this)
        authService.signUp(getUser())
    }

    override fun onSignUpSuccess() {
        finish()
    }

    override fun onSignUpFail() {
    }


}
