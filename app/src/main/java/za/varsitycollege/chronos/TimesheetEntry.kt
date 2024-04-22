package za.varsitycollege.chronos

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TimesheetEntry : AppCompatActivity() {

    private lateinit var startDatePicker: DatePicker
    private lateinit var endDatePicker: DatePicker
    private lateinit var startTimePicker: TimePickerHandler
    private lateinit var EndTimePicker : TimePickerHandler
    private lateinit var minHours: EditText
    private lateinit var maxHours: EditText
    private lateinit var etProjectName: EditText
    private lateinit var etCategory: EditText
    private lateinit var etDescription: EditText
    private lateinit var uniqueId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_timesheet_entry)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Create button
        val btnCreate: Button = findViewById(R.id.btnCreate)
        //Start and end time buttons
        val startTimeButton: Button = findViewById(R.id.btnStartTime)
        val EndTimeButton: Button = findViewById(R.id.btnEndTime)
        // Start and End date buttons
        val startDateButton: Button = findViewById(R.id.btnStartDate)
        val endDateButton: Button = findViewById(R.id.btnEndDate)

        etProjectName = findViewById(R.id.etProjectName)
        etCategory = findViewById(R.id.etCategory)
        etDescription = findViewById(R.id.etDescription)

        // Initialize TimePickers for buttons
        startTimePicker = TimePickerHandler(this, startTimeButton)
        EndTimePicker = TimePickerHandler(this, EndTimeButton)

        // Initialize DatePickers for buttons
        startDatePicker = DatePicker(this, startDateButton)
        endDatePicker = DatePicker(this, endDateButton)

        //Min and Max hours
        maxHours = findViewById(R.id.etMax)
        minHours = findViewById(R.id.etMin)

        // Set current date as default text for buttons
        val currentDate = getCurrentDate()
        startDateButton.text = currentDate
        endDateButton.text = currentDate

        val hoursValidator = HoursValidator(this)

        btnCreate.setOnClickListener {
            // Assuming you have methods to get the start and end times as LocalTime objects
            val startTime = startTimePicker.getTimeAsLocalTime()
            val endTime = EndTimePicker.getTimeAsLocalTime()

            // Validate start and end times
            startTimePicker.validateStartEndTime(startTime, endTime)




// Validate minHours and maxHours before proceeding
            if (hoursValidator.validateMinMaxHours(
                    minHours.text.toString(),
                    maxHours.text.toString()))
            {
                saveDataToSharedPreferences()
                // Start TimesheetEntryDisplayActivity and pass the unique ID
                val intent = Intent(this, ListOfEntries::class.java)
                intent.putExtra("uniqueId", uniqueId)
                startActivity(intent)
            }

        }
    }
        private fun saveDataToSharedPreferences() {
            // Use "Projects" as the SharedPreferences name
            val sharedPreferences = getSharedPreferences("Projects", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()


            // Generate a unique ID using the current timestamp
            uniqueId = System.currentTimeMillis().toString()

            // Save other data to "TimesheetData" SharedPreferences if needed
            val timesheetSharedPreferences = getSharedPreferences("TimesheetData", Context.MODE_PRIVATE)
            val timesheetEditor = timesheetSharedPreferences.edit()
            // Use the unique ID to create a unique key for each entry
            timesheetEditor.putString("projectName_$uniqueId", etProjectName.text.toString())
            timesheetEditor.putString("category_$uniqueId", etCategory.text.toString())
            timesheetEditor.putString("description_$uniqueId", etDescription.text.toString())
            timesheetEditor.putString("startTime_$uniqueId", startTimePicker.getTime())
            timesheetEditor.putString("startDate_$uniqueId", startDatePicker.getDate())
            timesheetEditor.putString("endTime_$uniqueId", EndTimePicker.getTime())
            timesheetEditor.putString("endDate_$uniqueId", endDatePicker.getDate())
            timesheetEditor.putInt("minHours_$uniqueId", minHours.text.toString().toInt())
            timesheetEditor.putInt("maxHours_$uniqueId", maxHours.text.toString().toInt())
            timesheetEditor.apply()
            editor.apply()
            // Log all the data being saved
            Log.d("TimesheetEntry", "Saving project name to SharedPreferences with ID: $uniqueId")
            Log.d("TimesheetEntry", "Saving data to SharedPreferences with ID: $uniqueId")
            Log.d("TimesheetEntry", "Project Name: " + etProjectName.text.toString())
            Log.d("TimesheetEntry", "Category: " + etCategory.text.toString())
            Log.d("TimesheetEntry", "Description: " + etDescription.text.toString())
            Log.d("TimesheetEntry", "Start Time: " + startTimePicker.getTime())
            Log.d("TimesheetEntry", "Start Date: " + startDatePicker.getDate())
            Log.d("TimesheetEntry", "End Time: " + EndTimePicker.getTime())
            Log.d("TimesheetEntry", "End Date: " + endDatePicker.getDate())
            Log.d("TimesheetEntry", "Min Hours: " + minHours.text.toString())
            Log.d("TimesheetEntry", "Max Hours: " + maxHours.text.toString())
            Log.d("TimesheetEntry", "Data saved successfully")

        }


    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Month is 0-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Format the date as "MM/dd"
        val dateFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    fun openStartTimePicker(view: View){
        startTimePicker.showTimePickerDialog()
    }

    fun openEndTimePicker(view: View){
        EndTimePicker.showTimePickerDialog()
    }

    fun openStartDatePicker(view: View) {
        startDatePicker.showDatePicker()
    }

    fun openEndDatePicker(view: View) {
        endDatePicker.showDatePicker()
    }

}