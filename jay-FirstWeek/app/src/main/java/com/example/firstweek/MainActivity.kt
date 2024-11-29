package com.example.firstweek

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.firstweek.ui.theme.FirstWeekTheme
import android.widget.ImageButton
import android.content.Intent


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val imageButton1: ImageButton = findViewById(R.id.imageButton1)
        imageButton1.setOnClickListener {
            val intent = Intent(this, PostPage::class.java)
            startActivity(intent)
        }
        val imageButton2: ImageButton = findViewById(R.id.imageButton2)
        imageButton2.setOnClickListener {
            val intent = Intent(this, PostPage::class.java)
            startActivity(intent)
        }
        val imageButton3: ImageButton = findViewById(R.id.imageButton3)
        imageButton3.setOnClickListener {
            val intent = Intent(this, PostPage::class.java)
            startActivity(intent)
        }
        val imageButton4: ImageButton = findViewById(R.id.imageButton4)
        imageButton4.setOnClickListener {
            val intent = Intent(this, PostPage::class.java)
            startActivity(intent)
        }
        val imageButton5: ImageButton = findViewById(R.id.imageButton5)
        imageButton5.setOnClickListener {
            val intent = Intent(this, PostPage::class.java)
            startActivity(intent)
        }

    }
}