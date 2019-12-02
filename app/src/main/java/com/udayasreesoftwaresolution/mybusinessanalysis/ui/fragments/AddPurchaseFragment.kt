package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.app.DatePickerDialog
import android.os.Bundle
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
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.PurchaseModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PaymentRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PurchaseRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PurchaseTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "clients_name"
private const val ARG_PARAM2 = "param2"

class AddPurchaseFragment : Fragment(), View.OnClickListener {

    private lateinit var selectDateText: EditText
    private lateinit var clientAutoText: AutoCompleteTextView
    private lateinit var billNoText: EditText
    private lateinit var purchaseAmountText: EditText

    private lateinit var progressBox : ProgressBox
    private var selectedClient = ""

    companion object {
        fun newInstance(clientsName : ArrayList<String>) : AddPurchaseFragment {
            val fragment = AddPurchaseFragment()
            val bundle = Bundle()
            bundle.putStringArrayList(ARG_PARAM1, clientsName)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_purchase, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        selectDateText = view.findViewById(R.id.frag_purchase_date_id)
        clientAutoText = view.findViewById(R.id.frag_purchase_client_text_id)
        billNoText = view.findViewById(R.id.frag_purchase_billno_text_id)
        purchaseAmountText = view.findViewById(R.id.frag_purchase_amount_text_id)
        selectDateText.setOnClickListener(this)
        view.findViewById<Button>(R.id.frag_purchase_save_btn_id).setOnClickListener(this)

        progressBox = ProgressBox(activity!!)
        selectDateText.setText(AppUtils.getCurrentDate(false))
        getClientsName()
    }

    private fun getClientsName() {
        val args: Bundle? = arguments
        if (args != null) {
            if (args.containsKey(ARG_PARAM1)) {
                setupClientAutoText(args.getStringArrayList(ARG_PARAM1)!!)
            }
        }
    }

    private fun setupClientAutoText(clientsName: ArrayList<String>) {
        if (clientsName.isNotEmpty()) {
            val arrayAdapter =
                ArrayAdapter(activity!!, android.R.layout.select_dialog_item, clientsName)
            clientAutoText.threshold = 1
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            clientAutoText.setAdapter(arrayAdapter)

            clientAutoText.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    selectedClient = arrayAdapter.getItem(position)!!
                }
        }
    }

    private fun calendarViewDialog() {
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
        val datePickerDialog: DatePickerDialog.OnDateSetListener =
            DatePickerDialog.OnDateSetListener { _, _, _, _ ->

                val simpleDateFormat = SimpleDateFormat(ConstantUtils.DATE_FORMAT, Locale.US)
                selectDateText.setText(simpleDateFormat.format(calendar.time) ?: "")
            }

        val datePicker = DatePickerDialog(
            context!!, datePickerDialog, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = Date().time
        datePicker.show()
    }

    private fun readVersionOfChildFromFireBase(purchaseTable : PurchaseTable) {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(FireBaseConstants.PURCHASE_VERSION)

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

                        FirebaseDatabase.getInstance()
                        fireBaseReference
                            .setValue(bigDecimal.toDouble()) { error, _ ->
                                if (error == null) {
                                    PurchaseRepository(activity).insertPurchase(purchaseTable)
                                }
                            }
                    }
                }
            })
        }
    }

    private fun writePurchaseDetailsToFireBase() {
        if (AppUtils.networkConnectivityCheck(activity!!)) {
            val dateInFormat = selectDateText.text.toString()
            val billNo = billNoText.text.toString()
            val billAmount = purchaseAmountText.text.toString()
            val timeInMillis : Long = AppUtils.timeInMillis
            //selectedClient = clientAutoText.text.toString()
            if (dateInFormat.isNotEmpty() && selectedClient.isNotEmpty() && billNo.isNotEmpty() &&
                billAmount.isNotEmpty() && timeInMillis > 0L) {
                progressBox.show()
                val purchaseDetailsModel = PurchaseModel(timeInMillis, dateInFormat, selectedClient,
                    billNo, billAmount)
                val purchaseTable = PurchaseTable(dateInFormat, selectedClient, billNo, billAmount, timeInMillis)
                FirebaseDatabase.getInstance()
                    .getReference(AppUtils.OUTLET_NAME)
                    .child(FireBaseConstants.PURCHASE)
                    .child(selectedClient)
                    .child(timeInMillis.toString())
                    .setValue(purchaseDetailsModel) {error, _ ->
                        if (error == null) {
                            readVersionOfChildFromFireBase(purchaseTable)
                            selectDateText.setText(AppUtils.getCurrentDate(false))
                            clientAutoText.setText("")
                            billNoText.setText("")
                            purchaseAmountText.setText("")
                        }
                        progressBox.dismiss()
                    }
            } else {
                if (dateInFormat.isEmpty() || dateInFormat.isBlank()) {
                    selectDateText.error = "Please select date"
                }

                if (selectedClient.isEmpty() || selectedClient.isBlank()) {
                    clientAutoText.error = "Please select client"
                    clientAutoText.setText("")
                }

                if (billNo.isEmpty() && billNo.isBlank()) {
                    billNoText.error = "Please enter bill number"
                }

                if (billAmount.isEmpty() || billAmount.isBlank()) {
                    purchaseAmountText.error = "Please enter Purchase Amount"
                }
                progressBox.dismiss()
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.frag_purchase_save_btn_id -> {
                writePurchaseDetailsToFireBase()
            }

            R.id.frag_purchase_date_id -> {
                calendarViewDialog()
            }
        }
    }

}
