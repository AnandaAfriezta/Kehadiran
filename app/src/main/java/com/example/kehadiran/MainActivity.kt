package com.example.kehadiran

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.db.williamchart.view.BarChartView
import com.db.williamchart.view.HorizontalBarChartView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {
    private lateinit var barChart: BarChartView
    private lateinit var horizontabarchart : HorizontalBarChartView
    lateinit var btnAward: ImageButton
    lateinit var btnTable: ImageButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        horizontabarchart = findViewById(R.id.verticalbarchart)
        btnAward = findViewById(R.id.Ib_Award)
        btnAward.setOnClickListener{
            val moveIntent = Intent(this@MainActivity, AwardActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
        btnTable = findViewById(R.id.Ib_Table)
        btnTable.setOnClickListener {
            val moveIntent = Intent(this@MainActivity, TableActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
        // Mengambil referensi ke tabel kehadiran
        val database = FirebaseDatabase.getInstance()
        val kehadiranRef = database.getReference("1c27wQexbj78D4NFKKGyyJosZGeHWAgbdlJe2MChQ4zY/Sheet1")


        kehadiranRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nameCounts = mutableMapOf<String, Int>()
                // Loop melalui semua kehadiran
                for (kehadiranSnapshot in snapshot.children) {
                    val name = kehadiranSnapshot.child("Name").getValue(String::class.java)
                    // Tambahkan jumlah pembelian untuk name tertentu
                    if (name != null) {
                        if (nameCounts.containsKey(name)) {
                            nameCounts[name] = nameCounts[name]!! + 1
                        } else {
                            nameCounts[name] = 1
                        }
                    }
                }


                val entries = mutableListOf<Pair<String, Float>>()
                for ((name, count) in nameCounts) {
                    entries.add(name to count.toFloat())
                }
                //barChart.animate(entries)
                horizontabarchart.animate(entries)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan jika ada
                val message = databaseError.message
            }
        })
    }
}