package com.example.kehadiran

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class StartActivity : AppCompatActivity() {
    lateinit var btnStart: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        supportActionBar?.hide()

        btnStart = findViewById(R.id.btn_Start)

        btnStart.setOnClickListener {
            val moveIntent = Intent(this@StartActivity, MainActivity::class.java)
            startActivity(moveIntent)
        }
    }
}