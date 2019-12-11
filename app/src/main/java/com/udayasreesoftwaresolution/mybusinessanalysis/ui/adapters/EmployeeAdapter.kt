package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.UserSignInModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils

class EmployeeAdapter(val context: Context, var employeeList: ArrayList<UserSignInModel>, val employeeAdapterInterface : EmployeeAdapterInterface) :
    RecyclerView.Adapter<EmployeeAdapter.EmployeeHolder>() {

    init {
        val list = ArrayList<UserSignInModel>()
        list.addAll(employeeList)
        employeeList.clear()
        for (element in list) {
            if (element.loginUserType == ConstantUtils.isAdminAccess) {
                employeeList.add(element)
            }
        }
        for (values in list) {
            if (values.loginUserType != ConstantUtils.isAdminAccess) {
                employeeList.add(values)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmployeeAdapter.EmployeeHolder {
        return EmployeeHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_employee_list, parent, false))
    }

    override fun getItemCount(): Int {
        return employeeList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: EmployeeAdapter.EmployeeHolder, position: Int) {
        with(employeeList[position]) {
            if (loginUserType == "ADMIN_ACCESS") {
                holder.deleteBtn.visibility = View.GONE
                holder.adminBtn.visibility = View.VISIBLE
            } else {
                holder.deleteBtn.visibility = View.VISIBLE
                holder.adminBtn.visibility = View.GONE
            }
            holder.empName.text = userName
            holder.empContact.text = userMobile
            holder.empVerification.text = verificationCode

            holder.deleteBtn.setOnClickListener {
                val popup = PopupMenu(context, holder.deleteBtn)
                popup.inflate(R.menu.employee_menu)
                popup.setOnMenuItemClickListener { menu ->
                    when (menu?.itemId) {
                        R.id.employee_menu_edit -> {
                            employeeAdapterInterface.employeeAdapterListener(true, employeeList[position])
                        }

                        R.id.employee_menu_delete -> {
                            employeeAdapterInterface.employeeAdapterListener(false, employeeList[position])
                            removeItemAtPosition(position)
                        }
                    }
                    false
                }
                popup.show()
            }
        }
    }

    inner class EmployeeHolder(view : View) : RecyclerView.ViewHolder(view) {
        val deleteBtn = view.findViewById<ImageView>(R.id.row_employee_menu_id)
        val adminBtn = view.findViewById<ImageView>(R.id.row_employee_admin_id)
        val empName = view.findViewById<TextView>(R.id.row_employee_name_id)
        val empContact = view.findViewById<TextView>(R.id.row_employee_mobile_id)
        val empVerification = view.findViewById<TextView>(R.id.row_employee_verificationcode_id)

        /*init {
            deleteBtn.setOnClickListener {
                if (employeeList.isNotEmpty() && employeeList[adapterPosition].loginUserType != ConstantUtils.isAdminAccess) {

                } else {
                    Toast.makeText(context, "Admin details can't delete", Toast.LENGTH_SHORT).show()
                }
            }
        }*/
    }

    private fun removeItemAtPosition(position: Int) {
        employeeList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, employeeList.size)
    }

    interface EmployeeAdapterInterface {
        fun employeeAdapterListener(isModify : Boolean, userSignInModel : UserSignInModel)
    }

}