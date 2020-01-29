package de.abg.pamf.ui.rudder

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import de.abg.pamf.R
import de.abg.pamf.ui.centergravity.CogData
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.math.round


class RudderFragment : Fragment() {

    private lateinit var rudderViewModel: RudderViewModel

    var entries: List<Entry> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rudder, container, false)

        val chart = root.findViewById(R.id.rudder_canvas) as LineChart

        var entries1: List<Entry> = ArrayList()
        entries1 += Entry(1f,0f)
        entries1 += Entry(2f,16f)
        entries1 += Entry(3f,8f)
        val dataSet1 = LineDataSet(entries1, "Sensor A")
        dataSet1.setColor(Color.BLUE)

        var entries2: List<Entry> = ArrayList()
        entries2 += Entry(1f,0f)
        entries2 += Entry(2f,15f)
        entries2 += Entry(3f,7f)
        val dataSet2 = LineDataSet(entries2, "Sensor B")
        dataSet2.setColor(Color.GREEN)

        var entries3: List<Entry> = ArrayList()
        entries3 += Entry(1f,0f)
        entries3 += Entry(2f,1f)
        entries3 += Entry(3f,1f)
        val dataSet3 = LineDataSet(entries3, "Diff")
        dataSet3.setColor(Color.RED)

        val lineData = LineData(dataSet1, dataSet2, dataSet3)
        chart.data = lineData

        // Gestaltung
        chart.setBorderWidth(2f)

        // X-Achse
        chart.xAxis.axisMinimum = 0f
        chart.xAxis.axisMaximum = 200f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Y-Achse
        chart.axisLeft.axisMinimum = -100f
        chart.axisLeft.axisMaximum = 100f
        chart.axisRight.isEnabled = false

        chart.invalidate() // refresh

        var x = 4f
/*
        Timer().schedule(2000, 100) {
            activity!!.runOnUiThread(
                fun()  {
                    Log.d("CHART", "DRAW")

                    val nearVal = if(x%100 < 50) x%100f else 100-x%100f

                    entries1 += Entry(x,(nearVal + Math.random() * 5f).toFloat())
                    entries2 += Entry(x,(nearVal + Math.random() * 5f).toFloat())
                    entries3 += Entry(x,(entries1.last().y - entries2.last().y)*10)

                    ++x
                    val dataSet1 = LineDataSet(entries1, "Sensor A")
                    dataSet1.setColor(Color.BLUE)
                    dataSet1.setDrawCircles(false)
                    val dataSet2 = LineDataSet(entries2, "Sensor B")
                    dataSet2.setColor(Color.GREEN)
                    dataSet2.setDrawCircles(false)
                    val dataSet3 = LineDataSet(entries3, "Differenz (*10)")
                    dataSet3.setColor(Color.RED)
                    dataSet3.setDrawCircles(false)

                    val lineData = LineData(dataSet1, dataSet2, dataSet3)
                    chart.data = lineData
                    chart.invalidate()
                }
            )
        }*/
        return root
    }
}