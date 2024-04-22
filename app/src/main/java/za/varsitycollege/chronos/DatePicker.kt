package za.varsitycollege.chronos

import android.app.DatePickerDialog
import android.content.Context
import android.widget.Button
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale



class DatePicker(private val context: Context, private val dateButton: Button) {

    private lateinit var datePickerDialog: DatePickerDialog
    private var selectedDate: String = ""

    init {
        initDatePicker()
    }

    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate = makeDateString(dayOfMonth, month + 1, year)
            dateButton.text = selectedDate
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

    fun getDate(): String {
        return selectedDate
    }

    private fun makeDateString(day: Int, month: Int, year: Int): String {
        return "${getMonthFormat(month)} $day, $year"
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
