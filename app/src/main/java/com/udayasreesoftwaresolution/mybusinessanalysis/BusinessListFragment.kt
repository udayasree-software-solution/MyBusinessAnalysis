package com.udayasreesoftwaresolution.mybusinessanalysis


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountViewModel
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.BusinessViewIds
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class BusinessListFragment : Fragment(), View.OnClickListener {

    private lateinit var listCalendar: EditText
    private lateinit var listRecyclerView: RecyclerView
    private lateinit var listEmpty: TextView
    private lateinit var netAmountText: TextView
    private lateinit var expensesAmountText: TextView
    private lateinit var grossAmountText: TextView
    private lateinit var addFab: FloatingActionButton
    private lateinit var progressBox: ProgressBox

    private lateinit var editBusinessList : ArrayList<AmountViewModel>

    companion object {
        fun newInstance() : BusinessListFragment {
            return BusinessListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_business_list, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        listRecyclerView = view.findViewById(R.id.business_list_recycler_id)
        listEmpty = view.findViewById(R.id.business_list_empty_id)
        listCalendar = view.findViewById(R.id.business_list_date_id)
        addFab = view.findViewById(R.id.business_add_fab_id)

        netAmountText = view.findViewById(R.id.business_net_amount_id)
        expensesAmountText = view.findViewById(R.id.business_expenses_amount_id)
        grossAmountText = view.findViewById(R.id.business_gross_amount_id)

        addFab.setOnClickListener(this)
        listCalendar.setOnClickListener(this)

        listCalendar.setText(AppUtils.getCurrentDate(true))

        if (!AppUtils.isAdminStatus) {
            addFab.hide()
        }
        editBusinessList = ArrayList()
        progressBox = ProgressBox(activity)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.business_add_fab_id -> {

            }

            R.id.business_list_date_id -> {

            }
        }
    }
}
