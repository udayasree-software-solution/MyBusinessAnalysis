package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel

class AddBusinessAdapter(val context : Context, private val businessTableList : ArrayList<BusinessTable>)
    : RecyclerView.Adapter<AddBusinessAdapter.AddBusinessHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddBusinessAdapter.AddBusinessHolder {
        return AddBusinessHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_add_business,
            parent, false))
    }

    override fun getItemCount(): Int {
        return businessTableList.size
    }

    override fun onBindViewHolder(holder: AddBusinessAdapter.AddBusinessHolder, position: Int) {
        with(businessTableList[position]) {
            holder.nameEdit.setText(businessName)
            if (amount > 0) {
                holder.amountEdit.setText(amount.toString())
            }
        }
        holder.amountEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s.toString()
                businessTableList[position].amount = if (text.isEmpty()){0}else{text.toInt()}
            }
        })
    }

    inner class AddBusinessHolder(view : View) : RecyclerView.ViewHolder(view) {
        val nameEdit = view.findViewById<EditText>(R.id.row_add_business_name_id)
        val amountEdit = view.findViewById<EditText>(R.id.row_add_business_amount_id)
    }
}