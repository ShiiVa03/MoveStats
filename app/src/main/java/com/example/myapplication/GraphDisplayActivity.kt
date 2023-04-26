package com.example.myapplication

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class GraphDisplayActivity:  AppCompatActivity() {
    @SuppressWarnings("unchecked")
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphdisplay)
        val chart = findViewById<LineChart>(R.id.barChart)

        val accs = intent.getSerializableExtra ("accsPlot")as? List<Triple<Float,Float,Float>>


        val arrX: ArrayList<Entry> =
            accs!!.mapIndexed { index, triple ->
                Entry(
                    index.toFloat(),
                    triple.first
                )
            } as ArrayList<Entry>
        val arrY: ArrayList<Entry> =
            accs.mapIndexed { index, triple ->
                Entry(
                    index.toFloat(),
                    triple.second
                )
            } as ArrayList<Entry>
        val arrZ: ArrayList<Entry> =
            accs.mapIndexed { index, triple ->
                Entry(
                    index.toFloat(),
                    triple.third
                )
            } as ArrayList<Entry>


        val dataSet1 = LineDataSet(arrX, "X")
        dataSet1.color = Color.BLUE
        val dataSet2 = LineDataSet(arrY, "Y")
        dataSet2.color = Color.WHITE
        val dataSet3 = LineDataSet(arrZ, "Z")
        dataSet3.color = Color.GRAY

        val dataSets = arrayListOf<ILineDataSet>(dataSet1, dataSet2, dataSet3)
        val lineData = LineData(dataSets)
        chart.data = lineData
        chart.invalidate()


    }
}