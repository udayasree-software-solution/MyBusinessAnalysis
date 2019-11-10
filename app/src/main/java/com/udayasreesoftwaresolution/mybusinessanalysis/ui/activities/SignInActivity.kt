package com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.SingleEntityModel
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.UserSignInModel
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.SharedPreferenceUtils

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginLayout: LinearLayout
    private lateinit var loginUserName: EditText
    private lateinit var loginMobile: EditText
    private lateinit var loginOutletName: AutoCompleteTextView
    private lateinit var loginBtn: Button

    private lateinit var sharedPreferenceUtils : SharedPreferenceUtils
    private lateinit var progressBox : ProgressBox

    private var outletName = ""
    private var outletCode = ""
    private var isDialogVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        if (AppUtils.networkConnectivityCheck(this)) {
            initView()
        } else {
            exitDialog("Internet Connectivity", "Required internet connectivity to continue", "Exit")
        }
    }

    private fun exitDialog(title: String, message: String, posBtn: String) {
        val builder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(posBtn) { dialog, _ ->
                dialog.dismiss()
                finishAffinity()
            }
        builder.create().show()
    }

    private fun initView() {
        supportActionBar?.hide()
        loginLayout = findViewById(R.id.login_layout_id)
        loginUserName = findViewById(R.id.login_user_name_id)
        loginMobile = findViewById(R.id.login_mobile_id)
        loginOutletName = findViewById(R.id.login_outlet_name_id)
        loginBtn = findViewById(R.id.login_login_btn_id)
        findViewById<TextView>(R.id.login_title_id).typeface = AppUtils.getTypeFace(this, ConstantUtils.SUNDAPRADA)

        loginLayout.layoutParams.width = (AppUtils.SCREEN_WIDTH * 0.80).toInt()
        loginLayout.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.80).toInt()

        loginBtn.setOnClickListener(this)

        progressBox = ProgressBox.create(this)

        readOutletFromFireBase()
    }

    private fun readOutletFromFireBase() {
        if (AppUtils.networkConnectivityCheck(this)) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(ConstantUtils.ADMIN)
                .child(FireBaseConstants.OUTLET)

            fireBaseReference.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(dataSnapShot: DataSnapshot) {
                    val outletList = ArrayList<SingleEntityModel>()
                    for(ds in dataSnapShot.children) {
                        outletList.add(ds.getValue(SingleEntityModel::class.java)!!)
                    }
                    val outletName = ArrayList<String>()
                    for (element in outletList) {
                        outletName.add(element.inputData)
                    }
                    setupOutletTextView(outletName)
                    progressBox.dismiss()
                }
            })
        }
    }

    private fun setupOutletTextView(outletNames : List<String>?) {
        if (outletNames != null && outletNames.isNotEmpty()) {
            val arrayAdapter = ArrayAdapter(this, android.R.layout.select_dialog_item, outletNames)
            loginOutletName.threshold = 1
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            loginOutletName.setAdapter(arrayAdapter)

            loginOutletName.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    outletName = arrayAdapter.getItem(position)!!
                    if (outletName.isNotEmpty() && outletName.isNotBlank()) {
                        val split : List<String> = outletName.split(" ")
                        for (s in split) {
                            outletCode += s[0]
                        }
                    }
                }
        }
    }

    private fun readUserFromFireBase(userSignInModel: UserSignInModel) {
        if (AppUtils.networkConnectivityCheck(this)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(userSignInModel.userOutlet)
                .child(FireBaseConstants.USERS)
                .child(userSignInModel.userMobile)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    if (snapShot.exists()) {
                        val model = snapShot.getValue(UserSignInModel::class.java)
                        if (model != null) {
                            val sharedPreferenceUtils =
                                SharedPreferenceUtils(this@SignInActivity).getInstance()
                            with(model) {
                                if (userSignInModel.userOutlet == userOutlet && userSignInModel.userName == userName) {
                                    sharedPreferenceUtils.setUserName(userName)
                                    sharedPreferenceUtils.setMobileNumber(userMobile)
                                    sharedPreferenceUtils.setOutletName(userOutlet)
                                    sharedPreferenceUtils.setSignInCode(verificationCode)
                                    sharedPreferenceUtils.setUserFireBaseChildId(userId)
                                    sharedPreferenceUtils.setAdminStatus(admin)

                                    loginUserName.setText("")
                                    loginMobile.setText("")
                                    loginOutletName.setText("")
                                    progressBox.dismiss()
                                    sharedPreferenceUtils.setUserSignInStatus(true)
                                    setResult(Activity.RESULT_OK)
                                } else {
                                    Toast.makeText(this@SignInActivity, "User details doesn't match", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this@SignInActivity, "New user creating", Toast.LENGTH_SHORT).show()
                            writeToFireBase(userSignInModel)
                        }
                    } else {
                        writeToFireBase(userSignInModel)
                    }
                }
            })
        }
    }

    private fun writeToFireBase(userSignInModel: UserSignInModel) {
        if (AppUtils.networkConnectivityCheck(this)) {
            with(userSignInModel) {
                FirebaseDatabase.getInstance()
                    .getReference(userSignInModel.userOutlet)
                    .child(FireBaseConstants.USERS)
                    .child(userSignInModel.userMobile)
                    .setValue(userSignInModel) { error, _ ->
                        if (error == null) {
                            readUserFromFireBase(userSignInModel)
                        } else {
                            progressBox.dismiss()
                            Toast.makeText(
                                this@SignInActivity,
                                "Fail to create user. Please try again",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    }
            }
        }
    }

    private fun signInValidation() {
        if (AppUtils.networkConnectivityCheck(this)) {
            val userName = loginUserName.text.toString()
            val userMobile = loginMobile.text.toString()
            val userId = AppUtils.fireBaseChildId(outletCode)
            val verificationCode = AppUtils.randomNumbers().toString()
            val outletNameFetch = loginOutletName.text.toString()
            if (outletNameFetch != outletName) {
                loginOutletName.setText("")
                outletName = ""
            }
            if (userName.isNotEmpty() && userMobile.isNotEmpty() && userMobile.length == 10
                && outletName.isNotEmpty() && outletCode.isNotEmpty() && userId.isNotEmpty() && verificationCode.isNotEmpty()
            ) {
                progressBox.show()
                val userSignInModel =
                    UserSignInModel(
                        userId,
                        userName,
                        userMobile,
                        outletName,
                        verificationCode,
                        false, false
                    )
                readUserFromFireBase(userSignInModel)
            } else {
                if (userName.isEmpty()) {
                    loginUserName.error = "Enter User Name"
                }
                if (userMobile.isEmpty() || userMobile.length < 10) {
                    loginMobile.error = "Enter Valid Number"
                }
                if (outletName.isEmpty()) {
                    loginOutletName.error = "Select valid Outlet"
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.login_login_btn_id -> {
                signInValidation()
            }
        }
    }
}