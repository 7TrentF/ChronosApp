package za.varsitycollege.chronos

import android.content.Context
import android.widget.Toast

class HoursValidator(private val context: Context) {
    fun validateMinMaxHours(minHours: String, maxHours: String): Boolean {
        val minHoursValue = minHours.toIntOrNull() ?: 0
        val maxHoursValue = maxHours.toIntOrNull() ?: 0

        return if (minHoursValue >= maxHoursValue) {
            Toast.makeText(context, "Minimum hours cannot be more than maximum hours.", Toast.LENGTH_SHORT).show()
            false
        } else {
            true
        }
    }
}