package com.example.myapplication

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.temporal.ChronoUnit


class StateActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_state)
        supportActionBar?.hide()

        val extras = intent.extras
        val textView = findViewById<TextView>(R.id.textViewState)
        textView.text = extras!!.getString("Activity")

        val colour = extras.getInt("Colour")

        // Get the reference to the LineChart view
        val barChart = findViewById<BarChart>(R.id.barChart)

        val xAxis: XAxis = barChart.xAxis
        val leftAxis: YAxis = barChart.axisLeft
        val rightAxis: YAxis = barChart.axisRight

// remove vertical grid lines
        xAxis.setDrawGridLines(false)
        xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
// remove horizontal grid lines
        leftAxis.setDrawGridLines(false)
        rightAxis.setDrawGridLines(false)

// remove axis lines
        xAxis.setDrawAxisLine(false)
        leftAxis.setDrawAxisLine(true)
        rightAxis.setDrawAxisLine(false)


        // Set the value formatter for the y-axis
        leftAxis.labelCount = 5 // set the number of labels on the y-axis
        leftAxis.valueFormatter = object : ValueFormatter() {
           override fun getAxisLabel(value: Float, axis: AxisBase?): String {
               val hours = (value / 60).toInt()
               val minutes = (value % 60).toInt()
               return if (hours > 0)
                   "${hours}h ${minutes}m"
               else
                   "${minutes}m"
            }
        }
        leftAxis.textColor = Color.WHITE
        leftAxis.textSize = 13f


// Create a list of Entry objects for the data
        val entries = ArrayList<BarEntry>()

        val now = LocalDate.now()
        for (i in 0..6) {
            entries.add(BarEntry(
                (6 - i).toFloat(),
                (extras.getInt("Stats_$i") / 60).toFloat()
            ))
        }
        val labels = (6 downTo 0).map { now.minus(it.toLong(), ChronoUnit.DAYS).dayOfWeek.name.take(3) }
// Create a LineDataSet object with the data
        val dataSet = BarDataSet(entries, "Activity time")
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.textColor = Color.WHITE
        xAxis.textSize = 15f

// Create a LineData object with the LineDataSet
        val barData = BarData(dataSet)
        barData.setValueTextColor(Color.WHITE)
        barData.setValueTextSize(13f);

        //val colors = intArrayOf(Color.TRANSPARENT, Color.GREEN)
        //val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors)
        //dataSet.fillDrawable = gradientDrawable
        //dataSet.setDrawFilled(true)
        dataSet.setDrawValues(true)
        dataSet.setGradientColor(Color.TRANSPARENT, colour)

        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val hours = (value / 60).toInt()
                val minutes = (value % 60).toInt()
                return if (hours > 0)
                    "${hours}h ${minutes}m"
                else
                    "${minutes}m"
            }
        }

// Set the LineData to the LineChart
        barChart.data = barData
        barChart.setExtraOffsets(0f, 0f, 0f, 50f)

// Customize the appearance of the LineChart
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.legend.textColor = Color.WHITE

        val legend_entries = barChart.legend.entries
        legend_entries[0].formColor = colour
        barChart.legend.setCustom(legend_entries)


        barChart.setTouchEnabled(true)
        barChart.isDragEnabled = false;
        barChart.setScaleEnabled(false);
        barChart.setPinchZoom(false)
        barChart.animateY(1500)


    }
}
