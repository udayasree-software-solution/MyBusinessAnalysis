package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
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
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.ClientModel
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.PaymentModel
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.SingleEntityModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
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

@SuppressLint("StaticFieldLeak", "SetTextI18n")
class AddPaymentFragment : Fragment(), View.OnClickListener {

    private lateinit var progressBox: ProgressBox
    private var _modifyTaskDataTable: PaymentTable? = null
    private val selectDays = "Select Days"
    private var isNewPayment = true

    private var _selectedDateInMills: Long = 0
    private var _companyName: String = ""
    private var _categoryName: String = ""
    private var _selectedDays: String = ""
    private var _chequeNo: String = ""
    private var _payableAmount: String = ""
    private var _uniqueKeys = ""

    private var isClientSelected = false
    private var isCategorySelected = false
    private lateinit var clientTableList: ArrayList<ClientsTable>
    private lateinit var clientsName: ArrayList<String>
    private lateinit var categorysName: ArrayList<String>

    private lateinit var title: TextView
    private lateinit var companyAutoText: AutoCompleteTextView
    private lateinit var categoryAutoText: AutoCompleteTextView
    private lateinit var selectDate: EditText
    private lateinit var taskRemindDaySpinner: Spinner
    private lateinit var taskChequeNo: EditText
    private lateinit var taskAmount: EditText
    private lateinit var addTaskBtn: Button

