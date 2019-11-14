package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentAdapter(
    private val context: Context,
    private val taskList: ArrayList<PaymentTable>,
    val taskInterface: TaskInterface
) :
    RecyclerView.Adapter<PaymentAdapter.TaskHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TaskHolder {
        return TaskHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_payment, parent, false))
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TaskHolder, position: Int) {
        val model = taskList[position]
        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
        with(model) {
            holder.nameTV.text = clientName
            if (paymentStatus) {
                holder.nameTV.setTextColor(context.resources.getColor(android.R.color.holo_orange_dark))
                if (holder.menuBtn.visibility == View.VISIBLE) {
                    holder.menuBtn.visibility = View.GONE
                }
            } else {
                holder.nameTV.setTextColor(context.resources.getColor(android.R.color.holo_green_dark))
                if (holder.menuBtn.visibility == View.GONE) {
                    holder.menuBtn.visibility = View.VISIBLE
                }
            }
            holder.daysLeft.visibility = View.GONE
            if (!paymentStatus) {
                calendar.set(Calendar.HOUR_OF_DAY, ConstantUtils.HOUR)
                calendar.set(Calendar.MINUTE, ConstantUtils.MINUTE)
                calendar.set(Calendar.SECOND, (ConstantUtils.SECOND - 1))

                val todayMillis = calendar.timeInMillis
                var diff = (java.util.Date(dateInMillis.toLong()).time - Date(todayMillis).time)
                if (diff < 0) {
                    holder.daysLeft.text = "Exceed"
                    holder.daysLeft.visibility = android.view.View.VISIBLE
                } else {
                    var daysLeft = (diff / (1000 * 60 * 60 * 24))

                    if (daysLeft == 0L) {
                        holder.daysLeft.text = "Today"
                        holder.daysLeft.visibility = android.view.View.VISIBLE
                    } else if (daysLeft > 0L) {
                        calendar.timeInMillis = dateInMillis
                        calendar.add(Calendar.DATE, ((dateInMillis * -1).toInt()))

                        diff = (java.util.Date(calendar.timeInMillis).time - java.util.Date(todayMillis).time)
                        daysLeft = (diff / (1000 * 60 * 60 * 24))
                        if (daysLeft <= 0L) {
                            diff = (java.util.Date(dateInMillis.toLong()).time - java.util.Date(todayMillis).time)
                            daysLeft = (diff / (1000 * 60 * 60 * 24))

                            holder.daysLeft.text = "$daysLeft day(s) left"
                            holder.daysLeft.visibility = android.view.View.VISIBLE
                        }
                    }
                }
            }

            holder.dateTV.text = simpleDateFormat.format(Date(dateInMillis.toLong()))
            holder.chequeTV.text = chequeNumber
            holder.amountTV.text = "₹ ${java.text.NumberFormat.getNumberInstance(
                ConfigurationCompat.getLocales(context.resources.configuration)[0]).format(payAmount.toInt())} /-"

            holder.menuBtn.setOnClickListener { view ->
                when (view?.id) {
                    R.id.row_task_company_menu_id -> {
                        val popup = android.widget.PopupMenu(context, holder.menuBtn)
                        popup.inflate(R.menu.payable_menu)
                        popup.setOnMenuItemClickListener { menu ->
                            when (menu?.itemId) {
                                R.id.task_menu_payed_id -> {
                                    taskInterface.menuTaskAction(slNo, 0)
                                }

                                R.id.task_menu_modify_id -> {
                                    taskInterface.menuTaskAction(slNo, 1)
                                }

                                R.id.task_menu_delete_id -> {
                                    taskInterface.menuTaskAction(slNo, 2)
                                    removeItemAtPosition(position)
                                }
                            }
                            false
                        }
                        popup.show()
                    }
                }
            }
        }
    }


    inner class TaskHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTV: TextView = view.findViewById(R.id.row_task_company_name_id)
        val dateTV: TextView = view.findViewById(R.id.row_task_company_date_id)
        val chequeTV: TextView = view.findViewById(R.id.row_task_company_cheque_id)
        val amountTV: TextView = view.findViewById(R.id.row_task_company_amount_id)
        val menuBtn: ImageView = view.findViewById(R.id.row_task_company_menu_id)
        val daysLeft: TextView = view.findViewById(R.id.row_task_days_left_id)

        init {
            val kendaltype = AppUtils.getTypeFace(context, ConstantUtils.KENDALTYPE)

            nameTV.typeface = AppUtils.getTypeFace(context, ConstantUtils.SUNDAPRADA)
            dateTV.typeface = kendaltype
            chequeTV.typeface = kendaltype
            amountTV.typeface = kendaltype
            daysLeft.typeface = kendaltype

        }
    }

    private fun removeItemAtPosition(position: Int) {
        taskList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, taskList.size)
    }

    interface TaskInterface {
        fun menuTaskAction(slNo: Int, status: Int)
    }
}