package com.example.chronostimetracker

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.os.Handler
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import java.util.concurrent.TimeUnit
import android.app.DatePickerDialog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class ListOfEntries : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var adapter: TimesheetEntryAdapter
    private lateinit var entries: MutableList<TimesheetData>
    private lateinit var camera: Camera
    private lateinit var timerTextView: TextView
    private var timerHandler: Handler? = null
    private var timerRunnable: Runnable? = null
    private var startTime: Long = 0
    private var isTimerRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Chronos Timesheets"

        populateCategorySpinner()

        val button = findViewById<Button>(R.id.btnNext)
        button.setOnClickListener {
            val intent = Intent(this, TimesheetEntry::class.java)
            startActivity(intent)
        }

        // Initialize RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Retrieve all entries from SharedPreferences
        val sharedPreferences = getSharedPreferences("TimesheetData", MODE_PRIVATE)
        entries = mutableListOf<TimesheetData>()
        val allKeys = sharedPreferences.all.keys
        for (key in allKeys) {
            if (key.startsWith("projectName_")) {
                val uniqueIdString = key.substringAfter("projectName_")
                val uniqueId = uniqueIdString.toLongOrNull()
                    ?: 0 // Convert String to Long, default to 0 if conversion fails
                val projectName =
                    sharedPreferences.getString("projectName_$uniqueIdString", "") ?: ""
                val category = sharedPreferences.getString("category_$uniqueIdString", "") ?: ""
                val description =
                    sharedPreferences.getString("description_$uniqueIdString", "") ?: ""
                val startTime = sharedPreferences.getString("startTime_$uniqueIdString", "") ?: ""
                val startDate = sharedPreferences.getString("startDate_$uniqueIdString", "") ?: ""
                val endTime = sharedPreferences.getString("endTime_$uniqueIdString", "") ?: ""
                val endDate = sharedPreferences.getString("endDate_$uniqueIdString", "") ?: ""
                val minHours = sharedPreferences.getInt("minHours_$uniqueIdString", 0)
                val maxHours = sharedPreferences.getInt("maxHours_$uniqueIdString", 0)
                val userImage = sharedPreferences.getString("Image_$uniqueIdString", "") ?: ""
                val imageData = sharedPreferences.getString("imgUserImage_$uniqueIdString", null)
                if (imageData != null) {
                    val bitmap = camera.decodeBase64ToBitmap(imageData)
                    // Use the bitmap as needed
                }
                entries.add(
                    TimesheetData(
                        uniqueId,
                        projectName,
                        category,
                        description,
                        startTime,
                        startDate,
                        endTime,
                        endDate,
                        minHours,
                        maxHours,
                        userImage
                    )
                )
            }
        }

        // Set up the RecyclerView with the adapter
        adapter = TimesheetEntryAdapter(entries)
        recyclerView.adapter = adapter

        // Initialize the DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navView)

        // Setup the ActionBarDrawerToggle
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )

        //navigationView.inflateMenu(R.menu.mymenus)
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.report -> {
                    // Open Report activity when the Report item is clicked
                    val intent = Intent(this, Report::class.java)
                    startActivity(intent)
                    true
                }
                R.id.login -> {
                    // Open Login activity when the Login item is clicked
                    val intent = Intent(this, Login::class.java)
                    startActivity(intent)
                    true
                }

                R.id.Timesheet -> {
                    // Open Login activity when the Login item is clicked
                    val intent = Intent(this, TimesheetEntry::class.java)
                    startActivity(intent)
                    true
                }

                // Add more cases as needed
                else -> false

            }
        }

        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    private fun populateCategorySpinner() {
        // Get SharedPreferences instance for TimesheetData
        val timesheetSharedPreferences = getSharedPreferences("TimesheetData", Context.MODE_PRIVATE)
        val categorySharedPreferences = getSharedPreferences("CategoryData", Context.MODE_PRIVATE)


        // Retrieve all saved dates
        val allDates = timesheetSharedPreferences.all
        val dateKeys = allDates.keys.filter { it.startsWith("startDate_") || it.startsWith("endDate_") }
        val dateValues = dateKeys.map { timesheetSharedPreferences.getString(it, "")!! }


        // Retrieve all saved categories
        val allCategories = categorySharedPreferences.all
        val categoryNames = allCategories.filterKeys { it.startsWith("category_") }.values.toList()
        val categoryNamesStringList = categoryNames.map { it.toString() }

        // Remove duplicates from the list of category names
        val uniqueCategoryNames = categoryNamesStringList.distinct()


        // Initialize your Spinner
        val spinner: Spinner = findViewById(R.id.categorySpinner)

        // Create a mutable list to hold the category names
        val mutableCategoryNames = uniqueCategoryNames.toMutableList()

        // Add a placeholder item to represent "no selection"
        val placeholder = "Filter by Category"
        mutableCategoryNames.add(0, placeholder)


        // Create a mutable list to hold the filter options
        val mutableFilterOptions = mutableListOf("Filter by Categories", "Filter by Date Range", "None")

        // Pass the mutable list to the ArrayAdapter
        val spinnerArrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, mutableFilterOptions)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerArrayAdapter

        // Set the default selection to the first option without triggering the listener
        spinner.setSelection(0, false)

        // Set the onItemSelectedListener for the Spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedOption = parent.getItemAtPosition(position).toString()
                if (selectedOption == "Filter by Categories") {

                    showCategorySelectionDialog()
                } else if (selectedOption == "Filter by Date Range") {
                    // If "Filter by Date Range" is selected, show two DatePickerDialogs
                    val calendar = Calendar.getInstance()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val day = calendar.get(Calendar.DAY_OF_MONTH)

                    // Show the first DatePickerDialog for the start date
                    DatePickerDialog(this@ListOfEntries, { _, year, monthOfYear, dayOfMonth ->
                        // Store the selected start date
                        val startDate = "$dayOfMonth/${monthOfYear + 1}/$year"

                        // Show the second DatePickerDialog for the end date
                        DatePickerDialog(this@ListOfEntries, { _, year, monthOfYear, dayOfMonth ->
                            // Handle the end date selected by the user
                            val endDate = "$dayOfMonth/${monthOfYear + 1}/$year"
                            filterEntriesByDate(startDate, endDate)
                        }, year, month, day).show()
                    }, year, month, day).show()
                }
                else if (selectedOption == "None") {
                    // Clear all filters and update the UI
                    clearAllFiltersAndUpdateUI()
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Optionally handle the case where no item is selected
            }
        }
    }

    private fun clearAllFiltersAndUpdateUI() {
        // Clear any existing filters
        // For example, if you have a method to clear category filters:
        filterEntriesByCategory("") // Assuming this clears the category filter

        // Assuming you have a method to clear date range filters:
        filterEntriesByDate("", "") // Passing empty strings to clear the date range

        // Update the UI to reflect the cleared filters
        // This might involve resetting the spinner to its initial state or updating the adapter with the original data
        // For example, if you have a method to reset the spinner:
        // resetSpinnerToInitialState()

        // Or, if you're using an adapter to display the data:
        // adapter.updateData(originalDataList) // Assuming 'originalDataList' is the list of all entries before any filtering
    }



    private fun showCategorySelectionDialog() {
        val categories = getSharedPreferences("CategoryData", Context.MODE_PRIVATE)
            .all.filterKeys { it.startsWith("category_") }
            .map { it.value.toString() }
            .distinct()

        val items = categories.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select a Category")
            .setSingleChoiceItems(items, -1) { dialog, which ->
                // The user has selected a category
                filterEntriesByCategory(categories[which])
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    private fun filterEntriesByCategory(selectedCategory: String?) {
        val filteredEntries = if (selectedCategory.isNullOrEmpty()) {
            entries // Display all entries if no category is selected
        } else {
            entries.filter { entry -> entry.category == selectedCategory }
        }
        adapter.updateData(filteredEntries)
    }

    private fun parseCreationTime(creationTime: Long): Date? {
        return try {
            Date(creationTime)
        } catch (e: IllegalArgumentException) {
            null // Return null if the creationTime cannot be converted to a Date
        }
    }

    private fun parseDate(dateString: String): Date? {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            dateFormat.parse(dateString)
        } catch (e: ParseException) {
            null // Return null if the date string cannot be parsed
        }
    }


    private fun filterEntriesByDate(startDate: String, endDate: String) {
        // Convert the selected date strings to Date objects
        val startDateObj = parseDate(startDate)?: return // Return early if the start date cannot be parsed
        val endDateObj = parseDate(endDate)?: return // Return early if the end date cannot be parsed

        // Filter entries by the selected date range
        val filteredEntries = entries.filter { entry ->
            // Convert the entry's creation time to a Date object for comparison
            val creationTime = parseCreationTime(entry.creationTime)?: return@filter false

            // Check if the entry's creation time falls within the selected date range
            creationTime.time >= startDateObj.time && creationTime.time <= endDateObj.time
        }

        adapter.updateData(filteredEntries)
    }




    private fun updateAdapterWithFilteredEntries(filteredEntries: List<TimesheetData>) {
        // Assuming you have a reference to your adapter
        adapter.updateData(filteredEntries)
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}