    private lateinit var addPaymentInterface: AddPaymentInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            addPaymentInterface = context as AddPaymentInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString().plus(" must implement PaymentFragment"))
        }
    }

    companion object {
        fun newInstance(slNo: Int): AddPaymentFragment {
            val fragment = AddPaymentFragment()
            val args = Bundle()
            args.putInt(ARG_SLNO, slNo)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_payment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        title = view.findViewById(R.id.remainder_task_title_id)
        companyAutoText = view.findViewById(R.id.remainder_task_company_id)
        taskRemindDaySpinner = view.findViewById(R.id.remainder_task_remind_id)
        categoryAutoText = view.findViewById(R.id.remainder_task_category_id)
        selectDate = view.findViewById(R.id.remainder_task_date_id)
        taskChequeNo = view.findViewById(R.id.remainder_task_cheque_id)
        taskAmount = view.findViewById(R.id.remainder_task_amount_id)
        addTaskBtn = view.findViewById(R.id.remainder_task_btn_id)

        progressBox = ProgressBox(activity)

        selectDate.setOnClickListener(this)
        addTaskBtn.setOnClickListener(this)
        addTaskBtn.text = "Add Task"

        title.typeface = AppUtils.getTypeFace(activity!!, ConstantUtils.MONTSERRAT)

        setCurrentDate()
        remindTaskDaysSpinner()
        ReadCategoryTaskAsync().execute()
    }

    inner class ReadCategoryTaskAsync : AsyncTask<Void, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            clientTableList = ArrayList()
            categorysName = ArrayList()
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            categorysName =
                CategoryRepository(activity!!).queryCategoryNamesList() as ArrayList<String>
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            progressBox.dismiss()
            ReadClientTaskAsync().execute()
        }
    }

    inner class ReadClientTaskAsync : AsyncTask<Void, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            clientsName = ArrayList()
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            clientsName = ClientRepository(activity).queryClientNamesList() as ArrayList<String>
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            setupClientTextView()
            setupCategoryTextView()
            val slNo = arguments?.getInt(ARG_SLNO)!!
            progressBox.dismiss()
            _modifyTaskDataTable = PaymentTable()

            when(slNo) {
                -1 -> {
                    isNewPayment = true
                }
                else -> {
                    isNewPayment = false
                    MenuTaskAsync(slNo).execute()
                }
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
        daysAdapter.notifyDataSetChanged()
        taskRemindDaySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                _selectedDays = adapter?.selectedItem.toString()
            }
        }
    }

    inner class MenuTaskAsync(private val slNo: Int) : AsyncTask<Void, Void, PaymentTable>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): PaymentTable {
            return PaymentRepository(activity).queryPaymentBySlNo(slNo) as PaymentTable
        }

        override fun onPostExecute(result: PaymentTable?) {
            super.onPostExecute(result)
            if (result != null) {
                with(result) {
                    _modifyTaskDataTable = result
                    val simpleDateFormat = SimpleDateFormat(ConstantUtils.DATE_FORMAT, Locale.US)
                    selectDate.setText(simpleDateFormat.format(Date(dateInMillis)))
                    taskChequeNo.setText(chequeNumber)
                    taskAmount.setText(payAmount)
                    _selectedDateInMills = dateInMillis
                    companyAutoText.setText(clientName)
                    categoryAutoText.setText(categoryName)
                    taskRemindDaySpinner.setSelection(preDays)
                    addTaskBtn.text = "Apply Changes"
                    _uniqueKeys = uniqueKey
                    _selectedDays = selectDays

                    if (categoryName != "NA") {
                        for (i in categorysName.indices) {
                            if (categorysName[i] == categoryName) {
                                categoryAutoText.setText(categoryName)
                                _categoryName = categoryName
                                isCategorySelected = true
                                break
                            }
                        }
                    }

                    if (clientName != "NA") {
                        for (i in clientsName.indices) {
                            if (clientsName[i] == clientName) {
                                companyAutoText.setText(clientName)
                                _companyName = clientName
                                isClientSelected = true
                                break
                            }
                        }
                    }
                }
            }
            progressBox.dismiss()
        }
    }

    private fun setupClientTextView() {
        if (clientsName.isNotEmpty()) {
            val arrayAdapter =
                ArrayAdapter(activity!!, android.R.layout.select_dialog_item, clientsName)
            companyAutoText.threshold = 1
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            companyAutoText.setAdapter(arrayAdapter)
            companyAutoText.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    _companyName = arrayAdapter.getItem(position)!!
                    isClientSelected = true
                }
            arrayAdapter.notifyDataSetChanged()
        }
    }

    private fun setupCategoryTextView() {
        if (categorysName.isNotEmpty()) {
            val categoryAdapter =
                ArrayAdapter(activity!!, android.R.layout.select_dialog_item, categorysName)
            categoryAutoText.threshold = 1
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categoryAutoText.setAdapter(categoryAdapter)
            categoryAdapter.notifyDataSetChanged()
            categoryAutoText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    _categoryName = adapter?.selectedItem.toString()
                    isCategorySelected = true
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

    private fun writeCategoryToFireBase(category: String) {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.BUSINESS_CATEGORY)
                .push()
                .setValue(category) { error, _ ->
                    if (error == null) {
                        readVersionOfChildFromFireBase(FireBaseConstants.BUSINESS_CATEGORY_VERSION)
                    }
                }
        }
    }

    private fun readVersionOfChildFromFireBase(child: String) {
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
                            .setValue(bigDecimal.toDouble())
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

    private fun readAmountFromFireBase(modified: Boolean, pay: Int) {
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
                        var payable =
                            dataSnapShot.child(FireBaseConstants.PAYABLE_AMOUNT).getValue(Int::class.java)!!
                        if (modified) {
                            payable += pay
                        } else {
                            payable -= pay
                        }
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
            progressBox.show()
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PAYMENT)
                .child(paymentModel.uniqueKey)
                .setValue(paymentModel) { error, _ ->
                    if (error == null) {
                        val paymentRepo = PaymentRepository(activity)
                        if (isNewPayment) {
                            with(paymentModel) {
                                paymentRepo.insertTask(PaymentTable(uniqueKey, clientName, categoryName, payAmount, chequeNumber, dateInMillis, payStatus, preDays))
                            }
                        } else {
                            paymentRepo.updateTask(_modifyTaskDataTable)
                        }
                        readVersionOfChildFromFireBase(FireBaseConstants.PAYMENT_VERSION)
                        Handler().postDelayed({
                            clearInputs()
                            _companyName = ""
                            _categoryName = ""
                            progressBox.dismiss()
                            if (!isNewPayment) {
                                addPaymentInterface.onSuccessfulModified()
                            }
                        }, 1000)
                    } else {
                        Toast.makeText(
                            activity!!,
                            "Unable to connect to server",
                            Toast.LENGTH_SHORT
                        ).show()
                        _companyName = ""
                        _categoryName = ""
                        progressBox.dismiss()
                    }
                }
        }
    }

    private fun checkCategoryAvailable() {
        _categoryName = categoryAutoText.text.toString().trim()
        var isNotFound = true
        if (_categoryName.isNotEmpty()) {
            for (element in categorysName) {
                if (_categoryName.equals(element.trim(), ignoreCase = true)) {
                    isNotFound = false
                    break
                }
            }
        }
        if (isNotFound) {
            writeCategoryToFireBase(_categoryName)
            CategoryRepository(activity!!).insertTask(CategoryTable(_categoryName))
        }
    }

    private fun checkClientAvailable() {
        _companyName = companyAutoText.text.toString().trim()
        var isNotFound = true
        if (_companyName.isNotEmpty()) {
            for (element in clientsName) {
                if (_companyName.equals(element.trim(), ignoreCase = true)) {
                    isNotFound = false
                    break
                }
            }
        }
        if (isNotFound) {
            val model = ClientModel(_companyName, _categoryName)
            writeClientToFireBase(model)
            ClientRepository(activity).insertClient(ClientsTable(_companyName, _categoryName))
        }
    }

    private fun addNewTaskToFireBase() {
        if (_selectedDays != selectDays && _categoryName.isNotEmpty() && _companyName.isNotEmpty() &&
            _selectedDateInMills != 0L && _chequeNo.isNotEmpty() && _payableAmount.isNotEmpty()) {

            readAmountFromFireBase(true, _payableAmount.toInt())
            _uniqueKeys = AppUtils.uniqueKey()

            Handler().postDelayed({
                writePaymentToFireBase(
                    PaymentModel(
                        _uniqueKeys, _companyName, _categoryName, _payableAmount, _chequeNo, _selectedDateInMills,
                        false, _selectedDays.toInt()
                    )
                )
            }, 2000)

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
                companyAutoText.error = "Select Company Name"
            }
            if (_selectedDays.isBlank() || _selectedDays.isEmpty() || _selectedDays == selectDays) {
                Toast.makeText(activity!!, "Please Select days", Toast.LENGTH_SHORT).show()
            }
            if (_categoryName.isBlank() || _categoryName.isEmpty()) {
                Toast.makeText(activity!!, "Select Category", Toast.LENGTH_SHORT).show()
            }
            progressBox.dismiss()
        }
    }

    private fun modifyTaskFromFireBase() {
        if (_selectedDays != selectDays && _categoryName.isNotEmpty() && _companyName.isNotEmpty() &&
            _selectedDateInMills != 0L && _chequeNo.isNotEmpty() && _payableAmount.isNotEmpty()) {

            readAmountFromFireBase(true, _payableAmount.toInt())

            _modifyTaskDataTable?.clientName = _companyName
            _modifyTaskDataTable?.categoryName = _categoryName
            _modifyTaskDataTable?.payAmount = _payableAmount
            _modifyTaskDataTable?.chequeNumber = _chequeNo
            _modifyTaskDataTable?.dateInMillis = _selectedDateInMills
            _modifyTaskDataTable?.paymentStatus = false
            _modifyTaskDataTable?.preDays = _selectedDays.toInt()
            _modifyTaskDataTable?.uniqueKey = _uniqueKeys

            Handler().postDelayed({
                writePaymentToFireBase(
                    PaymentModel(
                        _uniqueKeys, _companyName, _categoryName, _payableAmount, _chequeNo, _selectedDateInMills,
                        false, _selectedDays.toInt()
                    )
                )
            }, 2000)
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
                companyAutoText.error = "Select Company Name"
            }
            if (_selectedDays.isBlank() || _selectedDays.isEmpty() || _selectedDays == selectDays) {
                Toast.makeText(activity!!, "Please Select days", Toast.LENGTH_SHORT).show()
            }
            if (_categoryName.isBlank() || _categoryName.isEmpty()) {
                Toast.makeText(activity!!, "Select Category", Toast.LENGTH_SHORT).show()
            }
            progressBox.dismiss()
        }
    }

    private fun clearInputs() {
        taskChequeNo.setText("")
        taskAmount.setText("")
        companyAutoText.setText("")
        categoryAutoText.setText("")
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

                if (AppUtils.networkConnectivityCheck(activity!!)) {
                    progressBox.show()
                    checkCategoryAvailable()
                    checkClientAvailable()

                    _chequeNo = taskChequeNo.text.toString()
                    _payableAmount = taskAmount.text.toString()

                    if (isNewPayment) {
                        addNewTaskToFireBase()
                    } else {
                        readAmountFromFireBase(false, _modifyTaskDataTable!!.payAmount.toInt())
                        Handler().postDelayed({modifyTaskFromFireBase()}, 7000)
                    }
                }
            }
        }
    }

    interface AddPaymentInterface {
        fun onSuccessfulModified()
    }
}
