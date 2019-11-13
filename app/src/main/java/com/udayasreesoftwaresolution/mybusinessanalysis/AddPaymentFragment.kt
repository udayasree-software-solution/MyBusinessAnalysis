package com.udayasreesoftwaresolution.mybusinessanalysis


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.ClientModel
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.PaymentModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressDialog
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.ClientRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PaymentRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_SLNO = "slno"

@SuppressLint("StaticFieldLeak")
class AddPaymentFragment : Fragment(), View.OnClickListener {

    private lateinit var progressBox: ProgressDialog
    private var _modifyTaskDataTable: PaymentTable? = null
    private var isTaskAddedStatus = false
    private val selectDays = "Select Days"
    private val selectCategory = "Select Category"

    private var _selectedDateInMills: Long = 0
    private var _companyName: String = ""
    private var _categoryName : String = ""
    private var _selectedDays: String = ""
    private var _chequeNo: String = ""
    private var _payableAmount: String = ""
    private var isModify = false
    private var _uniqueKeys = ""
    private lateinit var clientTableList: ArrayList<ClientsTable>
    private lateinit var categoryTableList : ArrayList<CategoryTable>
    private lateinit var clientsName: ArrayList<String>

    private lateinit var title: TextView
    private lateinit var companyName: AutoCompleteTextView
    private lateinit var selectDate: EditText
    private lateinit var taskRemindDaySpinner: Spinner
    private lateinit var categorySpinner : Spinner
    private lateinit var taskChequeNo: EditText
    private lateinit var taskAmount: EditText
    private lateinit var addTaskBtn: Button

    companion object {
        fun getInstance(slNo: Int): AddPaymentFragment {
            val fragment = AddPaymentFragment()
            val args = Bundle()
            args.putInt(ARG_SLNO, slNo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_payment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        title = view.findViewById(R.id.remainder_task_title_id)
        companyName = view.findViewById(R.id.remainder_task_company_id)
        taskRemindDaySpinner = view.findViewById(R.id.remainder_task_remind_id)
        categorySpinner = view.findViewById(R.id.remainder_task_category_id)
        selectDate = view.findViewById(R.id.remainder_task_date_id)
        taskChequeNo = view.findViewById(R.id.remainder_task_cheque_id)
        taskAmount = view.findViewById(R.id.remainder_task_amount_id)
        addTaskBtn = view.findViewById(R.id.remainder_task_btn_id)

        progressBox = ProgressDialog(activity)

        selectDate.setOnClickListener(this)
        addTaskBtn.setOnClickListener(this)
        addTaskBtn.text = "Add Task"

        title.typeface = AppUtils.getTypeFace(activity!!, ConstantUtils.MONTSERRAT)

        setCurrentDate()
        remindTaskDaysSpinner()
        ReadCategoryTaskAsync().execute()
    }

    inner class ReadCategoryTaskAsync() : AsyncTask<Void, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            clientTableList = ArrayList()
            categoryTableList = ArrayList()
            clientsName = ArrayList()
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            categoryTableList = CategoryRepository(activity!!).queryClientNamesList() as ArrayList<CategoryTable>
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            ReadClientTaskAsync().execute()
        }
    }

