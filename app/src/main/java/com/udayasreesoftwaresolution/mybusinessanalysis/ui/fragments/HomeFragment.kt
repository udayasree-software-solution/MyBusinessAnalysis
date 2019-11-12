package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.AmountViewAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountViewModel


private const val ARG_AMOUNTS = "total_amount"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var pieChartView : PieChart
    private lateinit var progressBox : ProgressBox

    companion object {
        fun newInstance(totalAmountList : ArrayList<AmountViewModel>) : HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_AMOUNTS, totalAmountList)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.frag_home_recycler_id)
        pieChartView = view.findViewById(R.id.frag_home_piechart_id)
        progressBox = ProgressBox.create(activity)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(ARG_AMOUNTS)) {
                progressBox.show()
                val totalAmountList  = bundle.getParcelableArrayList<AmountViewModel>(ARG_AMOUNTS)
                if (totalAmountList != null && totalAmountList.isNotEmpty()) {
                    setupPieChart(totalAmountList)
                    val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    val homeAdapter = AmountViewAdapter(activity!!, totalAmountList)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.adapter = homeAdapter
                    homeAdapter.notifyDataSetChanged()
                }
                progressBox.dismiss()
            }
        }
    }

    private fun setupPieChart(totalAmountList: ArrayList<AmountViewModel>) {
        val calculatePercentage = ArrayList<PieEntry>()
        var totalValue = 0
        for (total in totalAmountList) {
            totalValue += total.total
        }
        for (element in totalAmountList) {
            var percentage = 0.1f
            if (element.total > 0f) {
                percentage = ((element.total / totalValue) * 100).toFloat()
            }
            calculatePercentage.add(PieEntry(percentage, element.title))
        }
        val pieDataSet = PieDataSet(calculatePercentage, "")
        pieDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        pieDataSet.sliceSpace = 2f
        pieDataSet.valueTextSize = 15f

        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(13f)
        pieData.setValueTextColor(Color.DKGRAY)
        pieData.setValueFormatter(LargeValueFormatter())

        /*val legend = pieChartView.legend
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        legend.orientation = Legend.LegendOrientation.VERTICAL
        legend.setDrawInside(false)*/

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
    }
}
