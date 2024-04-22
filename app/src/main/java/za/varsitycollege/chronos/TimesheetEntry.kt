package za.varsitycollege.chronos

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
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

    private lateinit var datePicker: DatePicker
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

        etProjectName = findViewById(R.id.etProjectName)
        etCategory = findViewById(R.id.etCategory)
        etDescription = findViewById(R.id.etDescription)


        //Start and end time buttons
        val startTimeButton:Button = findViewById(R.id.btnStartTime)
        val EndTimeButton :Button= findViewById(R.id.btnEndTime)

        // Initialize TimePickers for buttons
        startTimePicker = TimePickerHandler(this, startTimeButton)
        EndTimePicker = TimePickerHandler(this, EndTimeButton)

        // Start and End date buttons
        val startDateButton: Button = findViewById(R.id.btnStartDate)
        val endDateButton: Button = findViewById(R.id.btnEndDate)

        // Set current date as default text for buttons
        val currentDate = getCurrentDate()
        startDateButton.text = currentDate
        endDateButton.text = currentDate

        // Initialize DatePickers for buttons
        startDatePicker = DatePicker(this, startDateButton)
        endDatePicker = DatePicker(this, endDateButton)

        //Min and Max hours
         maxHours = findViewById(R.id.etMax)
         minHours = findViewById(R.id.etMin)

        val minMaxFilter = MinMaxFilter(this)
       // MinMaxFilter.addTextWatcherToEditText(minHours)
       // MinMaxFilter.addTextWatcherToEditText(maxHours)

        btnCreate.setOnClickListener {
            saveDataToSharedPreferences()
            // Start TimesheetEntryDisplayActivity and pass the unique ID
            val intent = Intent(this, ListOfEntries::class.java)
            intent.putExtra("uniqueId", uniqueId)
            startActivity(intent)
        }

    }

    private fun saveDataToSharedPreferences() {
        val sharedPreferences = getSharedPreferences("TimesheetData", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Generate a unique ID using the current timestamp
         uniqueId = System.currentTimeMillis().toString()

        // Use the unique ID to create a unique key for each entry
        editor.putString("projectName_$uniqueId", etProjectName.text.toString())
        editor.putString("category_$uniqueId", etCategory.text.toString())
        editor.putString("description_$uniqueId", etDescription.text.toString())
        editor.putString("startTime_$uniqueId", startTimePicker.getTime())
        editor.putString("startDate_$uniqueId", startDatePicker.getDate())
        editor.putString("endTime_$uniqueId", EndTimePicker.getTime())
        editor.putString("endDate_$uniqueId", endDatePicker.getDate())
        editor.putInt("minHours_$uniqueId", minHours.text.toString().toInt())
        editor.putInt("maxHours_$uniqueId", maxHours.text.toString().toInt())

        editor.apply()
        Log.d("TimesheetEntry", "Saving data to SharedPreferences with ID: $uniqueId")
        Log.d("TimesheetEntry", "Project Name: " + etProjectName.text.toString())
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