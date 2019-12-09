package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.FirebaseDatabase
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.UserSignInModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ImageLoaderUtils


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddEmployeeFragment : Fragment() {

    private lateinit var imageLoaderUtils: ImageLoaderUtils
    private lateinit var appSharedPreference: AppSharedPreference
    private lateinit var progressBox: ProgressBox

    private lateinit var outletBanner: ImageView
    private lateinit var outletLogo: ImageView
    private lateinit var registerLayout: LinearLayout

    private lateinit var employeeName: EditText
    private lateinit var employeeContact: EditText
    private lateinit var employeeOutlet: EditText
    private lateinit var saveEmployee: Button

    private var mOutlet = ""
    private var mOutletKey = ""

    companion object {
        fun newInstance(): AddEmployeeFragment {
            return AddEmployeeFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_employee, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        imageLoaderUtils = ImageLoaderUtils(context!!)
        imageLoaderUtils.setupImageLoader()
        appSharedPreference = AppSharedPreference(context!!)
        progressBox = ProgressBox(activity)

        outletBanner = view.findViewById(R.id.add_employee_banner_id)
        outletLogo = view.findViewById(R.id.add_employee_logo_id)
        registerLayout = view.findViewById(R.id.add_employee_layout_id)

        employeeName = view.findViewById(R.id.add_employee_user_name_id)
        employeeContact = view.findViewById(R.id.add_employee_mobile_id)
        employeeOutlet = view.findViewById(R.id.add_employee_outlet_name_id)
        saveEmployee = view.findViewById(R.id.add_employee_btn_id)

        employeeOutlet.setText(appSharedPreference.getOutletName())

        mOutlet = employeeOutlet.text.toString()
        if (mOutlet.isNotEmpty() && mOutlet.isNotBlank()) {
            val split: List<String> = mOutlet.split(" ")
            for (s in split) {
                mOutletKey += s[0]
            }
        }

        val values = (AppUtils.SCREEN_WIDTH * 0.3).toInt()
        outletLogo.layoutParams.width = values
        outletLogo.layoutParams.height = values

        val margin = (AppUtils.SCREEN_WIDTH * 0.1).toInt()

        setMargins(outletLogo, 0, margin, 0, 0)
        //setMargins(registerLayout, 0, (margin / 2).toInt(), 0, 0)

        imageLoaderUtils.displayImage(appSharedPreference.getOutletBannerUrl()!!, outletBanner)
        imageLoaderUtils.displayRoundImage(appSharedPreference.getOutletLogoUrl()!!, outletLogo)

        saveEmployee.setOnClickListener {
            registerEmployeeToFireBase()
        }
    }

    private fun setMargins(view: View, left: Int, top: Int, right: Int, bottom: Int) {
        if (view.layoutParams is ViewGroup.MarginLayoutParams) {
            val p = view.layoutParams as ViewGroup.MarginLayoutParams
            p.setMargins(left, top, right, bottom)
            view.requestLayout()
        }
    }

    private fun registerEmployeeToFireBase() {
        if (AppUtils.networkConnectivityCheck(context!!)) {
            val mName = employeeName.text.toString()
            val mContact = employeeContact.text.toString()
            val mUserId = AppUtils.fireBaseChildId(mOutletKey)
            val mVerifyCode = AppUtils.randomNumbers().toString()

            if (mName.isNotEmpty() && mContact.isNotEmpty() && mContact.length == 10 && mOutlet.isNotEmpty()
                && mUserId.isNotEmpty() && mVerifyCode.isNotEmpty()) {
                progressBox.show()
                val userSignInModel = UserSignInModel(mUserId, mName, mContact, mOutlet, mVerifyCode, false, false, "")
                FirebaseDatabase.getInstance()
                    .getReference(userSignInModel.userOutlet)
                    .child(FireBaseConstants.USERS)
                    .child(userSignInModel.userMobile)
                    .setValue(userSignInModel) { error, _ ->
                        progressBox.dismiss()
                        if (error == null) {
                            employeeName.setText("")
                            employeeContact.setText("")
                            Toast.makeText(activity, "Successfully created employee", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(activity, "Fail to create user. Please try again", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                if (mName.isEmpty()) {
                    employeeName.error = "Enter Employee Name"
                }
                if (mContact.isEmpty() || mContact.length < 10) {
                    employeeContact.error = "Enter Valid Number"
                }
                if (mOutlet.isEmpty()) {
                    employeeOutlet.error = "Select Outlet from option"
                }
            }
        }
    }
}
