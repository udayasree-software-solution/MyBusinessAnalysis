package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.UserSignInModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.adapters.EmployeeAdapter
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class EmployeesListFragment : Fragment(), EmployeeAdapter.EmployeeAdapterInterface {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText : TextView
    private lateinit var fabBtn : FloatingActionButton

    private lateinit var progressBox : ProgressBox
    private lateinit var fragmentInterface : EmployeesListFragmentInterface

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            fragmentInterface = context as EmployeesListFragmentInterface
        } catch (e : Exception){
            throw ClassCastException(context.toString().plus(" must implement EmployeesListFragment Interface"))
        }
    }

    companion object {
        fun newInstance() : EmployeesListFragment {
            return EmployeesListFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_employees_list, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        recyclerView = view.findViewById(R.id.frag_employee_recycler_id)
        emptyText = view.findViewById(R.id.frag_employee_empty_id)
        fabBtn = view.findViewById(R.id.frag_employee_fab_id)
        progressBox = ProgressBox(activity!!)

        fabBtn.setOnClickListener {
            fragmentInterface.employeesListListener()
        }

        visibility(true)
        readUsersFromFireBase()
    }

    private fun readUsersFromFireBase() {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.USERS)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    if (dataSnapShot.exists()) {
                        val userModelList = ArrayList<UserSignInModel>()
                        for (element in dataSnapShot.children) {
                            val userModel = element.getValue(UserSignInModel::class.java)
                            if (userModel != null) {
                                userModelList.add(userModel)
                            }
                        }
                        setupRecyclerView(userModelList)
                        progressBox.dismiss()
                        visibility(false)
                    } else {
                        progressBox.dismiss()
                    }
                }
            })
        }
    }

    private fun setupRecyclerView(userModelList : ArrayList<UserSignInModel>) {
        if (userModelList.isNotEmpty()) {
            val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            val adapter = EmployeeAdapter(context!!, userModelList, this)
            recyclerView.layoutManager = layoutManager
            recyclerView.adapter = adapter
            adapter.notifyDataSetChanged()
        }
    }

    private fun visibility(isEmpty : Boolean) {
        if (isEmpty) {
            emptyText.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        }
    }

    override fun employeeAdapterListener(userSignInModel: UserSignInModel) {
        if (!userSignInModel.admin) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.USERS)
                .child(userSignInModel.userMobile)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (element in dataSnapshot.children) {
                            element.ref.removeValue()
                        }
                        progressBox.dismiss()
                    } else {
                        progressBox.dismiss()
                    }
                }
            })
        } else {
            Toast.makeText(activity, "Admin details can't delete", Toast.LENGTH_SHORT).show()
        }
    }

    interface EmployeesListFragmentInterface {
        fun employeesListListener()
    }
}
