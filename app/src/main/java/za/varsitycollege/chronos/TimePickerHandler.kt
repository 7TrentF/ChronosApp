package za.varsitycollege.chronos

import android.app.TimePickerDialog
import android.content.Context
import android.widget.Button
import android.widget.Toast
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
class TimePickerHandler (private val context: Context, private val startTimeButton: Button) {

    private var hour = 0
    private var minute = 0

    fun showTimePickerDialog() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
            hour = selectedHour
            minute = selectedMinute
            startTimeButton.text = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        }

        TimePickerDialog(context, timeSetListener, hour, minute, true).apply {
            setTitle("Select Time")
            show()
        }
    }

    fun getTimeAsLocalTime(): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
        return LocalTime.parse(getTime(), formatter)
    }

    fun validateStartEndTime(startTime: LocalTime, endTime: LocalTime) {
        if (!startTime.isBefore(endTime)) {
            Toast.makeText(context, "Start time cannot be more than end time", Toast.LENGTH_SHORT).show()
        }
    }

    fun getTime(): String {
        return String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
    }

}