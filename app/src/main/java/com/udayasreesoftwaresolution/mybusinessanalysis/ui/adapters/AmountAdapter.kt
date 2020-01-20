package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.text.NumberFormat
import com.github.mikephil.charting.utils.ColorTemplate


@SuppressLint("SetTextI18n")
class AmountAdapter(val context : Context, val amountModelList : ArrayList<AmountModel>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private val CHART_VIEW = 1
    private val AMOUNT_VIEW = 2

    override fun getItemViewType(position: Int): Int {
        when(position) {
            0 -> {
                return CHART_VIEW
            }
            else -> {
                return AMOUNT_VIEW
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when(viewType) {
            CHART_VIEW -> {
                return ChartHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_home_chart, parent, false))
            }
            AMOUNT_VIEW -> {
                return HomeHolder(LayoutInflater.from(this.context).inflate(R.layout.adapter_home, parent, false))
            }
        }
        return null!!
    }

    override fun getItemCount(): Int {
        return amountModelList.size ?: 0
    }

    override fun onBindViewHolder(holders: RecyclerView.ViewHolder, position: Int) {
        val model = amountModelList[position]
        when(holders.itemViewType) {
            CHART_VIEW -> {
                val holder = ChartHolder(holders.itemView)

                setupPieChart(holder.chartView)
            }
            AMOUNT_VIEW -> {
                val holder = HomeHolder(holders.itemView)

                with(model) {
                    holder.homeTitle.text = title.toUpperCase()
                    holder.homeTotal.text = "₹ ${NumberFormat.getNumberInstance(
                        ConfigurationCompat.getLocales(context.resources.configuration)[0]).format(total)}"
                    if (title == "Expenses Amount") {
                        holder.homeTitle.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                        //holder.homeTotal.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                    }
                }
            }
        }
    }

    inner class HomeHolder(view : View) : RecyclerView.ViewHolder(view) {
        val homeTitle = view.findViewById<TextView>(R.id.row_home_title_id)
        val homeTotal = view.findViewById<TextView>(R.id.row_home_total_id)
        init {
            val kendaltype = AppUtils.getTypeFace(context, ConstantUtils.KENDALTYPE)
            homeTitle.typeface = kendaltype
            homeTotal.typeface = kendaltype
        }
    }

    inner class ChartHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chartView = view.findViewById<PieChart>(R.id.row_home_chart_id)
        init {
            //horBarChart.layoutParams.height = (AppUtils.SCREEN_HEIGHT * 0.8).toInt()
        }
    }

    private fun setupPieChart(pieChartView : PieChart) {
        val calculatePercentage = ArrayList<PieEntry>()

        for (element in amountModelList) {
            if (element.total > 0f) {
                calculatePercentage.add(PieEntry(element.total.toFloat(), element.title))
            }
        }

        val pieDataSet = PieDataSet(calculatePercentage, "")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        pieDataSet.sliceSpace = 3f
        pieDataSet.valueTextSize = 15f

        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(13f)
        pieData.setValueTextColor(Color.DKGRAY)
        pieData.setValueFormatter(PercentFormatter(pieChartView))

        /*val legend = pieChartView.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)*/

        pieChartView.setUsePercentValues(true)
        pieChartView.legend.isEnabled = false
        pieChartView.data = pieData
        pieChartView.description.isEnabled = false
        pieChartView.setExtraOffsets(5f,5f,5f,5f)
        pieChartView.dragDecelerationFrictionCoef = 0.9f
        pieChartView.setDrawCenterText(false)
        pieChartView.isDrawHoleEnabled = false
        pieChartView.setEntryLabelColor(Color.DKGRAY)
        pieChartView.setEntryLabelTextSize(13f)
        pieChartView.animateXY(2000, 2000)
        pieChartView.invalidate()
    }
}