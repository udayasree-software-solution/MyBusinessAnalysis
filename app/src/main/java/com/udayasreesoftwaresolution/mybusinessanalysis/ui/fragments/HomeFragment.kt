package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.graphics.Color
import android.os.Bundle
import android.os.Handler
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
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.AmountAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.udayasreesoftwaresolution.mybusinessanalysis.R




private const val ARG_AMOUNTS = "total_amount"
class HomeFragment : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private lateinit var notificationParent : FrameLayout
    private lateinit var progressBox : ProgressBox

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
        notificationParent = view.findViewById(R.id.frag_home_notification_parent)
        progressBox = ProgressBox(context)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val bundle = arguments
        if (bundle != null) {
            if (bundle.containsKey(ARG_AMOUNTS)) {
                progressBox.show()
                val totalAmountList  = bundle.getParcelableArrayList<AmountModel>(ARG_AMOUNTS)
                if (totalAmountList != null && totalAmountList.isNotEmpty()) {
                    //val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                    val layoutManager = StaggeredGridLayoutManager(2, RecyclerView.VERTICAL)
                    val homeAdapter = AmountAdapter(activity!!, totalAmountList)
                    recyclerView.layoutManager = layoutManager
                    recyclerView.adapter = homeAdapter
                    homeAdapter.notifyDataSetChanged()
                }
                progressBox.dismiss()
            }
        }
    }
}
