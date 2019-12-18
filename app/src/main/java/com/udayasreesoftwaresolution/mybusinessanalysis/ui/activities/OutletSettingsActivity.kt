package com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipDrawable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.CategoryRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.CategoryTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils




class OutletSettingsActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var outletLayout: RelativeLayout
    private lateinit var previousBtn: TextView
    private lateinit var nextBtn: TextView

    private lateinit var editText: EditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var doneBtn: ImageView

    private lateinit var categoryChipData: ArrayList<String>
    private lateinit var paymentTypeChipData: ArrayList<String>
    private lateinit var expensesTypeChipData: ArrayList<String>

    private lateinit var progressBox: ProgressBox

    private var clickCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_outlet_settings)

        initView()
    }

    private fun initView() {
        previousBtn = findViewById(R.id.outlet_setting_previous_id)
        nextBtn = findViewById(R.id.outlet_setting_next_id)

        outletLayout = findViewById(R.id.outlet_setting_outlet)

        editText = findViewById(R.id.outlet_setting_outlet_edittext)
        chipGroup = findViewById(R.id.outlet_setting_chip_group)
        doneBtn = findViewById(R.id.outlet_setting_outlet_done)

        chipGroup.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.6).toInt()

        progressBox = ProgressBox(this)

        previousBtn.setOnClickListener(this)
        nextBtn.setOnClickListener(this)
        doneBtn.setOnClickListener(this)

        /*editText.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(view: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (view != null && actionId == EditorInfo.IME_ACTION_DONE) {
                    val text: String = view.text.toString()
                    view.text = ""
                    createChipView(text)
                    return true
                }
                return false
            }
        })*/

        OutletSettingTask().execute()
    }

    @SuppressLint("StaticFieldLeak")
    inner class OutletSettingTask : AsyncTask<Void, Void, Boolean>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
            categoryChipData = ArrayList()
            paymentTypeChipData = ArrayList()
            expensesTypeChipData = ArrayList()
        }

        override fun doInBackground(vararg p0: Void?): Boolean {
            val categoryRepository = CategoryRepository(this@OutletSettingsActivity)
            categoryChipData =
                categoryRepository.queryCategoryListByKey(FireBaseConstants.OUTLET_CATEGORY) as ArrayList<String>
            paymentTypeChipData =
                categoryRepository.queryCategoryListByKey(FireBaseConstants.PAYMENT_CATEGORY) as ArrayList<String>
            expensesTypeChipData =
                categoryRepository.queryCategoryListByKey(FireBaseConstants.EXPENSES_CATEGORY) as ArrayList<String>
            return true
        }

        override fun onPostExecute(result: Boolean?) {
            super.onPostExecute(result)
            progressBox.dismiss()
            outletSetting(false)
        }
    }

    private fun createChipView(text: String) {
        if (text.isNotEmpty()) {
            val chip = Chip(this)
            chip.text = text

            chip.isClickable = true
            chip.isCheckable = false

            if (!text.equals(AppUtils.OUTLET_NAME, ignoreCase = true)) {
                val chipDrawable =
                    ChipDrawable.createFromAttributes(this, null, 0, R.style.Widget_MaterialComponents_Chip_Entry)
                chip.setChipDrawable(chipDrawable)
            }
            val id = View.generateViewId()
            chip.id = id

            when (clickCount) {
                1 -> {
                    categoryChipData.add(text)
                }

                2 -> {
                    paymentTypeChipData.add(text)
                }

                3 -> {
                    expensesTypeChipData.add(text)
                }
            }

            chipGroup.addView(chip)
            chip.setOnCloseIconClickListener { view ->

                val chipView = findViewById<Chip>(view.id)
                val chipText = chipView.text.toString()

                when (clickCount) {
                    1 -> {

                        val iterator = categoryChipData.iterator()
                        while (iterator.hasNext()) {
                            val category = iterator.next()
                            if (chipText.equals(category, ignoreCase = true)) {
                                iterator.remove()
                                chipGroup.removeView(chipView)
                                break
                            }
                        }

                        /*for (value in categoryChipData) {
                            if (chipText.equals(value, ignoreCase = true)) {
                                categoryChipData.remove(value)
                                chipGroup.removeView(chipView)
                                break
                            }
                        }*/
                    }

                    2 -> {

                        val iterator = paymentTypeChipData.iterator()
                        while (iterator.hasNext()) {
                            val payment = iterator.next()
                            if (chipText.equals(payment, ignoreCase = true)) {
                                iterator.remove()
                                chipGroup.removeView(chipView)
                                break
                            }
                        }

                        /*for (value in paymentTypeChipData) {
                            if (chipText.equals(value, ignoreCase = true)) {
                                paymentTypeChipData.remove(value)
                                chipGroup.removeView(chipView)
                                break
                            }
                        }*/
                    }

                    3 -> {

                        val iterator = expensesTypeChipData.iterator()
                        while (iterator.hasNext()) {
                            val expenses = iterator.next()
                            if (chipText.equals(expenses, ignoreCase = true)) {
                                iterator.remove()
                                chipGroup.removeView(chipView)
                                break
                            }
                        }

                        /*for (value in expensesTypeChipData) {
                            if (chipText.equals(value, ignoreCase = true)) {
                                expensesTypeChipData.remove(value)
                                chipGroup.removeView(chipView)
                                break
                            }
                        }*/
                    }
                }
            }
        }
    }

    private fun outletSetting(status: Boolean) {
        outletLayout.animation = if (status) {
            AnimationUtils.loadAnimation(this, R.anim.left_side)
        } else {
            AnimationUtils.loadAnimation(this, R.anim.right_side)
        }
        chipGroup.removeAllViews()
        nextBtn.setText(R.string.next)
        previousBtn.visibility = View.VISIBLE
        nextBtn.visibility = View.VISIBLE
        when (clickCount) {
            1 -> {
                previousBtn.visibility = View.INVISIBLE
                editText.hint = "Enter Outlet Category"

                val iterator = categoryChipData.iterator()
                while (iterator.hasNext()) {
                    val category = iterator.next()
                    if (!category.equals(ConstantUtils.EXPENSES, ignoreCase = true)) {
                        createChipView(category)
                    }
                }
            }

            2 -> {
                editText.hint = "Enter Payment Acceptance"

                val iterator = paymentTypeChipData.iterator()
                while (iterator.hasNext()) {
                    createChipView(iterator.next())
                }
            }

            3 -> {
                editText.hint = "Enter Expenses Category"
                nextBtn.setText(R.string.done)

                val iterator = expensesTypeChipData.iterator()
                while (iterator.hasNext()) {
                    createChipView(iterator.next())
                }
            }

            4 -> {
                /*TODO: Save all the data to server*/
                saveDataToServer()
            }
        }
    }

    private fun saveDataToServer() {
        var totalLoop = 0
        var loopCount = 0

        totalLoop += categoryChipData.size
        totalLoop += paymentTypeChipData.size
        totalLoop += expensesTypeChipData.size

        if (totalLoop > 0) {
            progressBox.show()

            val referenceFireBase = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.BUSINESS_CATEGORY)

            referenceFireBase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        dataSnapshot.ref.removeValue { error, _ ->
                            if (error == null) {

                                val referenceOutlet = FirebaseDatabase.getInstance()
                                    .getReference(AppUtils.OUTLET_NAME)
                                    .child(FireBaseConstants.BUSINESS_CATEGORY)
                                    .child(FireBaseConstants.OUTLET_CATEGORY)

                                val referencePayment = FirebaseDatabase.getInstance()
                                    .getReference(AppUtils.OUTLET_NAME)
                                    .child(FireBaseConstants.BUSINESS_CATEGORY)
                                    .child(FireBaseConstants.PAYMENT_CATEGORY)

                                val referenceExpenses = FirebaseDatabase.getInstance()
                                    .getReference(AppUtils.OUTLET_NAME)
                                    .child(FireBaseConstants.BUSINESS_CATEGORY)
                                    .child(FireBaseConstants.EXPENSES_CATEGORY)

                                for (category in categoryChipData) {
                                    referenceOutlet
                                        .push()
                                        .setValue(category) { _, _ ->
                                            loopCount++
                                        }
                                }

                                for (payment in paymentTypeChipData) {
                                    referencePayment
                                        .push()
                                        .setValue(payment) { _, _ ->
                                            loopCount++
                                        }
                                }

                                for (expenses in expensesTypeChipData) {
                                    referenceExpenses
                                        .push()
                                        .setValue(expenses) { _, _ ->
                                            loopCount++
                                        }
                                }

                                if (loopCount >= totalLoop) {
                                    loopCount = 0
                                    val categoryRepository = CategoryRepository(this@OutletSettingsActivity)
                                    categoryRepository.clearDataBase()

                                    Handler().postDelayed({

                                        for (outlet in categoryChipData) {
                                            loopCount++
                                            categoryRepository.insertTask(
                                                CategoryTable(
                                                    FireBaseConstants.OUTLET_CATEGORY,
                                                    outlet
                                                )
                                            )
                                        }

                                        for (payment in paymentTypeChipData) {
                                            loopCount++
                                            categoryRepository.insertTask(
                                                CategoryTable(
                                                    FireBaseConstants.PAYMENT_CATEGORY,
                                                    payment
                                                )
                                            )
                                        }

                                        for (expenses in expensesTypeChipData) {
                                            loopCount++
                                            categoryRepository.insertTask(
                                                CategoryTable(
                                                    FireBaseConstants.EXPENSES_CATEGORY,
                                                    expenses
                                                )
                                            )
                                        }

                                        val sharedPreference = AppSharedPreference(this@OutletSettingsActivity)
                                        if (!sharedPreference.getOutletDetailsStatus()) {
                                            sharedPreference.setOutletDetailsStatus(true)
                                        }
                                        if (loopCount >= totalLoop) {
                                            progressBox.dismiss()
                                        }
                                    }, 4000)
                                }

                            } else {
                                progressBox.dismiss()
                            }
                        }
                    } else {
                        progressBox.dismiss()
                    }
                }
            })
        }
    }

    private fun businessPayment(status: Boolean) {
        nextBtn.setText(R.string.next)
        previousBtn.visibility = View.VISIBLE
        nextBtn.visibility = View.VISIBLE
        when (clickCount) {
            1 -> {
                previousBtn.visibility = View.INVISIBLE

            }

            2 -> {

            }

            3 -> {
                nextBtn.setText(R.string.done)

            }

            4 -> {
                nextBtn.visibility = View.INVISIBLE

            }

            else -> {
                /*TODO: Save all the data to server*/
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.outlet_setting_previous_id -> {
                clickCount--
                outletSetting(true)
            }

            R.id.outlet_setting_next_id -> {
                clickCount++
                outletSetting(false)
            }

            R.id.outlet_setting_outlet_done -> {
                val text: String = editText.text.toString()
                editText.setText("")
                createChipView(text)
            }
        }
    }

    override fun onBackPressed() {
        if (clickCount > 1) {
            clickCount--
            outletSetting(true)
        } else {
            finish()
            super.onBackPressed()
        }
    }
}
