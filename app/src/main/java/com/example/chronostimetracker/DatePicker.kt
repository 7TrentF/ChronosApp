package com.example.chronostimetracker

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class DatePicker (private val context: Context, private val dateButton: Button) {

    private lateinit var datePickerDialog: DatePickerDialog
    var selectedDate: String = ""
        private set
    init {
        initDatePicker()
    }

    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val date = makeDateString(dayOfMonth, month + 1, year)
            dateButton.text = date
            selectedDate = date
        }

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        datePickerDialog = DatePickerDialog(context, dateSetListener, year, month, dayOfMonth)
    }


    fun showDatePicker() {
        datePickerDialog.show()
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return "${getMonthFormat(month)} $day $year"
    }

    fun getDate(): String {
        return selectedDate
    }

    fun validateStartEndDates(startDate: Calendar, endDate: Calendar) {
        if (startDate.timeInMillis >= endDate.timeInMillis) {
            Toast.makeText(context, "Start date must be before end date", Toast.LENGTH_LONG).show()
        }
    }


    fun getDateAsCalendar(): Calendar {
        val format = SimpleDateFormat("MMM dd yyyy", Locale.getDefault())
        val dateParsed = format.parse(selectedDate)
        val calendar = Calendar.getInstance()
        calendar.time = dateParsed
        return calendar
    }


    private fun getMonthFormat(month: Int): String {
        return when (month) {
            1 -> "JAN"
            2 -> "FEB"
            3 -> "MAR"
            4 -> "APR"
            5 -> "MAY"
            6 -> "JUN"
            7 -> "JUL"
            8 -> "AUG"
            9 -> "SEP"
            10 -> "OCT"
            11 -> "NOV"
            12 -> "DEC"
            else -> "UNKNOWN"
        }
    }


}
