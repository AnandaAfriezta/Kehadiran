package com.example.kehadiran

import android.app.DatePickerDialog
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
    private lateinit var filterButton: Button
    private lateinit var resetFilter: Button
    private var selectedDate: Date? = null
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
        filterButton = findViewById(R.id.filterButton)
        resetFilter = findViewById(R.id.resetFilterButton)
        resetFilter.setOnClickListener {
            resetFilter()
        }
        filterButton.setOnClickListener {
            showDatePickerDialog()
        }
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
                for (childSnapshot in snapshot.children) {
                    val dateString = childSnapshot.child("DateTime").value.toString()
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                    dateFormat.timeZone = TimeZone.getTimeZone("UTC")
                    val dateTime = dateFormat.parse(dateString)
                    if (isSameDate(dateTime, selectedDate)) {
                        dataSnapshotList.add(childSnapshot)
                    }
                }

                totalData = dataSnapshotList.size
                updateTable()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    private fun resetFilter() {
        selectedDate = null
        startIndex = 0
        dataSnapshotList.clear()
        fetchData()
    }
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth, 0, 0, 0)
                selectedDate = selectedCalendar.time

                // Reset startIndex when filtering by date
                startIndex = 0

                // Clear the dataSnapshotList
                dataSnapshotList.clear()

                // Fetch data from Firebase and filter by date
                fetchData()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun updateTable() {
        tableLayout.removeAllViews()

        for (i in startIndex until minOf(startIndex + itemsPerPage, totalData)) {
            val dataSnapshot = dataSnapshotList[i]
            val no = dataSnapshot.child("No").value.toString()
            val name = dataSnapshot.child("Name").value.toString()
            val dateString = dataSnapshot.child("DateTime").value.toString()
            val status = dataSnapshot.child("Status").value.toString()

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            dateFormat.timeZone = TimeZone.getTimeZone("UTC")
            val dateTime = dateFormat.parse(dateString)

            if (selectedDate == null || isSameDate(dateTime, selectedDate)) {
                val noTextView = TextView(this)
                val row = TableRow(this)
                val nameTextView = TextView(this)
                val dateTimeTextView = TextView(this)
                val statusTextView = TextView(this)

                val customTypeface = ResourcesCompat.getFont(this, R.font.poppinsmedium)
                noTextView.typeface = customTypeface
                nameTextView.typeface = customTypeface
                dateTimeTextView.typeface = customTypeface
                statusTextView.typeface = customTypeface

                noTextView.setTextColor(ContextCompat.getColor(this, R.color.main))
                nameTextView.setTextColor(ContextCompat.getColor(this, R.color.main))
                dateTimeTextView.setTextColor(ContextCompat.getColor(this, R.color.main))
                statusTextView.setTextColor(ContextCompat.getColor(this, R.color.main))

                noTextView.text = no
                nameTextView.text = name
                dateTimeTextView.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(dateTime)
                statusTextView.text = status

                noTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                nameTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                dateTimeTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
                statusTextView.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

                row.addView(noTextView)
                row.addView(nameTextView)
                row.addView(dateTimeTextView)
                row.addView(statusTextView)

                tableLayout.addView(row)
            }
        }

        nextButton.isEnabled = startIndex + itemsPerPage < totalData
        previousButton.isEnabled = startIndex > 0
    }

    private fun isSameDate(date1: Date, date2: Date?): Boolean {
        if (date2 == null) {
            return true
        }
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

}
