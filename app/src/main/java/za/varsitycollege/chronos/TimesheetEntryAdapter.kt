package za.varsitycollege.chronos

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TimesheetEntryAdapter(private val entries: List<TimesheetData>) : RecyclerView.Adapter<TimesheetEntryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val uniqueIdTextView: TextView = itemView.findViewById(R.id.uniqueIdTextView)
        val projectNameTextView: TextView = itemView.findViewById(R.id.projectNameTextView)
        val categoryTextView: TextView = itemView.findViewById(R.id.CategoryTextView)
        // Initialize other TextViews if needed
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
        // Set other TextViews
    }

    override fun getItemCount() = entries.size
}
