package com.example.chronostimetracker

// Updated version 
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
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
    private lateinit var imgUserImage : ImageView
    private lateinit var btnPickImg :Button
    private var uniqueId: Int = -1 // Initialize uniqueId with a default value
    private lateinit var camera: Camera


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
        val endTimeButton: Button = findViewById(R.id.btnEndTime)
        // Start and End date buttons
        val startDateButton: Button = findViewById(R.id.btnStartDate)
        val endDateButton: Button = findViewById(R.id.btnEndDate)

        etProjectName = findViewById(R.id.etProjectName)
        etCategory = findViewById(R.id.etCategory)
        etDescription = findViewById(R.id.etDescription)

        // Initialize TimePickers for buttons
        startTimePicker = TimePickerHandler(this, startTimeButton)
        EndTimePicker = TimePickerHandler(this, endTimeButton)

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
                // SaveCategory()
                saveDataToSharedPreferences()
                // Start TimesheetEntryDisplayActivity and pass the unique ID
                val intent = Intent(this, ListOfEntries::class.java)
                intent.putExtra("uniqueId", uniqueId)
                startActivity(intent)
            }
        }

        camera = Camera(this)
        imgUserImage = findViewById(R.id.imgUserImage)
        btnPickImg = findViewById(R.id.btnPickImg)

        btnPickImg.setOnClickListener {
            //camera.openCamera(imgUserImage)
            camera.showImagePickerOptions(imgUserImage)

        }

        imgUserImage.setOnClickListener {
            // Get the current image from the ImageView
            val drawable = imgUserImage.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                // Inflate the dialog layout
                val dialogView = layoutInflater.inflate(R.layout.dialog_image_preview, null)
                val imageView = dialogView.findViewById<ImageView>(R.id.previewImageView)
                imageView.setImageBitmap(bitmap.copy(bitmap.config, true)) // Copy the bitmap without compression
                // Create and show the dialog
                val dialog = AlertDialog.Builder(this)
                    .setView(dialogView)
                    .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
                    .create()
                dialog.show()
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.handlePermissionResult(requestCode, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        camera.handleActivityResult(requestCode, resultCode, data)
    }
    private fun SaveProjectName(){

    }

    private fun SaveCategory(){
        // Save the category to its own SharedPreferences with a unique key
        val categorySharedPreferences = getSharedPreferences("CategoryData", Context.MODE_PRIVATE)
        val categoryEditor = categorySharedPreferences.edit()
        // Generate a unique key for the category
        val uniqueCategoryKey = "category_$uniqueId"
        categoryEditor.putString(uniqueCategoryKey, etCategory.text.toString())
        categoryEditor.apply()


        // Log all saved categories
        val allCategories = categorySharedPreferences.all
        for ((key, value) in allCategories) {
            Log.d("TimesheetEntry", "Category saved: $key = $value")

        }

    }

    private fun saveDataToSharedPreferences() {
        // Use "Projects" as the SharedPreferences name
        val sharedPreferences = getSharedPreferences("Projects", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Retrieve and increment the counter
        uniqueId = sharedPreferences.getInt("lastProjectId", 0) + 1
        editor.putInt("lastProjectId", uniqueId)
        SaveCategory()

        // Save the creation date and time
        val creationTime = System.currentTimeMillis()
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val formattedDate = sdf.format(Date(creationTime))

        // Save other data to "TimesheetData" SharedPreferences if needed
        val timesheetSharedPreferences = getSharedPreferences("TimesheetData", Context.MODE_PRIVATE)
        val timesheetEditor = timesheetSharedPreferences.edit()
        // Convert the ImageView image to a Bitmap
        val bitmap = (imgUserImage.drawable as BitmapDrawable).bitmap

        // Convert the Bitmap to a Base64 string
        val encodedImage = camera.encodeImage(bitmap)

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
        timesheetEditor.putString("Image_$uniqueId", encodedImage)
        timesheetEditor.putLong("creationTime_$uniqueId", creationTime)
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
        Log.d("TimesheetEntry", "Saving imgUserImage to SharedPreferences with ID: $uniqueId")
        Log.d("TimesheetEntry", "Creation Date and Time: $formattedDate")
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