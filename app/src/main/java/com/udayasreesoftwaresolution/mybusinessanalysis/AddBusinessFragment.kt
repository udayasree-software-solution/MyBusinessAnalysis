package com.udayasreesoftwaresolution.mybusinessanalysis


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.BusinessModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.BusinessRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountViewModel
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.BusinessViewIds
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@SuppressLint("StaticFieldLeak")
class AddBusinessFragment : Fragment(), View.OnClickListener {

    private lateinit var addCalendar: EditText
    private lateinit var includeLayout: LinearLayout
    private lateinit var insertBtn: ImageView
    private lateinit var saveBtn: Button
    private lateinit var fragView : View
    private lateinit var progressBox: ProgressBox

    private var isModifyBusiness = false

    private var selectedDate = ""
    private var timeInMillis = 0L

    private lateinit var businessLayoutIDs: ArrayList<BusinessViewIds>
    private lateinit var editBusinessList : ArrayList<AmountViewModel>

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
        addCalendar = view.findViewById(R.id.business_add_date_id)
        includeLayout = view.findViewById(R.id.business_add_insert_id)
        insertBtn = view.findViewById(R.id.business_add_include_id)
        saveBtn = view.findViewById(R.id.business_add_save_id)
        progressBox = ProgressBox(activity)

        insertBtn.setOnClickListener(this)
        saveBtn.setOnClickListener(this)
        addCalendar.setOnClickListener(this)

        businessLayoutIDs = ArrayList()
        editBusinessList = ArrayList()

        val currentDateFormat = AppUtils.getCurrentDate(true)
        selectedDate = currentDateFormat
        timeInMillis = AppUtils.timeInMillis
        addCalendar.setText(currentDateFormat)
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
                addCalendar.setText(selectedDate)
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
                for (element in result) {
                    isModifyBusiness = true
                    createBusinessLayout(true, element.businessName, element.amount.toString())
                }
            } else {
                isModifyBusiness = false
                CategoryListTask().execute()
            }
            progressBox.dismiss()
        }
    }

    private inner class CategoryListTask : AsyncTask<Void, Void, ArrayList<CategoryTable>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }
        override fun doInBackground(vararg p0: Void?): ArrayList<CategoryTable> {
            return CategoryRepository(activity).queryClientNamesList() as ArrayList<CategoryTable>
        }

        override fun onPostExecute(result: ArrayList<CategoryTable>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                for (element in result) {
                    createBusinessLayout(true, element.category_name, "")
                }
            }
            progressBox.dismiss()
        }
    }

    private fun createBusinessLayout(isFirst: Boolean, categoryName : String, categoryAmount : String) {
        val parentId = View.generateViewId()
        val deleteId = View.generateViewId()
        val nameId = View.generateViewId()
        val amountId = View.generateViewId()
        val parentChildId = View.generateViewId()
        businessLayoutIDs.add(BusinessViewIds(parentId, deleteId, nameId, amountId))


        val parentLayout = RelativeLayout(activity)
        parentLayout.layoutParams =
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        parentLayout.id = parentId

        val deleteRow = ImageView(activity)
        val params = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.ALIGN_PARENT_END)
        deleteRow.layoutParams = params
        deleteRow.setImageDrawable(ContextCompat.getDrawable(context!!, android.R.drawable.ic_delete))
        deleteRow.setBackgroundColor(Color.BLACK)
        deleteRow.id = deleteId

        val parentChildLayout = LinearLayout(activity)
        val layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
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
                    setTextColor(ContextCompat.getColor(context, R.color.colorAccent))
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
        amountEditText.setText(if (categoryAmount.isNotEmpty()){categoryAmount} else {""})
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
                    if (i > 0) {
                        if (view.id == ids.deleteId) {
                            includeLayout.removeView(view.findViewById(ids.parentId))
                            businessLayoutIDs.removeAt(i)
                            break
                        }
                    }
                }
            }
        }
    }

    private fun writeBusinessToFireBase() {
        if (AppUtils.networkConnectivityCheck(context!!)) {
            for (count in 0 until businessLayoutIDs.size) {
                with(businessLayoutIDs[count]) {
                    val name = fragView.findViewById<EditText>(nameId).text.toString()
                    val amount = fragView.findViewById<EditText>(amountId).text.toString()
                    if (name.isNotEmpty() && amount.isNotEmpty()) {
                        val businessModel = BusinessModel(count, name, amount.toInt(), selectedDate, timeInMillis)
                        val businessTable = BusinessTable(count, amount.toInt(), name, selectedDate, timeInMillis)
                        FirebaseDatabase.getInstance()
                            .getReference(AppUtils.OUTLET_NAME)
                            .child(FireBaseConstants.BUSINESS)
                            .child(selectedDate)
                            .setValue(businessModel) { error, _ ->
                                if (error == null){
                                    BusinessRepository(activity).insertBusiness(businessTable)
                                }
                            }
                    }
                }
            }
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
            return false
        }
        return true
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.business_add_include_id -> {
                if (checkForDataInView()) {
                    createBusinessLayout(false, "", "")
                }
            }

            R.id.business_add_save_id -> {
                writeBusinessToFireBase()
            }

            R.id.business_add_date_id -> {
                calendarViewDialog()
            }
        }
    }
}
