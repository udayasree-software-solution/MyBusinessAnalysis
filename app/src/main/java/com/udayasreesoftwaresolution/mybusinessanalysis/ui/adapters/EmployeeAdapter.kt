package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.UserSignInModel

class EmployeeAdapter(val context: Context, val employeeList: ArrayList<UserSignInModel>, val employeeAdapterInterface : EmployeeAdapterInterface) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeAdapter.EmployeeHolder {
        return EmployeeHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_employee_list, parent, false))
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EmployeeAdapter.EmployeeHolder, position: Int) {
        with(employeeList[position]) {
            if (admin) {
                holder.empName.text = "$userName [Admin]"
            } else {
                holder.empName.text = userName
            }
            holder.empContact.text = userMobile
            holder.empVerification.text = verificationCode
        }
    }

    inner class EmployeeHolder(view : View) : RecyclerView.ViewHolder(view) {
        val deleteBtn = view.findViewById<ImageView>(R.id.row_employee_delete_id)
        val empName = view.findViewById<TextView>(R.id.row_employee_name_id)
        val empContact = view.findViewById<TextView>(R.id.row_employee_mobile_id)
        val empVerification = view.findViewById<TextView>(R.id.row_employee_verificationcode_id)

        init {
            deleteBtn.setOnClickListener {
                removeItemAtPosition(adapterPosition)
                employeeAdapterInterface.employeeAdapterListener(employeeList[adapterPosition])
            }
        }
    }

    private fun removeItemAtPosition(position: Int) {
        employeeList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, employeeList.size)
    }

    interface EmployeeAdapterInterface {
        fun employeeAdapterListener(userSignInModel : UserSignInModel)
    }

}