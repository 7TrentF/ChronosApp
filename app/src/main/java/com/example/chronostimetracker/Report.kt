package com.example.chronostimetracker

import android.content.Context
import android.os.Bundle
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.TimeUnit
import android.graphics.Color
import android.graphics.Typeface
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF

class Report : AppCompatActivity() {
    lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_report)


        val toolbar: Toolbar = findViewById(R.id.my_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Reports" // Set the title here

        pieChart = findViewById(R.id.pieChart)
        // Configure the pie chart
        pieChart.setUsePercentValues(true)
        pieChart.getDescription().setEnabled(false)
        pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
        pieChart.setDragDecelerationFrictionCoef(0.95f)
        pieChart.setDrawHoleEnabled(true)
        pieChart.setHoleColor(Color.WHITE)
        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)
        pieChart.setHoleRadius(58f)
        pieChart.setTransparentCircleRadius(61f)
        pieChart.setDrawCenterText(true)
        pieChart.setRotationAngle(0f)
        pieChart.setRotationEnabled(true)
        pieChart.setHighlightPerTapEnabled(true)
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        pieChart.legend.isEnabled = false
        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)


        // Retrieve total times for each category from CategoryTimes SharedPreferences
        val categoryTimesSharedPreferences = getSharedPreferences("CategoryTimes", Context.MODE_PRIVATE)
        val allCategoryTimes = categoryTimesSharedPreferences.all
        val categories = mutableMapOf<String, Long>()

        for ((key, value) in allCategoryTimes) {
            val category = key.removePrefix("totalTime_").trim()
            // Safely cast value to Long and handle null
            categories[category] = value as? Long?: 0L
        }

// Create PieEntries based on the total times for each category
        val entries: ArrayList<PieEntry> = ArrayList()
        for ((category, totalTime) in categories) {
            entries.add(PieEntry(totalTime.toFloat(), category)) // Use the total time as the value
        }



// Create TextViews for each category and its total time
        val categoryContainer = findViewById<FrameLayout>(R.id.category_container)
        var topPadding = 30 // Initial top padding

        for ((category, totalTime) in categories) {
            // Convert milliseconds to hours, minutes, and seconds
            val hours = TimeUnit.MILLISECONDS.toHours(totalTime)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(totalTime) % 60
            val seconds = TimeUnit.MILLISECONDS.toSeconds(totalTime) % 60

            // Format the time as a string in HH:mm:ss format
            val formattedTime = String.format("%02d:%02d:%02d", hours, minutes, seconds)

            // Create a TextView for the category and its total time
            val textView = TextView(this).apply {
                // Combine category name and formatted time with a space in between
                text = "$category $formattedTime" // Display category name followed by the formatted time
                setTextColor(Color.WHITE) // Set the text color to white
                textSize = 16f // Set the text size
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // Use MATCH_PARENT for width
                    ViewGroup.LayoutParams.WRAP_CONTENT // Adjust height as needed
                ).apply {
                    marginStart = 30 // Add some margin to the start
                    topPadding = topPadding // Use the dynamic top padding
                }
            }
            textView.setPadding(0, topPadding, 0, 0) // Apply padding to the top
            categoryContainer.addView(textView)
            topPadding += 40 // Increase the top padding for the next TextView
        }

        // Assign colors to each category
        val colors: ArrayList<Int> = ArrayList()
        val predefinedColors = listOf(
            resources.getColor(R.color.blue),
            resources.getColor(R.color.yellow),
            resources.getColor(R.color.red),
            resources.getColor(R.color.purple_200)

        )
        var colorIndex = predefinedColors.size
        for ((_, _) in categories) { // Iterate over categories to assign colors
            if (colorIndex >= predefinedColors.size) {
                colorIndex = 0
            }
            colors.add(predefinedColors[colorIndex])
            colorIndex++
        }

        // Update the pie chart with the new entries
        val dataSet = PieDataSet(entries, "Categories")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f
        dataSet.colors = colors // Assign the colors to the dataset

        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(15f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.highlightValues(null)
        pieChart.invalidate()


        val projectCheckbox = findViewById<CheckBox>(R.id.projectCheckbox)
        val categoryCheckbox = findViewById<CheckBox>(R.id.categoryCheckbox)

        projectCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Handle project selection
            } else {
                // Handle unselection of project
            }
        }

        categoryCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {





            } else {
                // Handle unselection of category
            }
        }


    }




}