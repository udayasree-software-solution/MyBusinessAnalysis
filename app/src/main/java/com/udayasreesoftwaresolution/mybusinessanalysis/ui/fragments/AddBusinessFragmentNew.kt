package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.BusinessRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.AddBusinessAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@SuppressLint("StaticFieldLeak")
class AddBusinessFragmentNew : Fragment(), View.OnClickListener {

    private lateinit var parentLayout : RelativeLayout
    private lateinit var title : TextView
    private lateinit var recyclerView : RecyclerView
    private lateinit var previousBtn : TextView
    private lateinit var nextBtn : TextView

    private lateinit var calendarLayout : LinearLayout
    private lateinit var calendarText : EditText
    private lateinit var adapter : AddBusinessAdapter

    private lateinit var progressBox : ProgressBox

    private var isModifyBusiness = false
    private var timeInMillis : Long = 0L
    private var selectedDate : String = ""
    private var clickCount: Int = 1

    private lateinit var businessTableList: ArrayList<BusinessTable>
    private lateinit var outletAmount : ArrayList<AmountModel>
    private lateinit var paymentAmount : ArrayList<AmountModel>
    private lateinit var expensesAmount : ArrayList<AmountModel>

    companion object {
        fun newInstance(): AddBusinessFragmentNew {
            return AddBusinessFragmentNew()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_business_fragment_new, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        parentLayout = view.findViewById(R.id.outlet_business_purchase)
        title = view.findViewById(R.id.outlet_business_title)
        recyclerView = view.findViewById(R.id.outlet_business_recyclerview)
        previousBtn = view.findViewById(R.id.outlet_business_previous_id)
        nextBtn = view.findViewById(R.id.outlet_business_next_id)

        calendarLayout = view.findViewById(R.id.outlet_business_calender_layout)
        calendarText = view.findViewById(R.id.outlet_business_calender_text)

        progressBox = ProgressBox(activity)

        previousBtn.setOnClickListener(this)
        nextBtn.setOnClickListener(this)
        calendarLayout.setOnClickListener(this)

        val currentDateFormat = AppUtils.getCurrentDate(true)

        businessTableList = ArrayList()
        selectedDate = currentDateFormat
        timeInMillis = AppUtils.timeInMillis
        calendarText.setText(currentDateFormat)
        BusinessListByDateTask(currentDateFormat).execute()
    }

    private inner class BusinessListByDateTask(val dateFormat: String) :
        AsyncTask<Void, Void, ArrayList<BusinessTable>>() {
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
                isModifyBusiness = true
                /*TODO: Display data with amount in recyclerview*/
                businessTableList.addAll(result)
                launchRecyclerView()
            } else {
                isModifyBusiness = false
                /*TODO: Fetch business outlet, payment & Expenses from DB and display in recyclerview*/
                CategoryListTask().execute()
            }
        }
    }

    private inner class CategoryListTask : AsyncTask<Void, Void, ArrayList<CategoryTable>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<CategoryTable> {
            return CategoryRepository(activity).queryFullCategoryList() as ArrayList<CategoryTable>
        }

        override fun onPostExecute(result: ArrayList<CategoryTable>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                /*TODO: Display data with amount in recyclerview*/
                createBusinessTable(result)
                launchRecyclerView()
            } else {
                progressBox.dismiss()
            }
        }
    }

    private fun createBusinessTable(result : ArrayList<CategoryTable>) {
        var outletCount = 2
        var paymentCount = 1
        var expensesCount = 1
        businessTableList.clear()
        for (element in result) {
            with(element) {
                when(category_key) {
                    FireBaseConstants.OUTLET_CATEGORY -> {
                        when(category_name) {
                            AppUtils.OUTLET_NAME -> {
                                businessTableList.add(BusinessTable(1, 0, category_name,
                                    category_key, selectedDate, timeInMillis))
                            }

                            else -> {
                                businessTableList.add(BusinessTable(outletCount, 0, category_name,
                                    category_key, selectedDate, timeInMillis))
                                outletCount++
                            }
                        }
                    }

                    FireBaseConstants.PAYMENT_CATEGORY -> {
                        businessTableList.add(BusinessTable(paymentCount, 0, category_name,
                            category_key, selectedDate, timeInMillis))
                        paymentCount++
                    }

                    FireBaseConstants.EXPENSES_CATEGORY -> {
                        businessTableList.add(BusinessTable(expensesCount, 0, category_name,
                            category_key, selectedDate, timeInMillis))
                        expensesCount++
                    }

                    else -> {}
                }
            }
        }

        val compareAsc = Comparator { o1: BusinessTable, o2: BusinessTable ->
            o1.ascOrder.compareTo(o2.ascOrder)
        }

        Collections.sort(businessTableList, compareAsc)
    }

    private fun setupRecyclerView(tableList : ArrayList<BusinessTable>) {
        if (tableList.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            adapter = AddBusinessAdapter(activity?.applicationContext!!, tableList)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
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
                timeInMillis = calendar.timeInMillis
                selectedDate = simpleDateFormat.format(calendar.time)
                calendarText.setText(selectedDate)

                BusinessListByDateTask(selectedDate).execute()
            }

        val datePicker = DatePickerDialog(
            context!!, datePickerDialog, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = Date().time
        datePicker.show()
    }

    private fun launchRecyclerView() {
        val businessList = ArrayList<BusinessTable>()
        when(clickCount) {
            1 -> {
                for (element in businessTableList) {
                    with(element){
                        if (businessCategory == FireBaseConstants.OUTLET_CATEGORY) {
                            businessList.add(element)
                        }
                    }
                }
            }

            2 -> {
                for (element in businessTableList) {
                    with(element){
                        if (businessCategory == FireBaseConstants.PAYMENT_CATEGORY) {
                            businessList.add(element)
                        }
                    }
                }
            }

            3 -> {
                for (element in businessTableList) {
                    with(element){
                        if (businessCategory == FireBaseConstants.EXPENSES_CATEGORY) {
                            businessList.add(element)
                        }
                    }
                }
            }
        }
        progressBox.dismiss()
        setupRecyclerView(businessList)
    }


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.outlet_business_calender_layout -> {
                calendarViewDialog()
            }

            R.id.outlet_business_previous_id -> {
                clickCount--
                launchRecyclerView()
            }

            R.id.outlet_business_next_id -> {
                clickCount++
                progressBox.show()
                when(clickCount) {
                    2 -> {
                        outletAmount.clear()
                        outletAmount = adapter.getTextValues()
                        for (element in businessTableList) {
                            if (element.businessCategory == FireBaseConstants.OUTLET_CATEGORY) {
                                for (amount in outletAmount) {
                                    if (element.businessName == amount.title) {
                                        element.amount = amount.total
                                        break
                                    }
                                }
                            }
                        }
                    }
                    3 -> {
                        paymentAmount.clear()
                        paymentAmount = adapter.getTextValues()
                        for (element in businessTableList) {
                            if (element.businessCategory == FireBaseConstants.PAYMENT_CATEGORY) {
                                for (amount in paymentAmount) {
                                    if (element.businessName == amount.title) {
                                        element.amount = amount.total
                                        break
                                    }
                                }
                            }
                        }
                    }
                    4 -> {
                        expensesAmount.clear()
                        expensesAmount = adapter.getTextValues()
                        for (element in businessTableList) {
                            if (element.businessCategory == FireBaseConstants.EXPENSES_CATEGORY) {
                                for (amount in expensesAmount) {
                                    if (element.businessName == amount.title) {
                                        element.amount = amount.total
                                        break
                                    }
                                }
                            }
                        }
                    }
                }
                launchRecyclerView()
            }
        }
    }
}
