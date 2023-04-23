package com.example.myapplication

import CustomAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private val stats: Stats = Stats()
    private val colours = listOf(
        Color.RED,
        Color.GREEN,
        Color.BLUE,
        Color.YELLOW,
        Color.MAGENTA
    )


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        stats.load(applicationContext)

        updateDate()

        val data = initData()

        setupCards(data)

        createPieChart(data)

        val swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.refreshLayout)

        swipeRefreshLayout.setOnRefreshListener{

            stats.load(applicationContext)

            val data = initData()

            setupCards(data)

            createPieChart(data)
            swipeRefreshLayout.isRefreshing = false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateDate() {
        val now = LocalDateTime.now()

        val s = "${now.dayOfWeek.name}, ${now.dayOfMonth} ${now.month}"

        val textView = findViewById<TextView>(R.id.textView)
        textView.text = s

    }

    private fun initData(): ArrayList<ItemsViewModel> {
        val data = ArrayList<ItemsViewModel>()

        Stats.activities.forEachIndexed { i, e ->
            data.add(
                ItemsViewModel(
                    R.drawable.ic_launcher_foreground,
                    ((stats.weeklyStats[0][i] / 60).toFloat()),
                    e, colours[i]
                )
            )
        }

        return data
    }

    private fun setupCards(data: ArrayList<ItemsViewModel>) {

        val recyclerview = findViewById<RecyclerView>(R.id.recyclerView)

        val adapterListener = CustomAdapter.OnClickListener { item ->
            val i = Intent(this, StateActivity::class.java)
            val id = Stats.activities.indexOf(item.text)
            i.putExtra("Activity", item.text)
            i.putExtra("Colour", item.colour)
            for (day_num in 0..6)
                i.putExtra("Stats_$day_num", stats.weeklyStats[day_num][id])
            this.startActivity(i)
        }
        val adapter = CustomAdapter(data, adapterListener)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter
        recyclerview.layoutManager = LinearLayoutManager(this)
    }

    private fun createPieChart(data: ArrayList<ItemsViewModel>) {
        val pieChart = findViewById<PieChart>(R.id.pieChart)

        val pieEntries = ArrayList<PieEntry>()
        val usedColours = ArrayList<Int>()
        for(i in 0 until Stats.activities.size) {
            if (data[i].time > 0) {
                pieEntries.add(PieEntry(data[i].time, data[i].text))
                usedColours.add(colours[i])
            }
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.colors = usedColours
        dataSet.valueTextSize = 14f
        //dataSet.valueFormatter = PercentFormatter(pieChart)
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val hours = (value / 60).toInt()
                val minutes = (value % 60).toInt()
                return "${hours}h ${minutes}m"
            }
        }


        val pieData = PieData(dataSet)

        pieChart.setUsePercentValues(true)
        pieChart.data = pieData
        pieChart.description.isEnabled = false
        pieChart.setUsePercentValues(false)
        pieChart.isDrawHoleEnabled = false
        pieChart.isHighlightPerTapEnabled = false
        pieChart.animateY(1400, Easing.EaseInOutQuad)
        //pieChart.setEntryLabelColor(Color.WHITE)
        //pieChart.setEntryLabelTextSize(14f)
        pieChart.setEntryLabelTypeface(Typeface.DEFAULT_BOLD)

        pieChart.legend.isEnabled = false


        if (pieEntries.size == 0)
            pieChart.data = PieData(PieDataSet(listOf(PieEntry(1f, "No Today's Data")), ""))

        pieChart.invalidate()


    }
}