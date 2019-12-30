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
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyData: TextView
    private lateinit var netAmountText: TextView
    private lateinit var expensesAmountText: TextView
    private lateinit var grossAmountText: TextView
    private lateinit var infoBtn : ImageView
    private lateinit var addBusinessFab: FloatingActionButton
    private lateinit var progressBox: ProgressBox

    private lateinit var businessTableList : ArrayList<BusinessTable>
    private var onlinePayments = 0
    private var netAmount = 0
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
        calendarLayout = view.findViewById(R.id.business_list_date_layout)
        addBusinessFab = view.findViewById(R.id.business_add_fab_id)

        netAmountText = view.findViewById(R.id.business_net_amount_id)
        expensesAmountText = view.findViewById(R.id.business_expenses_amount_id)
        grossAmountText = view.findViewById(R.id.business_gross_amount_id)

        infoBtn = view.findViewById(R.id.business_info_id)
        businessTableList = ArrayList()

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
        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            businessTableList.clear()
        }
        override fun doInBackground(vararg p0: Void?): Boolean {
            businessTableList.addAll(BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.OUTLET_CATEGORY) as ArrayList<BusinessTable>)
            businessTableList.addAll(BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.PAYMENT_CATEGORY) as ArrayList<BusinessTable>)
            businessTableList.addAll(BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.EXPENSES_CATEGORY) as ArrayList<BusinessTable>)
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (businessTableList.isNotEmpty()) {
                var expense = 0
                netAmount = 0
                onlinePayments = 0
                for (element in businessTableList) {
                    when(element.businessCategory) {
                        FireBaseConstants.OUTLET_CATEGORY -> {
                            netAmount += element.amount
                        }

                        FireBaseConstants.EXPENSES_CATEGORY -> {
                            expense += element.amount
                        }

                        FireBaseConstants.PAYMENT_CATEGORY -> {
                            onlinePayments += element.amount
                        }
                    }
                }
                recyclerView.visibility = View.VISIBLE
                emptyData.visibility = View.GONE
                val businessAdapter = BusinessAdapter(activity!!, businessTableList, netAmount, expense, onlinePayments)
                recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                recyclerView.adapter = businessAdapter
                businessAdapter.notifyDataSetChanged()
                setTotal(
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format(netAmount + onlinePayments),
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format(expense),
                    NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(resources.configuration)[0]).format((netAmount - expense))
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

            R.id.business_list_date_layout -> {
                calendarViewDialog()
            }

            R.id.business_info_id -> {
                val build = AlertDialog.Builder(activity)
                build.setMessage("Cash In Hands : ${netAmount - onlinePayments}\nOnline Payments: $onlinePayments")
                build.setCancelable(false)
                build.setPositiveButton("Ok", DialogInterface.OnClickListener { dialog, _ ->
                    dialog.dismiss()
                })
                build.create().show()
            }
        }
    }

    interface BusinessListInterface {
        fun addBusinessFragmentListener()
    }
}
