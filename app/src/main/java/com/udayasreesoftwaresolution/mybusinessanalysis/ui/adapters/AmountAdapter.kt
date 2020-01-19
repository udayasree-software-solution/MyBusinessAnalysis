package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.text.NumberFormat
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate


@SuppressLint("SetTextI18n")
class AmountAdapter(val context : Context, val amountModelList : ArrayList<AmountModel>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {

    private val CHART_VIEW = 1
    private val AMOUNT_VIEW = 2

    override fun getItemViewType(position: Int): Int {
        when(position) {
            -1 -> {
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
            }
            AMOUNT_VIEW -> {
                val holder = HomeHolder(holders.itemView)

                with(model) {
                    holder.homeTitle.text = title.toUpperCase()
                    holder.homeTotal.text = "₹ ${NumberFormat.getNumberInstance(
                        ConfigurationCompat.getLocales(context.resources.configuration)[0]).format(total)}"
                    /*if (title == "Expenses Amount") {
                        holder.homeTitle.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                        holder.homeTotal.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                    }*/
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
        val horBarChart = view.findViewById<HorizontalBarChart>(R.id.row_home_bar_chart_id)
        init {
            //horBarChart.layoutParams.height = (AppUtils.SCREEN_HEIGHT * 0.8).toInt()
        }
    }
}