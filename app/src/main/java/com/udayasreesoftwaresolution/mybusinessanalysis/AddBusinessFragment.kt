package com.udayasreesoftwaresolution.mybusinessanalysis


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.BusinessRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
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
    private lateinit var progressBox: ProgressBox

    private var isModifyBusiness = false
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
        val view = inflater.inflate(R.layout.fragment_add_business, container, false)
        initView(view)
        return view
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

        val currentDateFormat = AppUtils.getCurrentDate(true)
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
                val date = simpleDateFormat.format(calendar.time)
                addCalendar.setText(date ?: "")
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
                }
            }
            progressBox.dismiss()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.business_add_include_id -> {

            }

            R.id.business_add_save_id -> {

            }

            R.id.business_add_date_id -> {
                calendarViewDialog()
            }
        }
    }
}
