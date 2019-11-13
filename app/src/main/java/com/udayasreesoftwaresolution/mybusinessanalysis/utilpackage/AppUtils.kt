package com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage

import android.content.Context
import android.content.DialogInterface
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AppUtils {
    companion object {
        @JvmField var outletValidity = false
        @JvmField var isAdminStatus = false
        @JvmField var isServiceRun = true
        @JvmField var OUTLET_NAME = ""

        @JvmField var SCREEN_WIDTH = 0
        @JvmField var SCREEN_HEIGHT = 0

        @JvmField var timeInMillis : Long = 0L

        fun logMessage(message : String) {
            Log.d("CHECK_DATE", message)
        }

        fun randomNumbers() : Int {
            return Random().nextInt(999999)
        }

        fun uniqueKey() : String {
            val characters : String = "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
            var key = ""
            for (c in 0 until 11) {
                key += characters[Random().nextInt(characters.length)]
            }
            return key
        }

        fun fireBaseChildId(outletCode : String) : String {
            val characters : String = "0123456789AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz"
            var userId = outletCode
            for (c in 0 until 11) {
                userId += characters[Random().nextInt(characters.length)]
            }
            return userId
        }

        fun getTypeFace(context: Context, typefaceId : Int) : Typeface {
            var typeFaceName = ""
            when(typefaceId) {
                0 -> {
                    typeFaceName = "alluraregular.otf"
                }
                1 -> {
                    typeFaceName = "blackjack.otf"
                }
                2 -> {
                    typeFaceName = "kendaltype.ttf"
                }
                3 -> {
                    typeFaceName = "montserrat_regular_Editor.ttf"
                }
                4 -> {
                    typeFaceName = "Satisfy-Regular.ttf"
                }
                else -> {
                    typeFaceName = "sundaprada.ttf"
                }
            }
            return Typeface.createFromAsset(context.assets, "fonts/$typeFaceName")
        }

        fun getCurrentDate(isFixTime : Boolean) :String {
            val simpleDateFormat = SimpleDateFormat(ConstantUtils.DATE_FORMAT, Locale.US)
            val calendar = Calendar.getInstance()
            calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
            if (isFixTime) {
                calendar.set(Calendar.HOUR_OF_DAY, ConstantUtils.HOUR)
                calendar.set(Calendar.MINUTE, ConstantUtils.MINUTE)
                calendar.set(Calendar.SECOND, ConstantUtils.SECOND)
            }
            timeInMillis = calendar.timeInMillis
            return simpleDateFormat.format(timeInMillis)
        }

        fun networkConnectivityCheck(context: Context): Boolean {
            val connectivityManager: ConnectivityManager? =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivityManager != null) {
                val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
                if (networkInfo != null && networkInfo.isConnected) {
                    return true
                }
            }
            Toast.makeText(context, "Required Internet Connection", Toast.LENGTH_SHORT).show()
            return false
        }

    }
}