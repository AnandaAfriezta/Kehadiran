package com.example.kehadiran

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TableActivity : AppCompatActivity() {
    lateinit var btnHome: ImageButton
    private lateinit var btnAward: ImageButton
    private lateinit var tableLayout: TableLayout
    private lateinit var nextButton: Button
    private lateinit var previousButton: Button
    private val itemsPerPage = 10
    private var startIndex = 0
    private var totalData = 0
    private lateinit var databaseReference: DatabaseReference
    private val dataSnapshotList = ArrayList<DataSnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        supportActionBar?.hide()

        btnHome = findViewById(R.id.Ib_Home3)
        btnHome.setOnClickListener {
            val moveIntent = Intent(this@TableActivity, MainActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
        btnAward = findViewById(R.id.Ib_Award3)
        btnAward.setOnClickListener {
            val moveIntent = Intent(this@TableActivity, AwardActivity::class.java)
            startActivity(moveIntent)
            finish()
        }
        tableLayout = findViewById(R.id.tableLayout)
        nextButton = findViewById(R.id.nextButton)
        previousButton = findViewById(R.id.previousButton)

        databaseReference = FirebaseDatabase.getInstance().reference.child("1c27wQexbj78D4NFKKGyyJosZGeHWAgbdlJe2MChQ4zY/Sheet1")

        nextButton.setOnClickListener {
            startIndex += itemsPerPage
            updateTable()
        }

        previousButton.setOnClickListener {
            startIndex -= itemsPerPage
            updateTable()
        }

        fetchData()
    }
    private fun fetchData() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalData = snapshot.childrenCount.toInt()
                dataSnapshotList.clear()
                for (childSnapshot in snapshot.children) {
                    dataSnapshotList.add(childSnapshot)
                }
                updateTable()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

    private fun updateTable() {
        tableLayout.removeAllViews()

        for (i in startIndex until minOf(startIndex + itemsPerPage, totalData)) {
            val dataSnapshot = dataSnapshotList[i]
            val no = dataSnapshot.child("No").value.toString()
            val name = dataSnapshot.child("Name").value.toString()
            val dateString = dataSnapshot.child("DateTime").value.toString()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateTime = dateFormat.parse(dateString)

            val row = TableRow(this)
            val noTextView = TextView(this)
            val nameTextView = TextView(this)
            val dateTimeTextView = TextView(this)

            val customTypeface = ResourcesCompat.getFont(this, R.font.poppinsmedium)
            noTextView.typeface = customTypeface
            nameTextView.typeface = customTypeface
            dateTimeTextView.typeface = customTypeface

            noTextView.setTextColor(ContextCompat.getColor(this, R.color.main))
            nameTextView.setTextColor(ContextCompat.getColor(this, R.color.main))
            dateTimeTextView.setTextColor(ContextCompat.getColor(this, R.color.main))

            noTextView.text = no
            nameTextView.text = name
            dateTimeTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(dateTime)

            nameTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            noTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
            dateTimeTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

            row.addView(noTextView)
            row.addView(nameTextView)
            row.addView(dateTimeTextView)

            tableLayout.addView(row)
        }

        nextButton.isEnabled = startIndex + itemsPerPage < totalData
        previousButton.isEnabled = startIndex > 0
    }

}
