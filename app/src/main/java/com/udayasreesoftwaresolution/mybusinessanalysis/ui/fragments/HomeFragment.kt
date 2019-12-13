package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.AmountAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import java.lang.Appendable


private const val ARG_AMOUNTS = "total_amount"
class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var recyclerView : RecyclerView
    private lateinit var pieChartView : PieChart
    private lateinit var pieEmpty : TextView

    private lateinit var notificationLayout : LinearLayout
    private lateinit var chartLayout : LinearLayout
    private lateinit var recyclerLayout : LinearLayout
    private lateinit var slideImage : ImageView

    private lateinit var progressBox : ProgressBox
    private var isSlide = false

    companion object {
        fun newInstance(totalAmountList : ArrayList<AmountModel>) : HomeFragment {
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
        pieEmpty = view.findViewById(R.id.frag_home_piechart_empty_id)

        notificationLayout = view.findViewById(R.id.frag_home_notification_layout)
        chartLayout = view.findViewById(R.id.frag_home_piechart_layout)
        recyclerLayout = view.findViewById(R.id.frag_home_bottom_layout_id)
        slideImage = view.findViewById(R.id.frag_home_slide_image)

        chartLayout.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.8).toInt()

        slideImage.setOnClickListener(this)

        progressBox = ProgressBox(context)
        setupRecyclerView()
        slideAnimation()
    }

    private fun slideAnimation() {
        if (isSlide) {
            /*OPEN*/
            isSlide = false
            slideImage.rotation = 270f
            recyclerView.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.5).toInt()
        } else {
            /*CLOSE*/
            isSlide = true
            slideImage.rotation = 90f
            recyclerView.layoutParams.height = 0
        }
    }

    private fun setupRecyclerView() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(ARG_AMOUNTS)) {
                progressBox.show()
                val totalAmountList  = bundle.getParcelableArrayList<AmountModel>(ARG_AMOUNTS)
                if (totalAmountList != null && totalAmountList.isNotEmpty()) {
                    setupPieChart(totalAmountList)
                    val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    val homeAdapter = AmountAdapter(activity!!, totalAmountList)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.adapter = homeAdapter
                    homeAdapter.notifyDataSetChanged()
                }
                progressBox.dismiss()
            }
        }
    }

    private fun setupPieChart(totalAmountList: ArrayList<AmountModel>) {
        val calculatePercentage = ArrayList<PieEntry>()
        var isAmountNotFount = true

        for (element in totalAmountList) {
            if (element.total > 0f) {
                isAmountNotFount = false
                calculatePercentage.add(PieEntry(element.total.toFloat(), element.title))
            }
        }

        if (isAmountNotFount) {
            pieEmpty.visibility = View.VISIBLE
            pieChartView.visibility = View.GONE
        } else {
            pieEmpty.visibility = View.GONE
            pieChartView.visibility = View.VISIBLE
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

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.frag_home_slide_image -> {
                slideAnimation()
            }
        }
    }
}
