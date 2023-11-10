package com.example.kehadiran

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import com.google.firebase.database.*
import java.io.BufferedReader
import java.io.InputStreamReader

class AwardActivity : AppCompatActivity() {
    lateinit var btnHome: ImageButton
    lateinit var btnTable: ImageButton
    private lateinit var resultTextView: TextView
    private lateinit var resultTextView2: TextView
    private lateinit var DescTextView: TextView
    private lateinit var database: FirebaseDatabase
    private lateinit var namesRef: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_award)
        supportActionBar?.hide()

        btnHome = findViewById(R.id.Ib_Home2)
        btnHome.setOnClickListener {
            val moveIntent = Intent(this@AwardActivity, MainActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
        btnTable = findViewById(R.id.Ib_Table2)
        btnTable.setOnClickListener {
            val moveIntent = Intent(this@AwardActivity, TableActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
        resultTextView = findViewById(R.id.resultTextView)
        resultTextView2 = findViewById(R.id.resultTextView2)
        DescTextView = findViewById(R.id.tv_desc1)
        database = FirebaseDatabase.getInstance()
        namesRef = database.getReference("1c27wQexbj78D4NFKKGyyJosZGeHWAgbdlJe2MChQ4zY/Sheet1")

        fetchDataFromFirebase()
        fetchMostRecentName()

    }

    private fun fetchDataFromFirebase() {
        namesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val names = mutableListOf<String>()

                for (snapshot in dataSnapshot.children) {
                    val nameMap = snapshot.value as HashMap<*, *>
                    val name =
                        nameMap["Name"] as String?
                    name?.let {
                        names.add(it)
                    }
                }

                val nameCounts = names.groupingBy { it }.eachCount()
                val maxDuplicateCount = nameCounts.values.maxOrNull()

                if (maxDuplicateCount != null && maxDuplicateCount > 1) {
                    val mostDuplicateNames =
                        nameCounts.filterValues { it == maxDuplicateCount }.keys
                    val resultText =
                        " $mostDuplicateNames"
                    resultTextView.text = resultText
                    val descText =
                        "Dengan total kehadiran paling banyak"
                    DescTextView.text = descText
                } else {
                    resultTextView.text = "Tidak ada nama dengan duplikat."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultTextView.text = "Error: ${error.message}"
            }
        })
    }
    private fun fetchMostRecentName() {
        namesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val namesOnMostRecentDate = mutableSetOf<String>()
                var mostRecentDate = ""

                for (snapshot in dataSnapshot.children) {
                    val nameMap = snapshot.value as HashMap<*, *>
                    val name = nameMap["Name"] as String?
                    val date = nameMap["DateTime"] as String?

                    if (name != null && date != null) {
                        if (date > mostRecentDate) {
                            mostRecentDate = date
                            namesOnMostRecentDate.clear()
                        }
                        if (date == mostRecentDate) {
                            namesOnMostRecentDate.add(name)
                        }
                    }
                }

                val totalNamesOnMostRecentDate = namesOnMostRecentDate.size

                if (totalNamesOnMostRecentDate > 0) {
                    val resultText = "Jumlah yang hadir hari ini \n$totalNamesOnMostRecentDate"
                    resultTextView2.text = resultText
                } else {
                    resultTextView2.text = "Tidak ada nama yang hadir pada tanggal terbaru."
                }
            }

            override fun onCancelled(error: DatabaseError) {
                resultTextView.text = "Error: ${error.message}"
            }
        })
    }
}