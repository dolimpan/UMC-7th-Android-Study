package com.example.fourthweek

import android.os.Bundle
import android.widget.Button
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
import com.example.fourthweek.ui.theme.FourthWeekTheme
import android.widget.TextView
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    private lateinit var timerTextView: TextView
    private lateinit var timerButtonLeft: Button
    private lateinit var timerButtonRight: Button
    private var timerJob: Job? = null
    private var milliSecond : Int = 0
    private var paused : Boolean = true
    private var off : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.timer)
        timerTextView = findViewById(R.id.timer)
        timerButtonLeft = findViewById(R.id.leftButton)
        timerButtonRight = findViewById(R.id.rightButton)
        timerStart()

        timerButtonLeft.setOnClickListener {
            if(paused)
            {
                timerButtonLeft.text = "pause"
                timerResume()
            }
            else
            {
                timerButtonLeft.text = "start"
                timerPause()
            }
        }

        timerButtonRight.setOnClickListener {
            timerStop()
        }
    }

    private fun timerStart()
    {
        timerJob = CoroutineScope(Dispatchers.Default).launch()
        {
            while(isActive)
            {
                if(!paused)
                {
                    milliSecond++
                    withContext(Dispatchers.Main) {
                        timerUpdate()
                    }
                    delay(10L)
                }
            }
        }
    }

    private fun timerUpdate()
    {
        val mSecond = milliSecond % 100
        val second = (milliSecond / 100) % 60
        val minute = (milliSecond / 6000) % 60
        timerTextView.text = String.format("%02d:%02d:%02d", minute, second, mSecond)
    }

    private fun timerPause()
    {
        paused = true

    }

    private fun timerResume()
    {
        paused = false
    }

    private fun timerStop()
    {
        milliSecond = 0
        timerTextView.text = "00:00:00"
        paused = true
        timerStart()
    }

}


