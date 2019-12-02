package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.ClientRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PurchaseRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PurchaseTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.PurchaseAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import java.math.BigDecimal


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
@SuppressLint("StaticFieldLeak")
class PurchaseFragment : Fragment(), View.OnClickListener, PurchaseAdapter.PurchaseAdapterInterface {

    private lateinit var clientSpinner: Spinner
    private lateinit var purchaseRecycler: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var addPurchaseFAB: FloatingActionButton
    private lateinit var progressBox: ProgressBox
    private lateinit var clientsName: ArrayList<String>
    private lateinit var purchaseInterface: PurchaseInterface

    companion object {
        fun newInstance() : PurchaseFragment {
            return PurchaseFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            purchaseInterface = context as PurchaseInterface
        } catch (e : Exception){
            throw ClassCastException(context.toString().plus(" must implement PurchaseFragment Interface"))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_purchase, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        clientSpinner = view.findViewById(R.id.frag_purchase_client_spinner_id)
        purchaseRecycler = view.findViewById(R.id.frag_purchase_recycler_id)
        emptyText = view.findViewById(R.id.frag_purchase_empty_text_id)
        addPurchaseFAB = view.findViewById(R.id.frag_purchase_fab_btn_id)
        addPurchaseFAB.setOnClickListener(this)
        if (!AppUtils.isAdminStatus) {
            addPurchaseFAB.hide()
        }
        progressBox = ProgressBox(activity!!)
        ReadClientTaskAsync().execute()
    }

    inner class ReadClientTaskAsync : AsyncTask<Void, Void, ArrayList<String>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            clientsName = ArrayList()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<String> {
            clientsName = ClientRepository(activity).queryClientNamesList() as ArrayList<String>
            return clientsName
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            super.onPostExecute(result)
            progressBox.dismiss()
            if (clientsName.isNotEmpty()) {
                ReadPurchaseTaskAsync(clientsName[0]).execute()
                purchaseDataEmpty(false)
            } else {
                clientsName.add("Select Client")
                purchaseDataEmpty(true)
            }
            setupClientSpinner(clientsName)
        }
    }

    inner class ReadPurchaseTaskAsync(val client: String) : AsyncTask<Void, Void, ArrayList<PurchaseTable>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<PurchaseTable> {
            return PurchaseRepository(activity).queryPurchaseList(client) as ArrayList<PurchaseTable>
        }

        override fun onPostExecute(result: ArrayList<PurchaseTable>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                purchaseDataEmpty(false)
                setupRecyclerView(result)
            } else {
                purchaseDataEmpty(true)
            }
            progressBox.dismiss()
        }
    }

    private fun setupRecyclerView(purchaseModelList : ArrayList<PurchaseTable>){
        val layoutManager = LinearLayoutManager(activity!!, RecyclerView.VERTICAL, false)
        val adapter = PurchaseAdapter(activity!!, purchaseModelList, this)
        purchaseRecycler.layoutManager = layoutManager
        purchaseRecycler.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun setupClientSpinner(clientsName: ArrayList<String>) {
        val arrayAdapter =
            ArrayAdapter(activity!!, android.R.layout.simple_spinner_item, clientsName)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        clientSpinner.adapter = arrayAdapter

        clientSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                ReadPurchaseTaskAsync(arrayAdapter.getItem(position) ?: "").execute()            }

        }
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
                                    PurchaseRepository(activity).deletePurchase(purchaseTable)
                                }
                            }
                    }
                }
            })
        }
    }

    override fun purchaseAdapterListener(purchaseTable : PurchaseTable) {
        val fireBaseReference = FirebaseDatabase.getInstance()
            .getReference(AppUtils.OUTLET_NAME)
            .child(FireBaseConstants.PURCHASE)
            .child(purchaseTable.clientName)
            .child(purchaseTable.timeInMillis.toString())

        fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (element in dataSnapshot.children) {
                        element.ref.removeValue { error, _ ->
                            if (error == null) {
                                readVersionOfChildFromFireBase(purchaseTable)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun purchaseDataEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            emptyText.visibility = View.VISIBLE
            purchaseRecycler.visibility = View.GONE
        } else {
            emptyText.visibility = View.GONE
            purchaseRecycler.visibility = View.VISIBLE
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.frag_purchase_fab_btn_id -> {
                if (clientsName.isNotEmpty()) {
                    purchaseInterface.addPurchaseListener(clientsName)
                }
            }
        }
    }

    interface PurchaseInterface {
        fun addPurchaseListener(clientsName: ArrayList<String>)
    }
}
