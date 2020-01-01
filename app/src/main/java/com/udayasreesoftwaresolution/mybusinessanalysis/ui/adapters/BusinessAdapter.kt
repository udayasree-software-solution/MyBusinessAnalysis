package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.BusinessTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import kotlin.math.exp

class BusinessAdapter(val context: Context, val businessTable: ArrayList<BusinessTable>) :
    RecyclerView.Adapter<BusinessAdapter.BusinessHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessAdapter.BusinessHolder {
        return BusinessHolder(LayoutInflater.from(this.context).inflate(R.layout.adapter_business, parent, false))
    }

    override fun getItemCount(): Int {
        return businessTable.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BusinessAdapter.BusinessHolder, position: Int) {
        with(businessTable[position]) {
            holder.homeTitle.text = businessName.toUpperCase()
            holder.homeTotal.text =
                "₹ ${java.text.NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(context.resources.configuration)[0]).format(
                    amount
                )}/-"
        }
    }

    inner class BusinessHolder(view: View) : RecyclerView.ViewHolder(view) {
        val homeTitle = view.findViewById<TextView>(R.id.row_home_title_id)
        val homeTotal = view.findViewById<TextView>(R.id.row_home_total_id)

        init {
            val kendaltype = AppUtils.getTypeFace(context, ConstantUtils.KENDALTYPE)
            homeTitle.typeface = kendaltype
            homeTotal.typeface = kendaltype
        }
    }
}