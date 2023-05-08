package com.example.myapplication

import CustomAdapter
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.navigation.NavigationView
import java.time.LocalDateTime


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val stats: Stats = Stats()
    private val colours = ArrayList<Int>()


    private lateinit var drawerLayout : DrawerLayout
    private lateinit var navView: NavigationView


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)


        val hsv = FloatArray(3)


        // Start with blue color
        hsv[0] = 210f // blue hue

        hsv[1] = 1f // full saturation

        hsv[2] = 1f // full value


        val gradientStep = 1.0f / Stats.activities.size.toFloat()
        for (i in 0 until Stats.activities.size) {
            hsv[2] -= gradientStep // reduce value by gradientStep to darken the color
            colours.add(Color.HSVToColor(hsv))
        }


        stats.load(applicationContext)

        drawerLayout = findViewById(R.id.drawer_layoutMain)
        navView= findViewById(R.id.nav_viewMain)

        navView.setNavigationItemSelectedListener(this)
        val imgbtn : ImageButton = findViewById(R.id.imageButtonMain)
        imgbtn.setOnClickListener {
            drawerLayout.openDrawer(navView)
            return@setOnClickListener
        }

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

        val textView = findViewById<TextView>(R.id.textViewDate)
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

    private fun setupCards(data: ArrayList<ItemsViewModel>){

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
        var colourUses = 0
        for(i in 0 until Stats.activities.size) {
            if (data[i].time > 0) {
                pieEntries.add(PieEntry(data[i].time, data[i].text))
                usedColours.add(colours[colourUses])
                colourUses += 1
            }
        }

        val dataSet = PieDataSet(pieEntries, "")
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE;
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE;
        dataSet.colors = usedColours
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.WHITE
        //dataSet.valueFormatter = PercentFormatter(pieChart)
        dataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val hours = (value / 60).toInt()
                val minutes = (value % 60).toInt()
                if (hours > 0)
                    return "${hours}h ${minutes}m"
                else
                    return "${minutes}m"
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_item1 -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_item2 -> {
                val intent = Intent(this, MainActivityCollection::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_item3 -> {
                // Handle item 3 click
                val intent = Intent(this, ChangeInfoActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_item4 -> {
                // Handle item 3 click
                val intent = Intent(this, LeaderboardActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> return false
        }
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

}