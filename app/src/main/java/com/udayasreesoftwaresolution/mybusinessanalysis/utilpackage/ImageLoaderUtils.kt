package com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage

import android.content.Context
import android.widget.ImageView
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.ImageScaleType
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import com.udayasreesoftwaresolution.mybusinessanalysis.R

class ImageLoaderUtils(val mContext : Context) {

    private var imageLoaderUtils : ImageLoaderUtils? = null

    private lateinit var imageLoader : ImageLoader
    private lateinit var displayOptions: DisplayImageOptions
    private lateinit var roundDisplayOptions: DisplayImageOptions

    @Synchronized
    fun getInstance() : ImageLoaderUtils {
        if (imageLoaderUtils == null) {
            imageLoaderUtils =
                ImageLoaderUtils(
                    mContext.applicationContext
                )
        }
        return imageLoaderUtils as ImageLoaderUtils
    }

    fun setupImageLoader() {
        roundDisplayOptions = DisplayImageOptions.Builder()
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
            .showImageOnLoading(R.drawable.ic_default)
            .showImageForEmptyUri(R.drawable.ic_default)
            .showImageOnFail(R.drawable.ic_default)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .build()

        val config = ImageLoaderConfiguration.Builder(mContext)
            .threadPriority(Thread.NORM_PRIORITY - 2)
            .denyCacheImageMultipleSizesInMemory()
            .defaultDisplayImageOptions(displayOptions)
            .build()

        imageLoader = ImageLoader.getInstance()
        imageLoader.init(config)
    }

    fun displayRoundImage(imageUrl : String, imageView : ImageView?) {
        try {
            if (imageUrl.isNotEmpty() && imageView != null) {
                imageLoader.displayImage(imageUrl, imageView, roundDisplayOptions)
            }
        } catch (e : Exception){
            e.printStackTrace()
        }
    }

    fun displayImage(imageUrl : String, imageView : ImageView?) {
        try {
            if (imageUrl.isNotEmpty() && imageView != null) {
                imageLoader.displayImage(imageUrl, imageView, displayOptions)
            }
        } catch (e : Exception){
            e.printStackTrace()
        }
    }
}