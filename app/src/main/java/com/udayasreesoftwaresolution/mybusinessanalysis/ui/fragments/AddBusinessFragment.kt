package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
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
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.BusinessViewIds
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
class AddBusinessFragment : Fragment(), View.OnClickListener {

    private lateinit var calendarText: EditText
    private lateinit var includeLayout: LinearLayout
    private lateinit var insertBtn: ImageView
    private lateinit var saveBtn: Button
    private lateinit var fragView: View
    private lateinit var progressBox: ProgressBox

    private var isModifyBusiness = false

    private var selectedDate = ""
    private var timeInMillis = 0L

    private lateinit var businessLayoutIDs: ArrayList<BusinessViewIds>
    private lateinit var backupBusinessList: ArrayList<BusinessTable>

    companion object {
        fun newInstance(): AddBusinessFragment {
            return AddBusinessFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragView = inflater.inflate(R.layout.fragment_add_business, container, false)
        initView(fragView)
        return fragView
    }

    private fun initView(view: View) {
        calendarText = view.findViewById(R.id.business_add_date_id)
        includeLayout = view.findViewById(R.id.business_add_insert_id)
        insertBtn = view.findViewById(R.id.business_add_include_id)
        saveBtn = view.findViewById(R.id.business_add_save_id)
        progressBox = ProgressBox(activity)

        insertBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        calendarText.setOnClickListener(this)

        businessLayoutIDs = ArrayList()
        backupBusinessList = ArrayList()

        val currentDateFormat = AppUtils.getCurrentDate(true)
        selectedDate = currentDateFormat
        timeInMillis = AppUtils.timeInMillis
        calendarText.setText(currentDateFormat)
        BusinessListByDateTask(currentDateFormat).execute()
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
                includeLayout.removeAllViews()
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
            progressBox.dismiss()
            if (result != null && result.isNotEmpty()) {
                backupBusinessList.clear()
                backupBusinessList.addAll(result)
                isModifyBusiness = true
                for (element in result) {
                    createBusinessLayout(true, element.businessName, element.amount.toString())
                }
            } else {
                isModifyBusiness = false
                CategoryListTask().execute()
            }
        }
    }

