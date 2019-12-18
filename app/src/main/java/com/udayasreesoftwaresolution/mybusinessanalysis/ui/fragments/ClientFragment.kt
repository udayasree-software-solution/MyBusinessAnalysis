package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.ClientModel
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.SingleEntityModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.ClientRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.ClientsTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.ClientAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import java.math.BigDecimal


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ClientFragment : Fragment(), View.OnClickListener, ClientAdapter.ClientAdapterInterface {

    private lateinit var progressBox: ProgressBox

    private lateinit var clientRecyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var clientEditText: AutoCompleteTextView
    private lateinit var categoryEditText : AutoCompleteTextView
    private lateinit var clientSave: Button
    private lateinit var clientCancel: Button
    private lateinit var clientFAB: FloatingActionButton
    private lateinit var clientLayout: CardView

    private var isFoundInCategory = false
    private lateinit var categoryList : ArrayList<String>
    private lateinit var clientsTable : ArrayList<ClientsTable>

    companion object {
        fun newInstance() : ClientFragment {
            return ClientFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_client, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        clientRecyclerView = view.findViewById(R.id.frag_client_recycler_view_id)
        clientEditText = view.findViewById(R.id.frag_client_edittext_id)
        categoryEditText = view.findViewById(R.id.frag_category_edittext_id)
        clientSave = view.findViewById(R.id.frag_client_save_id)
        clientCancel = view.findViewById(R.id.frag_client_cancel_id)
        clientFAB = view.findViewById(R.id.frag_client_fab_id)
        clientLayout = view.findViewById(R.id.frag_client_editor_layout)
        emptyText = view.findViewById(R.id.frag_client_empty_id)

        clientSave.setOnClickListener(this)
        clientCancel.setOnClickListener(this)
        clientFAB.setOnClickListener(this)

        clientLayout.visibility = View.GONE
        progressBox = ProgressBox(activity!!)

        if (!AppUtils.isAdminStatus) {
            clientFAB.hide()
        }

        ReadClientTaskAsync().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class ReadClientTaskAsync : AsyncTask<Void, Void, ArrayList<ClientsTable>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            clientsTable = ArrayList()
            categoryList = ArrayList()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<ClientsTable> {
            categoryList = CategoryRepository(activity).queryCategoryNamesList() as ArrayList<String>
            return ClientRepository(activity).queryClientList() as ArrayList<ClientsTable>
        }

        override fun onPostExecute(result: ArrayList<ClientsTable>?) {
            super.onPostExecute(result)
            progressBox.dismiss()
            if (categoryList.isNotEmpty()) {
                setupClientTextView()
            }
            if (result != null && result.isNotEmpty()) {
                val clientList = ArrayList<String>()
                for (value in result) {
                    clientList.add(value.client)
                }
                setupClientTextView(clientList)
                clientsTable.addAll(result)
                val adapter = ClientAdapter(activity!!, result, this@ClientFragment)
                clientRecyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
                clientRecyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
                clientRecyclerView.visibility = View.VISIBLE
                emptyText.visibility = View.GONE
            } else {
                clientRecyclerView.visibility = View.GONE
                emptyText.visibility = View.VISIBLE
            }
        }
    }

    private fun setupClientTextView(clientList: ArrayList<String>) {
        if (clientList.isNotEmpty()) {
            val arrayAdapter =
                ArrayAdapter(activity!!, android.R.layout.select_dialog_item, clientList)
            clientEditText.threshold = 1
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            clientEditText.setAdapter(arrayAdapter)
            clientEditText.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    //_companyName = arrayAdapter.getItem(position)!!
                    //isClientSelected = true
                }
            arrayAdapter.notifyDataSetChanged()
        }
    }

    private fun setupClientTextView() {
        if (categoryList.isNotEmpty()) {
            val categoryAdapter =
                ArrayAdapter(activity!!, android.R.layout.select_dialog_item, categoryList)
            categoryEditText.threshold = 1
            categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            categoryEditText.setAdapter(categoryAdapter)
            categoryAdapter.notifyDataSetChanged()
            categoryEditText.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {}
                override fun onItemSelected(adapter: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    //_categoryName = adapter?.selectedItem.toString()
                    //isCategorySelected = true
                }
            }
        }
    }

    private fun writeClientToFireBase(clientModel: ClientModel) {
        if (AppUtils.networkConnectivityCheck(context!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.CLIENT)
                .push()
                .setValue(clientModel) { error, _ ->
                    if (error == null){
                        readVersionOfChildFromFireBase(FireBaseConstants.CLIENT_VERSION)
                        ClientRepository(activity).insertClient(ClientsTable(clientModel.clientName, clientModel.category))

                        if (!isFoundInCategory) {
                            FirebaseDatabase.getInstance()
                                .getReference(AppUtils.OUTLET_NAME)
                                .child(FireBaseConstants.BUSINESS_CATEGORY)
                                .child(FireBaseConstants.OUTLET_CATEGORY)
                                .push()
                                .setValue(clientModel.category) { derror, _ ->
                                    if (derror == null) {
                                        CategoryRepository(activity!!).insertTask(CategoryTable(FireBaseConstants.OUTLET_CATEGORY,clientModel.category))
                                        readVersionOfChildFromFireBase(FireBaseConstants.BUSINESS_CATEGORY_VERSION)
                                    }
                                }
                        }

                        Handler().postDelayed({
                            progressBox.dismiss()
                            ReadClientTaskAsync().execute()
                        },7000)
                    } else {
                        progressBox.dismiss()
                        Toast.makeText(activity, "Failed! Please try again...", Toast.LENGTH_SHORT).show()
                    }
                }
        } else {
            progressBox.dismiss()
        }
    }

    private fun readVersionOfChildFromFireBase(child : String) {
        if (AppUtils.networkConnectivityCheck(context!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(child)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {progressBox.show()}

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        var version = dataSnapShot.getValue(Double::class.java)!!
                        version += 0.001
                        val bigDecimal = BigDecimal(version).setScale(3, BigDecimal.ROUND_HALF_UP)

                        FirebaseDatabase.getInstance()
                            .getReference(AppUtils.OUTLET_NAME)
                            .child(FireBaseConstants.VERSION)
                            .child(child)
                            .setValue(bigDecimal.toDouble())
                    }
                    progressBox.dismiss()
                }
            })
        } else {
            progressBox.dismiss()
        }
    }

    override fun clientAdapterListener(clientTable: ClientsTable) {
        if (AppUtils.networkConnectivityCheck(context!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.VERSION)
                .child(FireBaseConstants.CLIENT_VERSION)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {progressBox.dismiss()}

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        for (element in dataSnapShot.children) {
                            dataSnapShot.ref.removeValue()
                        }
                        ClientRepository(activity).deleteClient(clientTable)
                        readVersionOfChildFromFireBase(FireBaseConstants.CLIENT_VERSION)
                    } else {
                        progressBox.dismiss()
                    }
                }
            })
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.frag_client_fab_id -> {
                clientFAB.hide()
                clientLayout.animation = AnimationUtils.loadAnimation(activity, R.anim.bottom_to_top)
                clientLayout.visibility = View.VISIBLE
            }

            R.id.frag_client_cancel_id -> {
                clientLayout.visibility = View.GONE
                clientFAB.show()
            }

            R.id.frag_client_save_id -> {
                val client = clientEditText.text.toString()
                val category = categoryEditText.text.toString()
                if (client.isNotEmpty() && category.isNotEmpty()) {
                    progressBox.show()
                    var isFound = false
                    for (value in categoryList) {
                        if (category.toLowerCase() == value.toLowerCase()) {
                            isFoundInCategory = true
                        }
                    }
                    for (element in clientsTable) {
                        if (client.toLowerCase() == element.client.toLowerCase()) {
                            for (value in categoryList) {
                                if (category.toLowerCase() == value.toLowerCase()) {
                                    isFound = true
                                    break
                                }
                            }
                            break
                        }
                    }
                    if (!isFound) {
                        writeClientToFireBase(ClientModel(client, category))
                        clientEditText.setText("")
                        categoryEditText.setText("")
                    } else {
                        progressBox.dismiss()
                        Toast.makeText(activity, "Client already exist", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(activity, "Client and Category must not be empty", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}
