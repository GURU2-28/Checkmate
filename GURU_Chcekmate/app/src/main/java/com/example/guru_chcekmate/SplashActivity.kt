package com.example.guru_chcekmate

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val time : Long = 3000 //3초간 스플래시 화면을 보여줌 (ms)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        this.supportActionBar?.hide()
        CoroutineScope(Dispatchers.IO).launch {
            delay(time)
            val intent = Intent(applicationContext, Title::class.java)
            startActivity(intent)
            finish()
        }
    }

}

