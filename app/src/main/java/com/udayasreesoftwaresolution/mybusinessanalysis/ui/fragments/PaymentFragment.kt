package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.PaymentModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressDialog
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PaymentRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.PaymentAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@SuppressLint("StaticFieldLeak")
class PaymentFragment : Fragment(), View.OnClickListener, PaymentAdapter.TaskInterface {

    private lateinit var cardView: CardView
    private lateinit var payableLayout: FrameLayout
    private lateinit var paidLayout: FrameLayout
    private lateinit var paymentRecycler: RecyclerView
    private lateinit var paymentEmptyText: TextView
    private lateinit var payableText: TextView
    private lateinit var paidText: TextView
    private lateinit var paymentFAB: FloatingActionButton
    private lateinit var animation: Animation
    private lateinit var progressBox: ProgressDialog
    private lateinit var paymentInterface: PaymentInterface

    private lateinit var editPaymentTable : PaymentTable

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            paymentInterface = context as PaymentInterface
        } catch (e: ClassCastException) {
            throw ClassCastException(context.toString().plus(" must implement PaymentFragment"))
        }
    }

    companion object {
        fun getInstance(): Fragment {
            return PaymentFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_payment, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        payableLayout = view.findViewById(R.id.frag_payment_payable_id)
        paidLayout = view.findViewById(R.id.frag_payment_paid_id)
        paymentRecycler = view.findViewById(R.id.frag_payment_recycler_id)
        paymentEmptyText = view.findViewById(R.id.frag_payment_empty_id)
        payableText = view.findViewById(R.id.frag_payment_payable_text)
        paidText = view.findViewById(R.id.frag_payment_paid_text)
        paymentFAB = view.findViewById(R.id.frag_payment_fab_id)
        cardView = view.findViewById(R.id.frag_payment_card_id)

        cardView.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.12).toInt()
        progressBox = ProgressDialog(activity)

        paymentRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && paymentFAB.isShown) {
                    paymentFAB.hide()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    paymentFAB.show()
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        payableLayout.setOnClickListener(this)
        paidLayout.setOnClickListener(this)
        paymentFAB.setOnClickListener(this)

        if (!AppUtils.isAdminStatus) {
            paymentFAB.hide()
        }

        GetTaskListAsync(false).execute()
    }

    private fun selectedColor(isPaid: Boolean) {
        if (isPaid) {
            paidLayout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
            paidText.setTextColor(ContextCompat.getColor(activity!!, R.color.window_background))

            payableLayout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.window_background))
            payableText.setTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
        } else {
            payableLayout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
            payableText.setTextColor(ContextCompat.getColor(activity!!, R.color.window_background))

            paidLayout.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.window_background))
            paidText.setTextColor(ContextCompat.getColor(activity!!, R.color.colorAccent))
        }
    }

    inner class GetTaskListAsync(private val status: Boolean) : AsyncTask<Void, Void, List<PaymentTable>>() {
        override fun onPreExecute() {
            super.onPreExecute()
            animation = if (status) {
                AnimationUtils.loadAnimation(activity, R.anim.left_side)
            } else {
                AnimationUtils.loadAnimation(activity, R.anim.right_side)
            }
            selectedColor(status)
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): List<PaymentTable> {
            return PaymentRepository(activity).queryPaymentDetail(status) as ArrayList<PaymentTable>
        }

        override fun onPostExecute(result: List<PaymentTable>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                paymentRecycler.visibility = View.VISIBLE
                paymentEmptyText.visibility = View.GONE
                val layoutManager =
                    LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                paymentRecycler.layoutManager = layoutManager

                val adapter = PaymentAdapter(activity!!,
                    result as ArrayList<PaymentTable>,
                    this@PaymentFragment
                )
                paymentRecycler.adapter = adapter
                adapter.notifyDataSetChanged()
                paymentRecycler.startAnimation(animation)
            } else {
                paymentRecycler.visibility = View.GONE
                paymentEmptyText.visibility = View.VISIBLE
                paymentEmptyText.startAnimation(animation)
            }
            progressBox.dismiss()
        }
    }

    inner class MenuTaskAsync(private val slNo: Int, private val status: Int) : AsyncTask<Void, Void, PaymentTable>() {
        override fun doInBackground(vararg p0: Void?): PaymentTable {
            return PaymentRepository(activity).queryPaymentBySlNo(slNo) as PaymentTable
        }

        override fun onPostExecute(result: PaymentTable?) {
            super.onPostExecute(result)
            if (result != null) {
                editPaymentTable = result
                when (status) {
                    0 -> {
                        // payed
                        calendarViewDialog()
                    }

                    2 -> {
                        // delete
                        deletePaymentInFireBase()
                    }
                }
            }
        }
    }

    private fun deletePaymentInFireBase() {
        if (AppUtils.networkConnectivityCheck(activity!!)) {
            progressBox.show()
            if (AppUtils.OUTLET_NAME.isNotEmpty()
                && AppUtils.OUTLET_NAME.isNotBlank() && AppUtils.OUTLET_NAME != "NA"
            ) {
                val fireBaseReference = FirebaseDatabase.getInstance()
                    .getReference(AppUtils.OUTLET_NAME)
                    .child(FireBaseConstants.PAYMENT)
                    .child(editPaymentTable.uniqueKey)

                fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        progressBox.dismiss()
                    }

                    override fun onDataChange(dataSnapShot: DataSnapshot) {
                        if (dataSnapShot.exists()) {
                            for (element in dataSnapShot.children) {
                                element.ref.removeValue()
                            }
                            readAmountFromFireBase(editPaymentTable.payAmount.toInt())
                            PaymentRepository(activity!!).deleteTask(editPaymentTable)
                            progressBox.dismiss()
                        } else {
                            progressBox.dismiss()
                        }
                    }
                })
            }
        }
    }

    private fun readAmountFromFireBase(pay : Int) {
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
                        val payable = dataSnapShot.child(FireBaseConstants.PAYABLE_AMOUNT).getValue(Int::class.java)!!
                        FirebaseDatabase.getInstance()
                            .getReference(AppUtils.OUTLET_NAME)
                            .child(FireBaseConstants.TOTAL_AMOUNT)
                            .child(FireBaseConstants.PAYABLE_AMOUNT)
                            .setValue((payable - pay))
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

                val simpleDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
                val payedDate = simpleDateFormat.format(calendar.time)
                with(editPaymentTable) {
                    writePaymentToFireBase(PaymentModel(uniqueKey, clientName.plus("[$payedDate]"),
                        payAmount, chequeNumber, dateInMillis, true, preDays))
                }
                editPaymentTable.paymentStatus = true
                editPaymentTable.clientName = editPaymentTable.clientName.plus(" [$payedDate]")
            }

        val datePicker = DatePickerDialog(
            activity!!, datePickerDialog, calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.maxDate = Date().time
        datePicker.show()
    }

    private fun writePaymentToFireBase(paymentModel: PaymentModel) {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.PAYMENT)
                .child(paymentModel.uniqueKey)
                .setValue(paymentModel) {error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase()
                    }
                }
        }
    }

    private fun readVersionOfChildFromFireBase() {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(FireBaseConstants.PAYMENT_VERSION)

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
                                    PaymentRepository(activity!!).updateTask(editPaymentTable)
                                    GetTaskListAsync(false).execute()
                                }
                            }
                    }
                }
            })
        }
    }

    /*TODO: Interface Implementation*/
    override fun menuTaskAction(slNo: Int, status: Int) {
        when (status) {
            1 -> {
                paymentInterface.paymentActionListener(slNo)
            }
            else -> {
                MenuTaskAsync(slNo, status).execute()
            }
        }
    }

    /*TODO: Action Listener*/
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.frag_payment_payable_id -> {
                GetTaskListAsync(false).execute()
            }

            R.id.frag_payment_paid_id -> {
                GetTaskListAsync(true).execute()
            }

            R.id.frag_payment_fab_id -> {
                paymentInterface.paymentActionListener(-1)
            }
        }
    }

    interface PaymentInterface {
        fun paymentActionListener(slNo: Int)
    }
}
