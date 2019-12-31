package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.BusinessRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.BusinessAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.lang.Exception
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
class BusinessListFragment : Fragment(), View.OnClickListener {

    private lateinit var calenderText: EditText
    private lateinit var calendarLayout : LinearLayout
    private lateinit var barChart : BarChart
    private lateinit var netAmountText: TextView
    private lateinit var expensesAmountText: TextView
    private lateinit var grossAmountText: TextView
    private lateinit var infoBtn : ImageView
    private lateinit var addBusinessFab: FloatingActionButton
    private lateinit var progressBox: ProgressBox

    private lateinit var outletTableList : ArrayList<BusinessTable>
    private lateinit var paymentTableList : ArrayList<BusinessTable>
    private lateinit var expensesTableList : ArrayList<BusinessTable>

    private lateinit var businessListInterface : BusinessListInterface

    companion object {
        fun newInstance() : BusinessListFragment {
            return BusinessListFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            businessListInterface = context as BusinessListInterface
        } catch (e : Exception){
            throw ClassCastException(context.toString().plus(" must implement BusinessListFragment Interface"))
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
        barChart = view.findViewById(R.id.business_list_barchart_id)
        calenderText = view.findViewById(R.id.business_list_date_id)
        calendarLayout = view.findViewById(R.id.business_list_date_layout)
        addBusinessFab = view.findViewById(R.id.business_add_fab_id)

        netAmountText = view.findViewById(R.id.business_net_amount_id)
        expensesAmountText = view.findViewById(R.id.business_expenses_amount_id)
        grossAmountText = view.findViewById(R.id.business_gross_amount_id)

        infoBtn = view.findViewById(R.id.business_info_id)

        outletTableList = ArrayList()
        paymentTableList = ArrayList()
        expensesTableList = ArrayList()

        addBusinessFab.setOnClickListener(this)
        calendarLayout.setOnClickListener(this)
        infoBtn.setOnClickListener(this)
        progressBox = ProgressBox(activity)

        calenderText.setText(AppUtils.getCurrentDate(true))

        if (!AppUtils.isAdminStatus) {
            addBusinessFab.hide()
        }
        val currentDateFormat = AppUtils.getCurrentDate(true)
        calenderText.setText(currentDateFormat)
        BusinessListByDateTask(currentDateFormat).execute()
    }


    @SuppressLint("SetTextI18n")
    private fun setTotal(netBusiness : String, expenses : String, grossBusiness : String) {
        netAmountText.text = "₹ $netBusiness/-"
        expensesAmountText.text = "₹ $expenses/-"
        grossAmountText.text = "₹ $grossBusiness/-"
    }

    private inner class BusinessListByDateTask(val dateFormat : String) : AsyncTask<Void, Void, Boolean>() {

        private var onlinePayments = 0
        private var netAmount = 0
        private var expense = 0
        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            outletTableList.clear()
            paymentTableList.clear()
            expensesTableList.clear()
        }
        override fun doInBackground(vararg p0: Void?): Boolean {
            outletTableList.addAll(BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.OUTLET_CATEGORY) as ArrayList<BusinessTable>)
            paymentTableList.addAll(BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.PAYMENT_CATEGORY) as ArrayList<BusinessTable>)
            expensesTableList.addAll(BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.EXPENSES_CATEGORY) as ArrayList<BusinessTable>)

            for (outlet in outletTableList) {
                netAmount += outlet.amount
            }
            for (payment in paymentTableList) {
                onlinePayments += payment.amount
            }
            for (expenses in expensesTableList) {
                expense += expenses.amount
            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (netAmount > 0 || onlinePayments > 0 || expense > 0) {

                val barEntityList = ArrayList<BarEntry>()
                barEntityList.add(BarEntry(netAmount.toFloat(), 0f))
                barEntityList.add(BarEntry(onlinePayments.toFloat(), 1f))
                barEntityList.add(BarEntry(expense.toFloat(), 2f))

                val yAxisName = ArrayList<String>()
                yAxisName.add(FireBaseConstants.OUTLET_CATEGORY)
                yAxisName.add(FireBaseConstants.PAYMENT_CATEGORY)
                yAxisName.add(FireBaseConstants.EXPENSES_CATEGORY)

                val barDataSet = BarDataSet(barEntityList, "")
                val barData = BarData(barDataSet)
                barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
                barDataSet.valueTextSize = 15f
                barChart.animateY(5000)
                barChart.data = barData
                barChart.invalidate()
                setTotal(
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format(netAmount),
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format(expense),
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format((netAmount - expense))
                )
            } else {
                setTotal("0","0", "0")
            }
            progressBox.dismiss()
        }
    }

    private fun calendarViewDialog() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
        val datePickerDialog: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                val simpleDateFormat = SimpleDateFormat(ConstantUtils.DATE_FORMAT, Locale.US)
                val date = simpleDateFormat.format(calendar.time)
                calenderText.setText(date ?: "")
                BusinessListByDateTask(date).execute()
            }

        val datePicker = DatePickerDialog(
            context!!, datePickerDialog, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = Date().time
        datePicker.show()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.business_add_fab_id -> {
                businessListInterface.addBusinessFragmentListener()
            }

            R.id.business_list_date_layout -> {
                calendarViewDialog()
            }
        }
    }

    interface BusinessListInterface {
        fun addBusinessFragmentListener()
    }
}
