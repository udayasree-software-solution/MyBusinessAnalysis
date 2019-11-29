package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.udayasreesoftwaresolution.mybusinessanalysis.R
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
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyData: TextView
    private lateinit var netAmountText: TextView
    private lateinit var expensesAmountText: TextView
    private lateinit var grossAmountText: TextView
    private lateinit var addBusinessFab: FloatingActionButton
    private lateinit var progressBox: ProgressBox

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
        recyclerView = view.findViewById(R.id.business_list_recycler_id)
        emptyData = view.findViewById(R.id.business_list_empty_id)
        calenderText = view.findViewById(R.id.business_list_date_id)
        addBusinessFab = view.findViewById(R.id.business_add_fab_id)

        netAmountText = view.findViewById(R.id.business_net_amount_id)
        expensesAmountText = view.findViewById(R.id.business_expenses_amount_id)
        grossAmountText = view.findViewById(R.id.business_gross_amount_id)

        addBusinessFab.setOnClickListener(this)
        calenderText.setOnClickListener(this)
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

    private inner class BusinessListByDateTask(val dateFormat : String) : AsyncTask<Void, Void, ArrayList<BusinessTable>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }
        override fun doInBackground(vararg p0: Void?): ArrayList<BusinessTable> {
            return BusinessRepository(activity).queryBusinessByDate(dateFormat) as ArrayList<BusinessTable>
        }

        override fun onPostExecute(result: ArrayList<BusinessTable>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                var expense = 0
                var netAmount = 0
                for (element in result) {
                    if (element.businessName.equals("Expenses", ignoreCase = true)) {
                        expense += element.amount
                    } else {
                        netAmount += element.amount
                    }
                }
                recyclerView.visibility = View.VISIBLE
                emptyData.visibility = View.GONE
                val businessAdapter = BusinessAdapter(activity!!, result)
                recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = businessAdapter
                businessAdapter.notifyDataSetChanged()
                setTotal(
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format(netAmount),
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format((netAmount - expense)),
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format(expense)
                )
            } else {
                setTotal("0","0", "0")
                emptyData.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
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

            R.id.business_list_date_id -> {
                calendarViewDialog()
            }
        }
    }

    interface BusinessListInterface {
        fun addBusinessFragmentListener()
    }
}
