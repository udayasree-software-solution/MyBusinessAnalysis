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
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PurchaseTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils

class PurchaseAdapter(private val context: Context, private val purchaseList : ArrayList<PurchaseTable>,
                      private val purchaseAdapterInterface : PurchaseAdapterInterface) : RecyclerView.Adapter<PurchaseAdapter.PurchaseHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseAdapter.PurchaseHolder {
        return PurchaseHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_purchase, parent, false))
    }

    override fun getItemCount(): Int {
        return purchaseList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PurchaseAdapter.PurchaseHolder, position: Int) {
        with(purchaseList[position]) {
            holder.dateText.text = dateOfPurchase
            holder.clientText.text = clientName
            holder.billNoText.text = billNo
            holder.billAmountText.text = "₹ ${java.text.NumberFormat.getNumberInstance(
                ConfigurationCompat.getLocales(context.resources.configuration)[0]).format(billAmount.toInt())}/-"
        }
    }

    inner class PurchaseHolder(view : View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val dateText = view.findViewById<TextView>(R.id.row_purchase_date_id)
        val deleteBtn = view.findViewById<ImageView>(R.id.row_purchase_delete_id)
        val clientText = view.findViewById<TextView>(R.id.row_purchase_client_id)
        val billNoText = view.findViewById<TextView>(R.id.row_purchase_billno_id)
        val billAmountText = view.findViewById<TextView>(R.id.row_purchase_amount_id)

        init {
            if (!AppUtils.isAdminStatus) {
                deleteBtn.visibility = View.GONE
            }
            val kendaltype = AppUtils.getTypeFace(context, ConstantUtils.KENDALTYPE)

            clientText.typeface = AppUtils.getTypeFace(context, ConstantUtils.SUNDAPRADA)
            dateText.typeface = kendaltype
            billNoText.typeface = kendaltype
            billAmountText.typeface = kendaltype

            deleteBtn.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            when(v?.id) {
                R.id.row_purchase_delete_id -> {
                    val model = purchaseList[adapterPosition]
                    removeItemAtIndex(adapterPosition)
                    purchaseAdapterInterface.purchaseAdapterListener(model)
                }
            }
        }
    }

    private fun removeItemAtIndex(position : Int) {
        purchaseList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, purchaseList.size)
    }

    interface PurchaseAdapterInterface {
        fun purchaseAdapterListener(purchaseTable : PurchaseTable)
    }
}