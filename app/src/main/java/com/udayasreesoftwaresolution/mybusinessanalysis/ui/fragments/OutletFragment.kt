package com.udayasreesoftwaresolution.mybusinessanalysis.ui.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.FailReason
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener
import com.udayasreesoft.adminbusinessanalysis.retorfit.ApiInterface
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.firebasepackage.FireBaseConstants
import com.udayasreesoftwaresolution.mybusinessanalysis.progresspackage.ProgressBox
import com.udayasreesoftwaresolution.mybusinessanalysis.retorfit.ApiClient
import com.udayasreesoftwaresolution.mybusinessanalysis.retorfit.model.PostOffice
import com.udayasreesoftwaresolution.mybusinessanalysis.retorfit.model.ZipcodeModel
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.model.CompanyModel
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import retrofit2.Call
import retrofit2.Callback


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class OutletFragment : Fragment(), View.OnClickListener {

    private lateinit var outletName: EditText
    private lateinit var outletContact: EditText
    private lateinit var outletAddress: AutoCompleteTextView
    private lateinit var outletZipcode: EditText
    private lateinit var outletSearch: ImageView
    private lateinit var outletLogoImage: ImageView
    private lateinit var outletLogoEdit : TextView
    private lateinit var outletBannerEdit: ImageView
    private lateinit var outletBannerImage: ImageView
    private lateinit var outletSave: Button

    private lateinit var progressBox: ProgressBox
    private lateinit var displayOptions: DisplayImageOptions
    private lateinit var roundDisplayOption: DisplayImageOptions
    private lateinit var imageLoader: ImageLoader

    private lateinit var preferenceSharedUtils: AppSharedPreference

    private var isLogoImage = false
    private var serverLogoUrl = ""
    private var serverBannerUrl = ""

    companion object {
        fun newInstance() : OutletFragment {
            return OutletFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_outlet, container, false)
        initView(view)
        return view
    }

    private fun initView(view: View) {
        outletName = view.findViewById(R.id.frag_setup_name_id)
        outletContact = view.findViewById(R.id.frag_setup_contact_id)
        outletAddress = view.findViewById(R.id.frag_setup_address_id)
        outletZipcode = view.findViewById(R.id.frag_setup_zipcode_id)
        outletSearch = view.findViewById(R.id.frag_setup_search_id)
        outletLogoImage = view.findViewById(R.id.frag_setup_logo_id)
        outletLogoEdit = view.findViewById(R.id.frag_Setup_edit_logo_id)
        outletBannerEdit = view.findViewById(R.id.frag_setup_edit_banner_id)
        outletBannerImage = view.findViewById(R.id.frag_setup_banner_id)
        outletSave = view.findViewById(R.id.frag_setup_save_btn_id)

        outletBannerImage.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.70).toInt()

        outletLogoImage.layoutParams.width = (AppUtils.SCREEN_WIDTH * 0.40).toInt()
        outletLogoImage.layoutParams.height = (AppUtils.SCREEN_WIDTH * 0.40).toInt()

        outletSave.setOnClickListener(this)
        outletSearch.setOnClickListener(this)
        outletLogoEdit.setOnClickListener(this)
        outletBannerEdit.setOnClickListener(this)

        preferenceSharedUtils = AppSharedPreference(activity!!)
        progressBox = ProgressBox(activity!!)

        setupImageLoader()
        readOutletDetailsFromFireBase()
    }

    private fun setupImageLoader() {
        roundDisplayOption = DisplayImageOptions.Builder()
            .displayer(RoundedBitmapDisplayer(1000))
            .showImageOnLoading(R.drawable.ic_default)
            .showImageForEmptyUri(R.drawable.ic_default)
            .showImageOnFail(R.drawable.ic_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build()

        displayOptions = DisplayImageOptions.Builder()
            .showImageOnLoading(android.R.drawable.stat_sys_download_done)
            .showImageForEmptyUri(android.R.drawable.stat_notify_error)
            .showImageOnFail(android.R.drawable.stat_notify_error)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build()

        val config = ImageLoaderConfiguration.Builder(activity)
            .threadPriority(Thread.NORM_PRIORITY - 2)
            .denyCacheImageMultipleSizesInMemory()
            .defaultDisplayImageOptions(displayOptions)
            .build()

        imageLoader = ImageLoader.getInstance()
        imageLoader.init(config)
    }

    private fun getZipCodeAddress(zipcode: String) {
        if (AppUtils.networkConnectivityCheck(activity!!) && zipcode.isNotEmpty() || zipcode.isNotBlank()) {
            progressBox.show()
            val apiInterface = ApiClient.getZipCodeApiClient().create(ApiInterface::class.java)
            val call = apiInterface.getZipCodeAddress(zipcode)
            call.enqueue(object : Callback<ZipcodeModel> {
                override fun onFailure(call: Call<ZipcodeModel>, t: Throwable) {
                    addressFunc()
                }

                override fun onResponse(
                    call: Call<ZipcodeModel>,
                    response: retrofit2.Response<ZipcodeModel>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val zipCodeModel: ZipcodeModel? = response.body()
                        if (zipCodeModel != null && zipCodeModel.status == "Success") {
                            val postOffice: List<PostOffice> = zipCodeModel.postOffice
                            if (postOffice.isNotEmpty()) {
                                addressFunc()
                                val addressList = ArrayList<String>()
                                for (element in postOffice) {
                                    with(element) {
                                        addressList.add("$name, $division,\n$state, $country,\npincode - $zipcode")
                                    }
                                }

                                if (addressList.isNotEmpty()) {
                                    setupAddressTextView(addressList)
                                }
                            }
                        } else {
                            addressFunc()
                        }
                    }
                }
            })
        }
    }

    private fun setupAddressTextView(addressList: List<String>?) {
        if (addressList != null && addressList.isNotEmpty()) {
            val arrayAdapter = ArrayAdapter(
                activity!!,
                android.R.layout.simple_list_item_1,
                android.R.id.text1,
                addressList
            )
            outletAddress.threshold = 1
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_list_item_1)
            outletAddress.setAdapter(arrayAdapter)
            outletAddress.showDropDown()
            outletAddress.onItemClickListener =
                AdapterView.OnItemClickListener { _, _, position, _ ->
                    outletAddress.setText(arrayAdapter.getItem(position)!!)
                }
        }
    }

    private fun readOutletDetailsFromFireBase() {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()
            && AppUtils.OUTLET_NAME.isNotBlank() && AppUtils.OUTLET_NAME.isNotEmpty()
        ) {
            progressBox.show()
            val fireBaseReference = FirebaseDatabase.getInstance()
                .getReference(AppUtils.OUTLET_NAME)
                .child(FireBaseConstants.OUTLET_PROFILE)

            fireBaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    progressBox.dismiss()
                }

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val companyModel = dataSnapshot.getValue(CompanyModel::class.java)
                        if (companyModel != null) {
                            setModelDataToView(companyModel)
                        }
                    } else {
                        setModelDataToView(
                            CompanyModel(
                                AppUtils.OUTLET_NAME,
                                "", "", FireBaseConstants.DEFAULT_LOGO, FireBaseConstants.DEFAULT_BANNER
                            )
                        )
                    }
                    progressBox.dismiss()
                }
            })

        }
    }

    private fun writeOutletDetailsToFireBase() {
        if (AppUtils.networkConnectivityCheck(activity!!) && AppUtils.OUTLET_NAME.isNotEmpty()
            && AppUtils.OUTLET_NAME.isNotBlank() && AppUtils.OUTLET_NAME.isNotEmpty()
        ) {
            val name = outletName.text.toString()
            val contact = outletContact.text.toString()
            val address = outletAddress.text.toString()

            if (name.isNotEmpty() && contact.isNotEmpty()
                && contact.length == 10 && address.isNotEmpty()
            ) {
                progressBox.show()
                FirebaseDatabase.getInstance()
                    .getReference(AppUtils.OUTLET_NAME)
                    .child(FireBaseConstants.OUTLET_PROFILE)
                    .setValue(
                        CompanyModel(
                            name,
                            address,
                            contact,
                            serverLogoUrl ?: FireBaseConstants.DEFAULT_LOGO,
                            serverBannerUrl ?: FireBaseConstants.DEFAULT_BANNER
                        )
                    ) { _, _ ->
                        progressBox.dismiss()
                    }
                Toast.makeText(activity!!, "Saved details successfully", Toast.LENGTH_SHORT).show()
            } else {
                if (name.isEmpty() && name.isBlank()) {
                    outletName.error = "Enter Outlet Name"
                }

                if (contact.isEmpty() && contact.isBlank() && contact.length != 10) {
                    outletContact.error = "Enter Outlet Contact"
                }

                if (address.isEmpty() && address.isBlank()) {
                    outletAddress.error = "Enter Outlet Address"
                }
                progressBox.dismiss()
            }
        }
    }

    private fun setModelDataToView(companyModel: CompanyModel) {
        progressBox.show()
        outletName.setText(companyModel.outletName ?: "")
        outletContact.setText(companyModel.outletContact ?: "")
        outletAddress.setText(companyModel.outletAddress ?: "")
        outletZipcode.setText("")
        if (AppUtils.networkConnectivityCheck(activity!!)) {

            serverLogoUrl = if (companyModel.outletLogo != null && companyModel.outletLogo.isNotEmpty()) {
                preferenceSharedUtils.setOutletLogoUrl(companyModel.outletLogo)
                companyModel.outletLogo
            } else {
                FireBaseConstants.DEFAULT_LOGO
            }
            serverBannerUrl = if (companyModel.outletBanner != null && companyModel.outletBanner.isNotEmpty()) {
                preferenceSharedUtils.setOutletBannerUrl(companyModel.outletBanner)
                companyModel.outletBanner
            } else {
                FireBaseConstants.DEFAULT_BANNER
            }

            imageLoader.displayImage(
                serverLogoUrl,
                outletLogoImage,
                roundDisplayOption
            )

            imageLoader.displayImage(
                serverBannerUrl,
                outletBannerImage,
                displayOptions
            )
        }
        progressBox.dismiss()
    }

    private fun storeImageToFireBase(url: String, imageView: ImageView) {
        if (url.isNotEmpty() && AppUtils.networkConnectivityCheck(activity!!)) {

            imageLoader.displayImage(
                url, imageView, if (isLogoImage) {
                    roundDisplayOption
                } else {
                    displayOptions
                }, object : ImageLoadingListener {
                    override fun onLoadingComplete(
                        imageUri: String?,
                        view: View?,
                        loadedImage: Bitmap?
                    ) {
                        if (AppUtils.OUTLET_NAME.isNotEmpty() && AppUtils.OUTLET_NAME.isNotBlank() && AppUtils.OUTLET_NAME.isNotEmpty()) {
                            progressBox.show()
                            val ext = url.substring(url.lastIndexOf("."))
                            val storageReference: StorageReference = FirebaseStorage.getInstance()
                                .getReference(AppUtils.OUTLET_NAME.plus("/"))
                                .child(FireBaseConstants.OUTLET_PROFILE)
                                .child(
                                    "${AppUtils.OUTLET_NAME}_${if (isLogoImage) {
                                        "Logo"
                                    } else {
                                        "Banner"
                                    }}$ext"
                                )

                            storageReference.putFile(Uri.parse(url))
                                .addOnSuccessListener { taskSnapShot ->
                                    progressBox.dismiss()
                                    if (isLogoImage) {
                                        serverLogoUrl =
                                            taskSnapShot.metadata?.reference?.downloadUrl.toString()
                                    } else {
                                        serverBannerUrl =
                                            taskSnapShot.metadata?.reference?.downloadUrl.toString()
                                    }
                                }

                                .addOnFailureListener {
                                    progressBox.dismiss()
                                    Toast.makeText(
                                        activity!!, "Fail to store Company ${if (isLogoImage) {
                                            "Logo"
                                        } else {
                                            "Banner"
                                        }}" +
                                                "Please try again", Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                    }

                    override fun onLoadingStarted(imageUri: String?, view: View?) {}
                    override fun onLoadingCancelled(imageUri: String?, view: View?) {}
                    override fun onLoadingFailed(
                        imageUri: String?,
                        view: View?,
                        failReason: FailReason?
                    ) {
                    }
                })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            if (requestCode == ConstantUtils.PERMISSION_GALLERY && resultCode == Activity.RESULT_OK && data != null && AppUtils.networkConnectivityCheck(
                    activity!!
                )
            ) {
                val selectedImage = data.data

                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                val cursor =
                    context?.contentResolver?.query(
                        selectedImage ?: Uri.parse(""),
                        filePathColumn,
                        null,
                        null,
                        null
                    )
                cursor?.moveToFirst()

                val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
                val imagePath = cursor?.getString(columnIndex ?: 0)
                cursor?.close()

                if (isLogoImage) {
                    storeImageToFireBase("file://$imagePath", outletLogoImage)
                } else {
                    storeImageToFireBase("file://$imagePath", outletBannerImage)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addressFunc() {
        with(outletAddress) {
            setSingleLine(false)
//            imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
            maxLines = 5
            isVerticalScrollBarEnabled = true
            movementMethod = ScrollingMovementMethod.getInstance()
            scrollBarStyle = View.SCROLLBARS_INSIDE_INSET
            gravity = Gravity.TOP + Gravity.START
            inputType =
                InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_FLAG_CAP_WORDS + InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setText("")
            hint = "Outlet Address"
            isFocusable = true
        }
        progressBox.dismiss()
    }

    private fun setupGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(
            Intent.createChooser(galleryIntent, "Select Image"),
            ConstantUtils.PERMISSION_GALLERY
        )
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.frag_setup_save_btn_id -> {
                writeOutletDetailsToFireBase()
            }

            R.id.frag_setup_search_id -> {
                getZipCodeAddress(outletZipcode.text.toString())
            }

            R.id.frag_Setup_edit_logo_id -> {
                isLogoImage = true
                setupGallery()
            }

            R.id.frag_setup_edit_banner_id -> {
                isLogoImage = false
                setupGallery()
            }
        }
    }

}
