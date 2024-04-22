package za.varsitycollege.chronos

import android.content.Context
import android.widget.Toast

class WordCountChecker {
    companion object {
        fun isWordCountValid(context: Context, text: String, minWords: Int = 5, maxWords: Int = 15): Boolean {
            val words = text.split("\\s+".toRegex()).filter { it.isNotBlank() }
            val isValid = words.size in minWords..maxWords

            if (!isValid) {
                val message = if (words.size < minWords) {
                    "Word count is too low. Please add more than 5 words ."
                } else {
                    "Word count must be below 15 words."
                }
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            return isValid
        }
    }
}
