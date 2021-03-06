package com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
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
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference

class SignInActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var loginLayout: LinearLayout
    private lateinit var loginUserName: EditText
    private lateinit var loginMobile: EditText
    private lateinit var loginOutletName: AutoCompleteTextView
    private lateinit var loginBtn: Button

    private lateinit var verifyAnimLayout : CardView
    private lateinit var verifyEditText : EditText
    private lateinit var verifyButton : TextView

    private lateinit var appSharedPreference : AppSharedPreference
    private lateinit var progressBox : ProgressBox

    private var outletName = ""
    private var outletCode = ""

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
                if (posBtn == "Exit") {
                    finishAffinity()
                }
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

        verifyAnimLayout = findViewById(R.id.login_verify_dialog)
        verifyEditText = findViewById(R.id.login_verify_code_id)
        verifyButton = findViewById(R.id.login_verify_btn_id)

        findViewById<TextView>(R.id.login_title_id).typeface = AppUtils.getTypeFace(this, ConstantUtils.MONTSERRAT)
        appSharedPreference = AppSharedPreference(this)
        //loginLayout.layoutParams.width = (AppUtils.SCREEN_WIDTH * 0.80).toInt()
        //loginLayout.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.80).toInt()

        loginBtn.setOnClickListener(this)
        verifyButton.setOnClickListener(this)
        progressBox = ProgressBox(this)
        verifyAnimLayout.visibility = View.GONE

        readOutletFromFireBase()
    }

    private fun readOutletFromFireBase() {
        if (AppUtils.networkConnectivityCheck(this)) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(FireBaseConstants.ADMIN)
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

    private fun readUserFromFireBase(outlet : String, contact : String, name : String) {
        if (AppUtils.networkConnectivityCheck(this)) {
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(outlet)
                .child(FireBaseConstants.USERS)
                .child(contact)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(snapShot: DataSnapshot) {
                    if (snapShot.exists()) {
                        val model = snapShot.getValue(UserSignInModel::class.java)
                        if (model != null) {
                            val sharedPreferenceUtils =
                                AppSharedPreference(this@SignInActivity)
                            with(model) {
                                if (outlet == userOutlet && name == userName) {

                                    val deviceCode = AppUtils.uniqueKey()
                                    val userId = AppUtils.fireBaseChildId(outletCode)
                                    val verifyCode = if (loginUserType == ConstantUtils.isAdminAccess) {verificationCode} else {AppUtils.randomNumbers().toString()}

                                    sharedPreferenceUtils.setUserName(userName)
                                    sharedPreferenceUtils.setMobileNumber(userMobile)
                                    sharedPreferenceUtils.setOutletName(userOutlet)
                                    sharedPreferenceUtils.setSignInCode(verifyCode)
                                    sharedPreferenceUtils.setUserFireBaseChildId(userId)
                                    sharedPreferenceUtils.setAdminStatus(loginUserType == ConstantUtils.isAdminAccess)
                                    sharedPreferenceUtils.setLoginType(loginUserType)
                                    sharedPreferenceUtils.setLoginDeviceCode(deviceCode)

                                    writeToFireBase(UserSignInModel(userId, userName, userMobile, userOutlet, verifyCode, loginUserType, deviceCode))
                                } else {
                                    progressBox.dismiss()
                                    Toast.makeText(this@SignInActivity, "User details doesn't match", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            progressBox.dismiss()
                            Toast.makeText(this@SignInActivity, "Please provide valid credentials to login", Toast.LENGTH_SHORT).show()
                            //writeToFireBase(userSignInModel)
                        }
                    } else {
                        progressBox.dismiss()
                        exitDialog("Account not found","Please contact your ADMIN to create credentials","Okay")
                        //writeToFireBase(userSignInModel)
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
                        progressBox.dismiss()
                        if (error == null) {
                            //readUserFromFireBase(userSignInModel)
                            verifyAnimLayout.animation = AnimationUtils.loadAnimation(this@SignInActivity, R.anim.bottom_to_top)
                            verifyAnimLayout.visibility = View.VISIBLE
                        } else {
                            Toast.makeText(
                                this@SignInActivity,
                                "Fail to Login. Please try again",
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
            val outletNameFetch = loginOutletName.text.toString()
            if (outletNameFetch != outletName) {
                loginOutletName.setText("")
                outletName = ""
            }
            if (userName.isNotEmpty() && userMobile.isNotEmpty() && userMobile.length == 10
                && outletName.isNotEmpty() && outletCode.isNotEmpty()) {
                progressBox.show()
                readUserFromFireBase(outletName, userMobile, userName)
            } else {
                if (userName.isEmpty()) {
                    loginUserName.error = "Enter User Name"
                }
                if (userMobile.isEmpty() || userMobile.length < 10) {
                    loginMobile.error = "Enter Valid Number"
                }
                if (outletName.isEmpty()) {
                    loginOutletName.error = "Select Outlet from option"
                }
            }
        }
    }

    private fun verifyDialog() {
        progressBox.show()
        val verifyCode = verifyEditText.text.toString()
        if (verifyCode.isNotEmpty() && verifyCode == appSharedPreference.getSignInCode()) {
            loginUserName.setText("")
            loginMobile.setText("")
            loginOutletName.setText("")
            progressBox.dismiss()
            appSharedPreference.setUserSignInStatus(true)
            AppUtils.OUTLET_NAME = appSharedPreference.getOutletName()!!
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            progressBox.dismiss()
            Toast.makeText(this, "Invalid Verification Code", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.login_login_btn_id -> {
                signInValidation()
            }

            R.id.login_verify_btn_id -> {
                verifyDialog()
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finishAffinity()
        super.onBackPressed()
    }
}
