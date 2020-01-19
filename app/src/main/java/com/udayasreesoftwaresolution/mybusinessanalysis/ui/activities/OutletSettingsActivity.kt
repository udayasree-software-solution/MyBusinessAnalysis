package com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
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
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.models.CategoryModel
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

    private lateinit var outletList : ArrayList<String>
    private lateinit var paymentList : ArrayList<String>
    private lateinit var expensesList : ArrayList<String>

    private lateinit var insertIdArray : ArrayList<Int>
    private lateinit var removedIdArray : ArrayList<Int>


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
        chipGroup.setChipSpacing(15)
        progressBox = ProgressBox(this)

        previousBtn.setOnClickListener(this)
        nextBtn.setOnClickListener(this)
        doneBtn.setOnClickListener(this)

        outletList = ArrayList()
        paymentList = ArrayList()
        expensesList = ArrayList()
        insertIdArray = ArrayList()
        removedIdArray = ArrayList()

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
    inner class OutletSettingTask : AsyncTask<Void, Void, ArrayList<CategoryTable>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            progressBox.show()
        }

        override fun doInBackground(vararg p0: Void?): ArrayList<CategoryTable>? {
            return CategoryRepository(this@OutletSettingsActivity).queryFullCategoryList() as ArrayList<CategoryTable>?
        }

        override fun onPostExecute(result: ArrayList<CategoryTable>?) {
            super.onPostExecute(result)
            progressBox.dismiss()
            if (result != null && result.isNotEmpty()) {
                for (element in result) {
                    when(element.category_key) {
                        FireBaseConstants.OUTLET_CATEGORY -> {
                            outletList.add(element.category_name)
                        }

                        FireBaseConstants.PAYMENT_CATEGORY -> {
                            paymentList.add(element.category_name)
                        }

                        FireBaseConstants.EXPENSES_CATEGORY -> {
                            expensesList.add(element.category_name)
                        }
                    }
                }
                outletSetting(false)
            }
        }
    }

    private fun createChipView(text: String) {
        if (text.isNotEmpty()) {
            val chip = Chip(this)
            chip.text = text
            val id = View.generateViewId()
            chip.id = id
            chip.isClickable = true
            chip.isCheckable = false

            if (!text.equals(AppUtils.OUTLET_NAME, ignoreCase = true)) {
                val chipDrawable =
                    ChipDrawable.createFromAttributes(this, null, 0,
                        R.style.Widget_MaterialComponents_Chip_Action)
                chip.setChipDrawable(chipDrawable)
            }

            /*when (clickCount) {
                1 -> {
                    outletList.add(text)
                }

                2 -> {
                    paymentList.add(text)
                }

                3 -> {
                    expensesList.add(text)
                }
            }*/

            chipGroup.addView(chip)
            insertIdArray.add(id)

            chip.setOnClickListener{ view ->
                val chipView = findViewById<Chip>(view.id)
                val chipText = chipView.text.toString()

                if (!chipText.equals(AppUtils.OUTLET_NAME, ignoreCase = true)) {
                    removedIdArray.add(view.id)
                    chipGroup.removeView(chipView)
                }
            }
        }
    }

    private fun outletSetting(status: Boolean) {
        try {
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

                    for (outlet in outletList) {
                        if (!outlet.equals(ConstantUtils.EXPENSES, ignoreCase = true)) {
                            createChipView(outlet)
                        }
                    }
                    outletList.clear()
                }

                2 -> {
                    editText.hint = "Enter Payment Acceptance"

                    for (payment in paymentList) {
                        createChipView(payment)
                    }
                    paymentList.clear()
                }

                3 -> {
                    editText.hint = "Enter Expenses Category"
                    nextBtn.setText(R.string.done)

                    for (expenses in expensesList) {
                        createChipView(expenses)
                    }
                    expensesList.clear()
                }

                4 -> {
                    /*TODO: Save all the data to server*/
                    saveDataToServer()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveDataToServer() {
        progressBox.show()
        val categoryModelList: ArrayList<CategoryModel> = ArrayList()
        for (outlet in outletList) {
            categoryModelList.add(CategoryModel(FireBaseConstants.OUTLET_CATEGORY, outlet))
        }
        outletList.clear()
        for (payment in paymentList) {
            categoryModelList.add(CategoryModel(FireBaseConstants.PAYMENT_CATEGORY, payment))
        }
        paymentList.clear()
        for (expenses in expensesList) {
            categoryModelList.add(CategoryModel(FireBaseConstants.EXPENSES_CATEGORY, expenses))
        }
        expensesList.clear()

        val totalLoop = categoryModelList.size
        var loopCount = 0

        if (totalLoop > 0) {
            //progressBox.show()

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

                                val reference = FirebaseDatabase.getInstance()
                                    .getReference(AppUtils.OUTLET_NAME)
                                    .child(FireBaseConstants.BUSINESS_CATEGORY)

                                for (category in categoryModelList) {
                                    reference
                                        .push()
                                        .setValue(category) { _, _ ->
                                            loopCount++
                                            if (loopCount >= totalLoop) {
                                                saveDataToRoomDB(categoryModelList)
                                            }
                                        }
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
        } else {
            progressBox.dismiss()
        }
    }

    private fun saveDataToRoomDB(categoryModelList : ArrayList<CategoryModel>) {
        val totalLoop = categoryModelList.size
        var loopCount = 0

        val categoryRepository = CategoryRepository(this)
        categoryRepository.clearDataBase()

        Handler().postDelayed({
            if (totalLoop <= 0) {
                progressBox.dismiss()
            }
            for (element in categoryModelList) {
                loopCount++
                categoryRepository.insertTask(CategoryTable(element.category_key, element.category_name))
                if (loopCount >= totalLoop) {
                    val sharedPreference = AppSharedPreference(this@OutletSettingsActivity)
                    if (!sharedPreference.getOutletDetailsStatus()) {
                        sharedPreference.setOutletDetailsStatus(true)
                        startActivity(Intent(this@OutletSettingsActivity, HomeActivity::class.java))
                    }
                    progressBox.dismiss()
                    finish()
                }
            }
        }, 4000)
    }

    private fun getViewTextFromId() {
        if (insertIdArray.isNotEmpty()) {
            progressBox.show()
            for (deleteId in removedIdArray) {
                insertIdArray.remove(deleteId)
            }

            Handler().postDelayed({
                for(id in insertIdArray) {
                    val chipView = findViewById<Chip>(id)
                    val chipText = chipView.text.toString()
                    when (clickCount) {
                        2 -> {
                            outletList.add(chipText)
                        }

                        3 -> {
                            paymentList.add(chipText)
                        }

                        4 -> {
                            expensesList.add(chipText)
                        }
                    }
                }
                insertIdArray.clear()
                removedIdArray.clear()
                progressBox.dismiss()
                outletSetting(false)
            },500)
        } else {
            outletSetting(false)
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
                getViewTextFromId()
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
