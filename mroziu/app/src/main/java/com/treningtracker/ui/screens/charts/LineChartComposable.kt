package com.treningtracker.ui.screens.charts

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LineChartComposable(
    entries: List<Entry>,
    label: String,
    unit: String,
    dates: List<Long>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setBackgroundColor(surfaceColor)

                // Configure X-axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                    granularity = 1f
                    textColor = onSurfaceColor
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            return if (value.toInt() < dates.size) {
                                dateFormat.format(Date(dates[value.toInt()]))
                            } else ""
                        }
                    }
                }

                // Configure Y-axis
                axisLeft.apply {
                    textColor = onSurfaceColor
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                }
                axisRight.isEnabled = false

                // Configure legend
                legend.apply {
                    textColor = onSurfaceColor
                    textSize = 12f
                }
            }
        },
        update = { chart ->
            if (entries.isNotEmpty()) {
                val dataSet = LineDataSet(entries, "$label ($unit)").apply {
                    color = primaryColor
                    setCircleColor(primaryColor)
                    lineWidth = 2f
                    circleRadius = 4f
                    setDrawCircleHole(false)
                    valueTextSize = 10f
                    valueTextColor = onSurfaceColor
                    setDrawValues(true)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    cubicIntensity = 0.2f
                }

                chart.data = LineData(dataSet)
                chart.invalidate()
            } else {
                chart.clear()
            }
        }
    )
}

@Composable
fun MultiLineChartComposable(
    dataSets: List<Pair<List<Entry>, String>>,
    unit: String,
    dates: List<Long>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val secondaryColor = MaterialTheme.colorScheme.secondary.toArgb()
    val tertiaryColor = MaterialTheme.colorScheme.tertiary.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val surfaceColor = MaterialTheme.colorScheme.surface.toArgb()

    val colors = listOf(primaryColor, secondaryColor, tertiaryColor)

    AndroidView(
        modifier = modifier
            .fillMaxWidth()
            .height(350.dp),
        factory = { context ->
            LineChart(context).apply {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setBackgroundColor(surfaceColor)

                // Configure X-axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(true)
                    granularity = 1f
                    textColor = onSurfaceColor
                    valueFormatter = object : ValueFormatter() {
                        private val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            return if (value.toInt() < dates.size) {
                                dateFormat.format(Date(dates[value.toInt()]))
                            } else ""
                        }
                    }
                }

                // Configure Y-axis
                axisLeft.apply {
                    textColor = onSurfaceColor
                    setDrawGridLines(true)
                    gridColor = Color.LTGRAY
                }
                axisRight.isEnabled = false

                // Configure legend
                legend.apply {
                    textColor = onSurfaceColor
                    textSize = 12f
                }
            }
        },
        update = { chart ->
            if (dataSets.isNotEmpty() && dataSets.any { it.first.isNotEmpty() }) {
                val lineDataSets = dataSets.mapIndexed { index, (entries, label) ->
                    LineDataSet(entries, "$label ($unit)").apply {
                        color = colors[index % colors.size]
                        setCircleColor(colors[index % colors.size])
                        lineWidth = 2f
                        circleRadius = 3f
                        setDrawCircleHole(false)
                        valueTextSize = 9f
                        valueTextColor = onSurfaceColor
                        setDrawValues(false) // Disable values for cleaner multi-line chart
                        mode = LineDataSet.Mode.CUBIC_BEZIER
                        cubicIntensity = 0.2f
                    }
                }.filter { it.entryCount > 0 }

                if (lineDataSets.isNotEmpty()) {
                    chart.data = LineData(lineDataSets)
                    chart.invalidate()
                }
            } else {
                chart.clear()
            }
        }
    )
}