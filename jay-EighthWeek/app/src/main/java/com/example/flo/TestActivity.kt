package com.example.flo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TestActivity : AppCompatActivity() {
    private lateinit var button1: Button
    private lateinit var button2: Button
    private lateinit var db: AppDatabase // RoomDatabase 인스턴스

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test)

        button1 = findViewById(R.id.insert)
        button2 = findViewById(R.id.see)
        button1.setOnClickListener {
        }

        button2.setOnClickListener {
        }
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "song_database"
        ).build()
    }

}