    inner class ReadClientTaskAsync() : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg p0: Void?): Boolean {
            clientTableList = ClientRepository(activity).queryClientNamesList() as ArrayList<ClientsTable>
            for (value in clientTableList) {
                clientsName.add(value.client)
            }
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            val slNo = arguments?.getInt(ARG_SLNO)!!
            if (slNo >= 0) {
                MenuTaskAsync(slNo).execute()
            } else {
                progressBox.show()
            }
        }
    }

    private fun setCurrentDate() {
        val simpleDateFormat = SimpleDateFormat(ConstantUtils.DATE_FORMAT, Locale.US)
        val calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
        calendar.set(Calendar.HOUR_OF_DAY, ConstantUtils.HOUR)
        calendar.set(Calendar.MINUTE, ConstantUtils.MINUTE)
        calendar.set(Calendar.SECOND, ConstantUtils.SECOND)
        _selectedDateInMills = calendar.timeInMillis
        selectDate.setText(simpleDateFormat.format(calendar.timeInMillis))
    }

    private fun remindTaskDaysSpinner() {
        val days = arrayOf(selectDays, "1", "2", "3", "4", "5", "6", "7")

        val daysAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, days)
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskRemindDaySpinner.adapter = daysAdapter
        taskRemindDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                _selectedDays = adapter?.selectedItem.toString()
            }
        }
    }

    private fun categoryDataSpinner() {
        val category = ArrayList<String>()
        category.add(selectCategory)
        for (value in categoryTableList) {
            category.add(value.category_name)
        }
        val categoryAdapter = ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, category)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        taskRemindDaySpinner.adapter = categoryAdapter
        taskRemindDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}
            override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                _categoryName = adapter?.selectedItem.toString()
            }
        }
    }

    inner class MenuTaskAsync(private val slNo: Int) : AsyncTask<Void, Void, PaymentTable>() {

        override fun doInBackground(vararg p0: Void?): PaymentTable {
            return PaymentRepository(activity).queryPaymentBySlNo(slNo) as PaymentTable
        }

        override fun onPostExecute(result: PaymentTable?) {
            super.onPostExecute(result)
            if (result != null) {
                with(result) {
                    _modifyTaskDataTable = result
                    val simpleDateFormat = SimpleDateFormat(ConstantUtils.DATE_FORMAT, Locale.US)
                    selectDate.setText(simpleDateFormat.format(Date(dateInMillis.toLong())))
                    taskChequeNo.setText(chequeNumber)
                    taskAmount.setText(payAmount)
                    taskRemindDaySpinner.setSelection(preDays)
                    addTaskBtn.text = "Apply Changes"
                    _uniqueKeys = uniqueKey
                    setupClientTextView(clientName)
                }
                progressBox.dismiss()
            }
        }
    }

    private fun setupClientTextView(name: String) {
        if (clientsName.isNotEmpty()) {
            val arrayAdapter = ArrayAdapter(activity!!, android.R.layout.select_dialog_item, clientsName)
            companyName.threshold = 1
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            companyName.setAdapter(arrayAdapter)
            companyName.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    _companyName = arrayAdapter.getItem(position)!!
                }
            arrayAdapter.notifyDataSetChanged()
            if (name != "NA") {
                for (i in clientsName.indices) {
                    if (clientsName[i] == name) {
                        companyName.setText(name)
                        _companyName = name
                        break
                    }
                }
            }
        }
    }

    private fun writeClientToFireBase(clientModel: ClientModel) {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.CLIENT)
                .push()
                .setValue(clientModel) { error, _ ->
                    if (error == null) {
                        readVersionOfChildFromFireBase(FireBaseConstants.CLIENT_VERSION)
                    }
                }
        }
    }

    private fun readVersionOfChildFromFireBase(child : String) {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(child)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
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
                            .setValue(bigDecimal.toDouble()) { error, _ ->
                                if (error == null) {

                                }
                            }
                    }
                }
            })
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
                calendar.set(Calendar.HOUR_OF_DAY, ConstantUtils.HOUR)
                calendar.set(Calendar.MINUTE, ConstantUtils.MINUTE)
                calendar.set(Calendar.SECOND, ConstantUtils.SECOND)

                val simpleDateFormat = SimpleDateFormat(ConstantUtils.DATE_FORMAT, Locale.US)
                _selectedDateInMills = calendar.timeInMillis
                selectDate.setText(simpleDateFormat.format(Date(calendar.timeInMillis)))
            }

        val datePicker = DatePickerDialog(
            activity!!, datePickerDialog, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun readAmountFromFireBase(modified : Boolean, pay : Int) {
        if (AppUtils.networkConnectivityCheck(activity!!)) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.TOTAL_AMOUNT)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        var payable = dataSnapShot.child(FireBaseConstants.PAYABLE_AMOUNT).getValue(Int::class.java)!!
                        if (modified){ payable += pay }else{ payable -= pay }
                        FirebaseDatabase.getInstance()
                            .getReference(AppUtils.OUTLET_NAME)
                            .child(FireBaseConstants.TOTAL_AMOUNT)
                            .child(FireBaseConstants.PAYABLE_AMOUNT)
                            .setValue(payable)
                    }
                }
            })
        }
    }

    private fun writePaymentToFireBase(paymentModel: PaymentModel) {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PAYMENT)
                .child(paymentModel.uniqueKey)
                .setValue(paymentModel) {error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase(FireBaseConstants.PAYMENT_VERSION)
                        clearInputs()
                    }
                }
        }
    }

    private fun addTaskToDB(isInsert: Boolean) {
        if (_companyName != companyName.text.toString()) {
            _companyName = ""
        }

        if (_companyName.isEmpty()) {
            _companyName = companyName.text.toString()
            if (_companyName.isNotEmpty()) {
                var isFound = false
                for (element in clientsName) {
                    if (_companyName.toLowerCase() == element.toLowerCase()) {
                        isFound = true
                        break
                    }
                }
                if (!isFound) {
                    if (_categoryName != selectCategory){
                        val model = ClientModel(_companyName, _categoryName)
                        writeClientToFireBase(model)
                    } else {
                        Toast.makeText(activity!!, "Please Select category", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        _chequeNo = taskChequeNo.text.toString()
        _payableAmount = taskAmount.text.toString()

        if (_selectedDays != selectDays && _categoryName != selectCategory && _companyName.isNotEmpty() &&
            _selectedDateInMills != 0L && _chequeNo.isNotEmpty() && _payableAmount.isNotEmpty()
        ) {
            readAmountFromFireBase(true, _payableAmount.toInt())

            _modifyTaskDataTable?.clientName = _companyName
            _modifyTaskDataTable?.categoryName = _categoryName
            _modifyTaskDataTable?.payAmount = _payableAmount
            _modifyTaskDataTable?.chequeNumber = _chequeNo
            _modifyTaskDataTable?.dateInMillis = _selectedDateInMills
            _modifyTaskDataTable?.paymentStatus = false
            _modifyTaskDataTable?.preDays = _selectedDays.toInt()

            _uniqueKeys = if (_uniqueKeys.isNotEmpty()) { _uniqueKeys } else { AppUtils.uniqueKey() }

            if (isInsert) {
                PaymentRepository(activity!!).insertTask(_modifyTaskDataTable)
            } else {
                PaymentRepository(activity!!).updateTask(_modifyTaskDataTable)
            }

            writePaymentToFireBase(PaymentModel(_uniqueKeys, _companyName, _payableAmount, _chequeNo, _selectedDateInMills,
                false, _selectedDays.toInt()))
        } else {
            if (_chequeNo.isEmpty() || _chequeNo.isBlank()) {
                taskChequeNo.error = "Enter Valid Cheque Number"
            }
            if (_selectedDateInMills == 0L) {
                selectDate.error = "Select Valid Date"
            }
            if (_payableAmount.isEmpty() || _payableAmount.isBlank()) {
                taskAmount.error = "Enter Valid Amount"
            }
            if (_companyName.isBlank() || _companyName.isEmpty()) {
                companyName.error = "Select Company Name"
            }
            if (_selectedDays.isBlank() || _selectedDays.isEmpty() || _selectedDays == selectDays) {
                Toast.makeText(activity!!, "Please Select days", Toast.LENGTH_SHORT).show()
            }
            if (_categoryName.isBlank() || _categoryName.isEmpty() || _categoryName == selectCategory) {
                Toast.makeText(activity!!, "Please Select category", Toast.LENGTH_SHORT).show()
            }
        }
        _companyName = ""
        progressBox.dismiss()
        if (!isInsert) {

        }
    }

    private fun clearInputs() {
        taskChequeNo.setText("")
        taskAmount.setText("")
        companyName.setText("")
        taskRemindDaySpinner.setSelection(0)
        setCurrentDate()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.remainder_task_date_id -> {
                // calendar action
                calendarViewDialog()
            }

            R.id.remainder_task_btn_id -> {
                // add/modify task to database
                isTaskAddedStatus = true
                if (AppUtils.networkConnectivityCheck(activity!!)) {
                    progressBox.show()
                    when (addTaskBtn.text) {

                        "Apply Changes" -> {
                            if (_modifyTaskDataTable != null) {
                                progressBox.show()
                                readAmountFromFireBase(false, _modifyTaskDataTable!!.payAmount.toInt())
                                Handler().postDelayed({
                                    addTaskToDB(false)
                                }, 7000)
                            }
                        }

                        else -> {
                            addTaskToDB(true)
                        }
                    }
                }
            }
        }
    }
}