    private inner class CategoryListTask : AsyncTask<Void, Void, ArrayList<String>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<String> {
            return CategoryRepository(activity).queryCategoryNamesList() as ArrayList<String>
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                for (element in result) {
                    createBusinessLayout(true, element, "")
                }
            } else {
                createBusinessLayout(true, "Expenses", "")
                createBusinessLayout(true, AppUtils.OUTLET_NAME, "")
            }
            progressBox.dismiss()
        }
    }

    private fun createBusinessLayout(isFirst: Boolean, categoryName: String, categoryAmount: String) {
        val parentId = View.generateViewId()
        val deleteId = View.generateViewId()
        val nameId = View.generateViewId()
        val amountId = View.generateViewId()
        val parentChildId = View.generateViewId()
        businessLayoutIDs.add(BusinessViewIds(parentId, deleteId, nameId, amountId))


        val parentLayout = RelativeLayout(activity)
        parentLayout.layoutParams =
            RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )
        parentLayout.id = parentId

        val deleteRow = ImageView(activity)
        val params = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.WRAP_CONTENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        params.addRule(RelativeLayout.ALIGN_PARENT_END)
        deleteRow.layoutParams = params
        deleteRow.setImageDrawable(ContextCompat.getDrawable(context!!, android.R.drawable.ic_delete))
        deleteRow.setBackgroundColor(Color.BLACK)
        deleteRow.id = deleteId

        val parentChildLayout = LinearLayout(activity)
        val layoutParams = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.addRule(RelativeLayout.BELOW, deleteId)
        layoutParams.setMargins(0, 5, 0, 0)
        parentChildLayout.layoutParams = layoutParams
        parentChildLayout.orientation = LinearLayout.HORIZONTAL
        parentChildLayout.weightSum = 2f
        parentChildLayout.id = parentChildId


        val nameTextLayout = TextInputLayout(context!!)
        nameTextLayout.layoutParams =
            LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        val nameEditText = EditText(activity)
        nameEditText.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        if (isFirst) {
            with(nameEditText) {
                setText(categoryName)
                if (categoryName.equals("Expenses", ignoreCase = true)) {
                    setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                } else {
                    setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorAccent
                        )
                    )
                }
                isClickable = false
                isCursorVisible = false
                isFocusable = false
                isFocusableInTouchMode = false
            }
        } else {
            nameEditText.inputType =
                InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_FLAG_CAP_WORDS + InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            nameEditText.hint = "Business Name"
        }
        nameEditText.id = nameId
        nameEditText.setSingleLine(true)
        nameEditText.maxLines = 1
        nameTextLayout.addView(nameEditText)

        val amountTextLayout = TextInputLayout(context!!)
        amountTextLayout.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        val amountEditText = EditText(activity)
        amountEditText.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        amountEditText.inputType = InputType.TYPE_CLASS_NUMBER
        amountEditText.hint = "Total Amount"
        amountEditText.id = amountId
        amountEditText.setText(
            if (categoryAmount.isNotEmpty()) {
                categoryAmount
            } else {
                ""
            }
        )
        amountTextLayout.addView(amountEditText)

        val divider = View(activity)
        val dividerParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 3)
        dividerParams.addRule(RelativeLayout.BELOW, parentChildId)
        divider.layoutParams = dividerParams
        divider.setBackgroundColor(Color.BLACK)
        divider.setPadding(0, 2, 0, 2)

        parentChildLayout.addView(nameTextLayout)
        parentChildLayout.addView(amountTextLayout)

        parentLayout.addView(deleteRow)
        parentLayout.addView(parentChildLayout)
        parentLayout.addView(divider)


        includeLayout.addView(parentLayout)

        deleteRow.setOnClickListener { view ->
            if (businessLayoutIDs.size > 1) {
                for (i in 0 until businessLayoutIDs.size) {
                    val ids = businessLayoutIDs[i]
                    if (i > 1) {
                        if (view.id == ids.deleteId) {
                            val relativeLayout = getView()?.findViewById<RelativeLayout>(ids.parentId)
                            (relativeLayout?.parent as LinearLayout).removeView(relativeLayout)
                            //includeLayout.removeView(view.findViewById(ids.parentId))
                            //includeLayout.invalidate()
                            businessLayoutIDs.removeAt(i)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun writeBusinessToFireBase(businessModelList: ArrayList<BusinessModel>) {
        if (AppUtils.networkConnectivityCheck(context!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            progressBox.show()
            for (count in 0 until businessModelList.size) {
                val businessModel = businessModelList[count]
                with(businessModel) {
                    FirebaseDatabase.getInstance()
                        .getReference(AppUtils.OUTLET_NAME)
                        .child(FireBaseConstants.BUSINESS)
                        .child(selectedDate)
                        .child(count.toString())
                        .setValue(businessModel) { _, _ ->
                            if (count == (businessModelList.size - 1)) {
                                progressBox.dismiss()
                                //remove layout
                                readVersionOfChildFromFireBase()
                            }
                        }
                }
            }
        }
    }

    private fun getDataFromViews() {
        if (AppUtils.networkConnectivityCheck(context!!) && AppUtils.OUTLET_NAME.isNotEmpty() && businessLayoutIDs.isNotEmpty()) {
            val businessModelList = ArrayList<BusinessModel>()
            var delay = 0L
            progressBox.show()
            if (isModifyBusiness) {
                delay = 7000L
                BusinessRepository(activity).deleteBusiness(selectedDate)
            }

            Handler().postDelayed({
                delay = 0L
                if (isModifyBusiness) {
                    delay = 7000L
                    var oldExpenses = 0
                    var oldNet = 0
                    for (element in backupBusinessList) {
                        if (element.businessName.equals("Expenses", ignoreCase = true)) {
                            oldExpenses += element.amount
                        } else {
                            oldNet += element.amount
                        }
                    }
                    modifyAmountFromFireBase(false, oldNet, oldExpenses)
                }

                Handler().postDelayed({
                    var expensesTotal = 0
                    var netTotal = 0
                    for (count in 0 until businessLayoutIDs.size) {
                        with(businessLayoutIDs[count]) {
                            if (fragView != null) {
                                val name = fragView.findViewById<EditText>(nameId).text.toString()
                                val amount = fragView.findViewById<EditText>(amountId).text.toString()
                                if (name.isNotEmpty() && amount.isNotEmpty()) {
                                    if (name.equals("Expenses", ignoreCase = true)) {
                                        expensesTotal += amount.toInt()
                                    } else {
                                        netTotal += amount.toInt()
                                    }
                                    businessModelList.add(
                                        BusinessModel(
                                            count,
                                            name,
                                            amount.toInt(),
                                            selectedDate,
                                            timeInMillis
                                        )
                                    )
                                    BusinessRepository(activity).insertBusiness(
                                        BusinessTable(
                                            count,
                                            amount.toInt(),
                                            name,
                                            selectedDate,
                                            timeInMillis
                                        )
                                    )
                                }
                            }
                        }
                    }
                    modifyAmountFromFireBase(true, netTotal, expensesTotal)

                    val fireBaseReference = FirebaseDatabase.getInstance()
                        .getReference(AppUtils.OUTLET_NAME)
                        .child(FireBaseConstants.BUSINESS)
                        .child(selectedDate)
                    fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {
                            progressBox.dismiss()
                            writeBusinessToFireBase(businessModelList)
                        }

                        override fun onDataChange(dataSnapShot: DataSnapshot) {
                            if (dataSnapShot.exists()) {
                                dataSnapShot.ref.removeValue()
                            }
                            progressBox.dismiss()
                            writeBusinessToFireBase(businessModelList)
                        }
                    })
                }, delay)
            }, delay)
        }
    }

    private fun modifyAmountFromFireBase(modified: Boolean, net: Int, expenses: Int) {
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
                        var expensesTotal = dataSnapShot.child(FireBaseConstants.EXPENSES_AMOUNT).getValue(Int::class.java)!!
                        var netTotal = dataSnapShot.child(FireBaseConstants.NET_AMOUNT).getValue(Int::class.java)!!

                        if (modified) {
                            expensesTotal += expenses
                            netTotal += net
                        } else {
                            expensesTotal -= expenses
                            netTotal -= net
                        }

                        FirebaseDatabase.getInstance()
                            .getReference(AppUtils.OUTLET_NAME)
                            .child(FireBaseConstants.TOTAL_AMOUNT)
                            .child(FireBaseConstants.EXPENSES_AMOUNT)
                            .setValue(expensesTotal)

                        FirebaseDatabase.getInstance()
                            .getReference(AppUtils.OUTLET_NAME)
                            .child(FireBaseConstants.TOTAL_AMOUNT)
                            .child(FireBaseConstants.NET_AMOUNT)
                            .setValue(netTotal)
                    }
                }
            })
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

    private fun checkForDataInView(): Boolean {
        if (businessLayoutIDs.isNotEmpty()) {
            val ids = businessLayoutIDs[businessLayoutIDs.size - 1]
            val name = fragView.findViewById<EditText>(ids.nameId).text.toString()
            val amount = fragView.findViewById<EditText>(ids.amountId).text.toString()
            if (name.isNotEmpty() && amount.isNotEmpty()) {
                return true
            }
            Toast.makeText(activity, "Please fill above details", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.business_add_include_id -> {
                if (checkForDataInView()) {
                    createBusinessLayout(false, "", "")
                }
            }

            R.id.business_add_save_id -> {
                getDataFromViews()
            }

            R.id.business_add_date_id -> {
                calendarViewDialog()
            }
        }
    }
}
