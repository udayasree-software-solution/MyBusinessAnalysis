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
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.AmountModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import java.text.NumberFormat

@SuppressLint("SetTextI18n")
class AmountAdapter(val context : Context, val amountModelList : ArrayList<AmountModel>)
    : RecyclerView.Adapter<AmountAdapter.HomeHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): HomeHolder {
        return HomeHolder(LayoutInflater.from(this.context).inflate(R.layout.adapter_home, parent, false))
    }

    override fun getItemCount(): Int {
        return amountModelList.size ?: 0
    }

    override fun onBindViewHolder(holder: AmountAdapter.HomeHolder, position: Int) {
        with(amountModelList[position]) {
            holder.homeTitle.text = title.toUpperCase()
            holder.homeTotal.text = "₹ ${NumberFormat.getNumberInstance(ConfigurationCompat.getLocales(context.resources.configuration)[0]).format(total)}/-"
            if (title == "Expenses Amount") {
                holder.homeTitle.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                holder.homeTotal.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
                //holder.homeCardLayout.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light))
            }/* else {
                //holder.homeCardLayout.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_light))
            }*/
        }
    }

    inner class HomeHolder(view : View) : RecyclerView.ViewHolder(view) {
        val homeCardLayout = view.findViewById<CardView>(R.id.row_home_card_layout_id)
        val homeLayout = view.findViewById<LinearLayout>(R.id.row_home_layout_id)
        val homeTitle = view.findViewById<TextView>(R.id.row_home_title_id)
        val homeTotal = view.findViewById<TextView>(R.id.row_home_total_id)
        init {
            val kendaltype = AppUtils.getTypeFace(context, ConstantUtils.KENDALTYPE)
            homeTitle.typeface = kendaltype
            homeTotal.typeface = kendaltype
        }
    }
}