package za.varsitycollege.chronos

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast

class MinMaxFilter(private val context: Context) {

    private val minValue = 1
    private val maxValue = 23

    fun addTextWatcherToEditText(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val number = s.toString().trim()
                if (!isValidNumber(number)) {
                    Toast.makeText(context, "Please enter a number between $minValue and $maxValue", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun isValidNumber(number: String): Boolean {
        return try {
            val value = number.toInt()
            value in minValue..maxValue
        } catch (e: NumberFormatException) {
            false
        }
    }
}