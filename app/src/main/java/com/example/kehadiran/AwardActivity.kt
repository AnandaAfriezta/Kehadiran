package com.example.kehadiran

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import java.io.BufferedReader
import java.io.InputStreamReader

class AwardActivity : AppCompatActivity() {
    lateinit var btnHome: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_award)
        supportActionBar?.hide()

        btnHome = findViewById(R.id.Ib_Home2)
        btnHome.setOnClickListener {
            val moveIntent = Intent(this@AwardActivity, MainActivity::class.java)
            finish()
        }

    }
}