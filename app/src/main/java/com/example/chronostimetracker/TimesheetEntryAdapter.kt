package com.example.chronostimetracker

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Timer
import java.util.concurrent.TimeUnit

class TimesheetEntryAdapter(private var entries: List<TimesheetData>) : RecyclerView.Adapter<TimesheetEntryAdapter.ViewHolder>() {
    private lateinit var camera: Camera
    private var timer: Timer? = null
    private var elapsedTime: Long = 0
    private val timerStates = mutableMapOf<Int, Boolean>()
    private val timerStartTimes = mutableMapOf<Int, Long>() // Map to track timer start times
    private var timerHandler: Handler? = null // Declare timerHandler
    private var timerRunnable: Runnable? = null // Declare timerRunnable
    private val categoryTotalTime = mutableMapOf<String, Long>()

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val uniqueIdTextView: TextView = itemView.findViewById(R.id.uniqueIdTextView)
        val projectNameTextView: TextView = itemView.findViewById(R.id.projectNameTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.CategoryTextView)
        val imageView: ImageView = itemView.findViewById(R.id.userImage)
        //val binding = ItemTimesheetEntryCardBinding.bind(itemView)
       // val timerButton: Button = binding.timerButton
        val timerButton: Button = itemView.findViewById(R.id.timerButton)
        val timerTextView: TextView = itemView.findViewById(R.id.timerTextView)
        //val CategoryTotalTime: TextView = itemView.findViewById(R.id.tvCategoryTotalTime)
        val TimesheetDate: TextView = itemView.findViewById(R.id.topTextView)
        // Initialize other TextViews if needed
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newEntries: List<TimesheetData>) {
        this.entries = newEntries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.timesheet_entry_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = entries[position]
        holder.uniqueIdTextView.text = entry.uniqueId.toString() // Assuming uniqueId is a Long or similar
        holder.projectNameTextView.text = entry.projectName
        holder.categoryTextView.text = entry.category

        if (entry.imageData != null) {
            val decodedString = Base64.decode(entry.imageData, Base64.DEFAULT)
            val decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            holder.imageView.setImageBitmap(decodedBitmap)
        } else {
            // Handle the case where imageData is null or empty
        }

        // Retrieve the saved elapsed time from SharedPreferences
        val timesheetSharedPreferences = holder.itemView.context.getSharedPreferences("TimesheetData", Context.MODE_PRIVATE)
        val savedElapsedTime = timesheetSharedPreferences.getLong("elapsedTime_${entry.uniqueId}", 0)
        Log.d("elapsed", "Saved Elapsed Time for unique ID ${entry.uniqueId}: $savedElapsedTime")


        val creationTime = timesheetSharedPreferences.getLong("creationTime_${entry.uniqueId}", 0)

        // Format the creation date
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        val formattedDate = sdf.format(Date(creationTime))

        holder.TimesheetDate.text = formattedDate

        // Format the elapsed time and set it to timerTextView
        val formattedTime = formatElapsedTime(savedElapsedTime)
        holder.timerTextView.text = formattedTime

        // Log the elapsed time for the unique ID

        // Retrieve the total time for the category from CategoryTimes SharedPreferences
        val category = entries[position].category
        val categoryTimesSharedPreferences = holder.itemView.context.getSharedPreferences("CategoryTimes", Context.MODE_PRIVATE)
        val totalTime = categoryTimesSharedPreferences.getLong("totalTime_$category", 0)
        Log.d("test", "Total Time for category $category: $totalTime")
        // Display the total time for the category
       // holder.CategoryTotalTime.text = formatElapsedTime(totalTime)



        Log.d("TimesheetEntryAdapter", "Elapsed time for Category ${entry.category}: $formattedTime")
        Log.d("total", "Total Time for category $category: $totalTime")
        holder.timerButton.setOnClickListener {
            val uniqueId: Long = entry.uniqueId // Use the actual uniqueId for the timesheet entry
            showTimerDialog(holder.itemView.context, uniqueId, position)
        }
    }

    private fun startTimer(position: Int) {
        timerHandler = Handler(Looper.getMainLooper())
        timerRunnable = object : Runnable {
            override fun run() {
                val elapsedTime = System.currentTimeMillis() - timerStartTimes[position]!!
                // Update the data model with the elapsed time
                entries[position].elapsedTime = elapsedTime
                // Notify the adapter that the data has changed
                notifyItemChanged(position)
                timerHandler?.postDelayed(this, 1000)
            }
        }
        timerHandler?.post(timerRunnable!!)
    }

    fun showTimerDialog(context: Context, uniqueId: Long, position: Int) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_timer)

        val timerTextView = dialog.findViewById<TextView>(R.id.timerTextView)
        val stopButton = dialog.findViewById<Button>(R.id.stopButton)

        var startTime: Long = System.currentTimeMillis() // Start the timer immediately
        var isTimerRunning = true // Timer is running by default

        // Start the timer immediately
        val timerHandler = Handler(Looper.getMainLooper())
        val timerRunnable = object : Runnable {
            override fun run() {
                if (isTimerRunning) {
                    val elapsedTime = System.currentTimeMillis() - startTime
                    val formattedTime = formatElapsedTime(elapsedTime)
                    timerTextView.text = formattedTime
                    timerHandler.postDelayed(this, 1000)
                }
            }
        }
        timerHandler.post(timerRunnable)

        stopButton.setOnClickListener {
            isTimerRunning = false // Stop the timer

            // Calculate the elapsed time and update the timerTextView
            val elapsedTime = System.currentTimeMillis() - startTime
            val formattedTime = formatElapsedTime(elapsedTime)
            timerTextView.text = formattedTime

            // Retrieve the existing elapsed time for the timesheet entry
            val timesheetSharedPreferences = context.getSharedPreferences("TimesheetData", Context.MODE_PRIVATE)
            val existingElapsedTime = timesheetSharedPreferences.getLong("elapsedTime_$uniqueId", 0)

            // Add the newly tracked elapsed time to the existing time
            val newTotalElapsedTime = existingElapsedTime + elapsedTime

            // Update the SharedPreferences with the new total elapsed time
            val timesheetEditor = timesheetSharedPreferences.edit()
            timesheetEditor.putLong("elapsedTime_$uniqueId", newTotalElapsedTime)
            timesheetEditor.apply()

            // Update the total time for the category
            val category = entries[position].category
            val currentTotalTime = categoryTotalTime[category] ?: 0
            categoryTotalTime[category] = currentTotalTime + elapsedTime

            // Save the total time for the category
            saveCategoryTotalTimes(context)

            // Close the dialog
            dialog.dismiss()
        }

        // Set a listener to be called when the dialog is dismissed
        dialog.setOnDismissListener {
            // Update the timerTextView in the ViewHolder
            val entry = entries[position]
            val timesheetSharedPreferences = context.getSharedPreferences("TimesheetData", Context.MODE_PRIVATE)
            val savedElapsedTime = timesheetSharedPreferences.getLong("elapsedTime_${entry.category}", 0)
            Log.d("YourTag", "Saved Elapsed Time for category ${entry.category}: $savedElapsedTime")
            val formattedTime = formatElapsedTime(savedElapsedTime)
            notifyItemChanged(position, formattedTime) // Assuming notifyItemChanged is updated to accept the formatted time
        }

        dialog.show()

        // Set dialog position to bottom of the screen
        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        window?.attributes = layoutParams
    }

    private fun saveCategoryTotalTimes(context: Context) {
        // Ensure the CategoryTimes file exists
        ensureCategoryTimesFileExists(context)

        val categorySharedPreferences = context.getSharedPreferences("CategoryTimes", Context.MODE_PRIVATE)
        val editor = categorySharedPreferences.edit()
        categoryTotalTime.forEach { (category, totalTime) ->
            // Generate a unique key for each category
            val uniqueCategoryKey = "totalTime_$category"
            editor.putLong(uniqueCategoryKey, totalTime)
            Log.d("CategoryData", "Saving value for key $uniqueCategoryKey: $totalTime")
        }
        editor.apply()
    }

    private fun ensureCategoryTimesFileExists(context: Context) {
        val sharedPreferences = context.getSharedPreferences("CategoryTimes", Context.MODE_PRIVATE)
        // No need to do anything if the file already exists
    }

    private fun formatElapsedTime(elapsedTime: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(elapsedTime)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    override fun getItemCount() = entries.size
}


