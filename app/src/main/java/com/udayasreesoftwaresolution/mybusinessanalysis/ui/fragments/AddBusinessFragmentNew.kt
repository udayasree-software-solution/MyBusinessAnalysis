package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.BusinessModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.BusinessRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.AddBusinessAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.math.BigDecimal
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

    private lateinit var outletTableList: ArrayList<BusinessTable>
    private lateinit var paymentTableList: ArrayList<BusinessTable>
    private lateinit var expensesTableList: ArrayList<BusinessTable>

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

        outletTableList = ArrayList()
        paymentTableList = ArrayList()
        expensesTableList = ArrayList()

        outletAmount = ArrayList()
        paymentAmount = ArrayList()
        expensesAmount = ArrayList()

        selectedDate = currentDateFormat
        timeInMillis = AppUtils.timeInMillis
        calendarText.setText(currentDateFormat)
        BusinessListByDateTask(currentDateFormat).execute()
    }

    private inner class BusinessListByDateTask(val dateFormat: String) :
        AsyncTask<Void, Void, Boolean>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            outletTableList = BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.OUTLET_CATEGORY) as ArrayList<BusinessTable>
            paymentTableList = BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.PAYMENT_CATEGORY) as ArrayList<BusinessTable>
            expensesTableList = BusinessRepository(activity).queryBusinessListByDateCategory(dateFormat, FireBaseConstants.EXPENSES_CATEGORY) as ArrayList<BusinessTable>

            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            if (outletTableList.isNotEmpty() && paymentTableList.isNotEmpty() && expensesTableList.isNotEmpty()) {
                isModifyBusiness = true
                launchRecyclerView(false)
            } else {
                isModifyBusiness = false
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
                createBusinessTable(result)
                launchRecyclerView(false)
            } else {
                progressBox.dismiss()
            }
        }
    }

    private fun createBusinessTable(result : ArrayList<CategoryTable>) {
        var outletCount = 2
        var paymentCount = 1
        var expensesCount = 1
        for (element in result) {
            with(element) {
                when(category_key) {
                    FireBaseConstants.OUTLET_CATEGORY -> {
                        when(category_name) {
                            AppUtils.OUTLET_NAME -> {
                                outletTableList.add(BusinessTable(1, 0, category_name,
                                    category_key, selectedDate, timeInMillis))
                            }

                            else -> {
                                outletTableList.add(BusinessTable(outletCount, 0, category_name,
                                    category_key, selectedDate, timeInMillis))
                                outletCount++
                            }
                        }
                    }

                    FireBaseConstants.PAYMENT_CATEGORY -> {
                        paymentTableList.add(BusinessTable(paymentCount, 0, category_name,
                            category_key, selectedDate, timeInMillis))
                        paymentCount++
                    }

                    FireBaseConstants.EXPENSES_CATEGORY -> {
                        expensesTableList.add(BusinessTable(expensesCount, 0, category_name,
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

        Collections.sort(outletTableList, compareAsc)
        Collections.sort(paymentTableList, compareAsc)
        Collections.sort(expensesTableList, compareAsc)
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

    private fun launchRecyclerView(status: Boolean) {
        progressBox.dismiss()
        parentLayout.animation = if (status) {
            AnimationUtils.loadAnimation(activity, R.anim.left_side)
        } else {
            AnimationUtils.loadAnimation(activity, R.anim.right_side)
        }
        nextBtn.setText(R.string.next)
        previousBtn.visibility = View.VISIBLE
        nextBtn.visibility = View.VISIBLE
        when(clickCount) {
            1 -> {
                previousBtn.visibility = View.INVISIBLE
                setupRecyclerView(outletTableList)
            }

            2 -> {
                setupRecyclerView(paymentTableList)
            }

            3 -> {
                nextBtn.setText(R.string.done)
                setupRecyclerView(expensesTableList)
            }

            4 -> {
                saveDataToServer()
            }
        }
    }

    private fun saveDataToServer() {
        progressBox.show()
        val finalBusinessList = ArrayList<BusinessTable>()
        finalBusinessList.addAll(outletTableList)
        finalBusinessList.addAll(paymentTableList)
        finalBusinessList.addAll(expensesTableList)

        outletTableList.clear()
        paymentTableList.clear()
        expensesTableList.clear()

        val fireBaseReference = FirebaseDatabase.getInstance()
            .getReference(AppUtils.OUTLET_NAME)
            .child(FireBaseConstants.BUSINESS)
            .child(selectedDate)
        fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                progressBox.dismiss()
                writeBusinessToFireBase(finalBusinessList)
            }

            override fun onDataChange(dataSnapShot: DataSnapshot) {
                if (dataSnapShot.exists()) {
                    dataSnapShot.ref.removeValue()
                }
                BusinessRepository(activity).deleteBusiness(selectedDate)
                Handler().postDelayed({
                    progressBox.dismiss()
                    writeBusinessToFireBase(finalBusinessList)
                },4000)
            }
        })
    }

    private fun writeBusinessToFireBase(businessList : ArrayList<BusinessTable>) {
        if (AppUtils.networkConnectivityCheck(context!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            progressBox.show()
            val businessRepository = BusinessRepository(activity)
            for (count in 0 until businessList.size) {
                val businessTable = businessList[count]
                with(businessTable) {
                    val businessModel = BusinessModel(ascOrder, amount, businessName, businessCategory, selectedDate, timeInMillis)
                    FirebaseDatabase.getInstance()
                        .getReference(AppUtils.OUTLET_NAME)
                        .child(FireBaseConstants.BUSINESS)
                        .child(selectedDate)
                        .child(count.toString())
                        .setValue(businessModel) { error, _ ->
                            if (error == null) {
                                businessRepository.insertBusiness(businessTable)
                                readVersionOfChildFromFireBase()
                            }
                            if (count == (businessList.size - 1)) {
                                progressBox.dismiss()
                            }
                        }
                }
            }
        }
    }

    private fun readVersionOfChildFromFireBase() {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(FireBaseConstants.BUSINESS_VERSION)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                    Toast.makeText(
                        activity!!,
                        "Server connection failed. Please try again",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        var version = dataSnapShot.getValue(Double::class.java)!!
                        version += 0.001
                        val bigDecimal = BigDecimal(version).setScale(3, BigDecimal.ROUND_HALF_UP)

                        fireBaseReference
                            .setValue(bigDecimal.toDouble())
                    }
                    progressBox.dismiss()
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.outlet_business_calender_layout -> {
                calendarViewDialog()
            }

            R.id.outlet_business_previous_id -> {
                clickCount--
                launchRecyclerView(false)
            }

            R.id.outlet_business_next_id -> {
                clickCount++
                progressBox.show()
                when(clickCount) {
                    2 -> {
                        outletAmount.clear()
                        outletAmount = adapter.getTextValues()
                        if (outletAmount.size == outletTableList.size) {
                            for (i in 0 until outletTableList.size) {
                                val outletList = outletAmount[i]
                                val outletTable = outletTableList[i]
                                if (outletList.title == outletTable.businessName) {
                                    outletTableList[i] =
                                        BusinessTable(outletTable.ascOrder, outletList.total, outletTable.businessName,
                                            outletTable.businessCategory, outletTable.selectedDate, outletTable.timeInMillis)
                                }
                            }
                        }
                    }
                    3 -> {
                        paymentAmount.clear()
                        paymentAmount = adapter.getTextValues()
                        if (paymentAmount.size == paymentTableList.size) {
                            for (i in 0 until paymentTableList.size) {
                                val paymentList = paymentAmount[i]
                                val paymentTable = paymentTableList[i]
                                if (paymentList.title == paymentTable.businessName) {
                                    paymentTableList[i] =
                                        BusinessTable(paymentTable.ascOrder, paymentList.total, paymentTable.businessName,
                                            paymentTable.businessCategory, paymentTable.selectedDate, paymentTable.timeInMillis)
                                }
                            }
                        }
                    }
                    4 -> {
                        expensesAmount.clear()
                        expensesAmount = adapter.getTextValues()
                        if (expensesAmount.size == expensesTableList.size) {
                            for (i in 0 until expensesTableList.size) {
                                val expensesList = expensesAmount[i]
                                val expensesTable = expensesTableList[i]
                                if (expensesList.title == expensesTable.businessName) {
                                    expensesTableList[i] =
                                        BusinessTable(expensesTable.ascOrder, expensesList.total, expensesTable.businessName,
                                            expensesTable.businessCategory, expensesTable.selectedDate, expensesTable.timeInMillis)
                                }
                            }
                        }
                    }
                }
                launchRecyclerView(true)
            }
        }
    }
}
