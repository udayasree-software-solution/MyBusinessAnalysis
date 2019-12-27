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
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.BusinessRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
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

    private lateinit var progressBox : ProgressBox

    private var isModifyBusiness = false
    private var timeInMillis : Long = 0L
    private var selectedDate : String = ""

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
            progressBox.dismiss()
            if (result != null && result.isNotEmpty()) {
                isModifyBusiness = true
                /*TODO: Display data with amount in recyclerview*/
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
            progressBox.dismiss()
            if (result != null && result.isNotEmpty()) {
                /*TODO: Display data with amount in recyclerview*/
            }
        }
    }

    private fun setupRecyclerView() {

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


    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.outlet_business_calender_layout -> {
                calendarViewDialog()
            }

            R.id.outlet_business_previous_id -> {

            }

            R.id.outlet_business_next_id -> {

            }
        }
    }
}
