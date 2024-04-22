package za.varsitycollege.chronos

data class TimesheetData(
    val uniqueId: Long,
    val projectName: String,
    val category: String,
    val description: String,
    val startTime: String,
    val startDate: String,
    val endTime: String,
    val endDate: String,
    val minHours: Int,
    val maxHours: Int
)
