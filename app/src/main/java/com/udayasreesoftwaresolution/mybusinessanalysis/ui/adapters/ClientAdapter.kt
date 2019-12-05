package com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable

class ClientAdapter(val context: Context, val clientList : ArrayList<ClientsTable>, val clientAdapterInterface: ClientAdapterInterface)
    : RecyclerView.Adapter<ClientAdapter.ClientHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClientAdapter.ClientHolder {
        return ClientHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_client, parent, false))
    }

    override fun getItemCount(): Int {
        return clientList.size
    }

    override fun onBindViewHolder(holder: ClientAdapter.ClientHolder, position: Int) {
        with(clientList[position]) {
            holder.clientText.text = client
            holder.categoryText.text = category
        }
    }

    inner class ClientHolder(view : View) : RecyclerView.ViewHolder(view) {
        val clientText = view.findViewById<TextView>(R.id.row_client_text_id)
        val categoryText = view.findViewById<TextView>(R.id.row_client_category_text_id)
        val delete = view.findViewById<ImageView>(R.id.row_client_delete_id)
        init {
            delete.setOnClickListener {
                clientAdapterInterface.clientAdapterListener(clientList[adapterPosition])
            }
        }
    }

    interface ClientAdapterInterface {
        fun clientAdapterListener(clientTable: ClientsTable)
    }
}