package com.example.chronostimetracker
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

    class TextOnImageView(context: Context, attrs: AttributeSet) : AppCompatImageView(context, attrs) {
        private val paint = Paint()

        init {
            paint.textSize = 40f
            paint.color = 0xFFFFFFFF.toInt() // White color
            // Set the background color to white
            setBackgroundColor(0xFFFFFFFF.toInt())
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            val text = "Add image"
            val bounds = Rect()
            paint.getTextBounds(text, 0, text.length, bounds)
            // Position the text at the top center of the ImageView
            val x = width / 2f
            val y = bounds.height().toFloat() // Adjusted to position at the top
            canvas.drawText(text, x, y, paint)
        }
}