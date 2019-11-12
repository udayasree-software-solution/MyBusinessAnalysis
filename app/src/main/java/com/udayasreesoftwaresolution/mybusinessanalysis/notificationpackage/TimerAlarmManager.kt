package com.udayasreesoftwaresolution.mybusinessanalysis.notificationpackage

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Build
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PaymentRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.TimeDataTable
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TimerAlarmManager(val context: Context) {

    private var timerAlarmManager: TimerAlarmManager? = null

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    @Synchronized
    fun getInstance(): TimerAlarmManager {
        if (timerAlarmManager == null) {
            timerAlarmManager = TimerAlarmManager(context.applicationContext)
        }
        return timerAlarmManager as TimerAlarmManager
    }

    private fun requestCodeFromPreference(requestType : Boolean, ids : Int) : ArrayList<Int> {
        val requestIds = ArrayList<Int>()
        try {
            val preferenceSharedUtils = AppSharedPreference(context)
            val jsonArray = JSONArray(preferenceSharedUtils.getAlarmIds())

            if (requestType) {
                jsonArray.put(ids)
                preferenceSharedUtils.clearAlarmIDPreference()
                preferenceSharedUtils.setAlarmIds(jsonArray.toString())
            }

            for (i in 0 until jsonArray.length()) {
                requestIds.add(jsonArray.getInt(i))
            }
            if (!requestType){
                preferenceSharedUtils.clearAlarmIDPreference()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return requestIds
    }

    fun startAlarmManager() {
        GetTimerDateAsync().execute()
    }

    fun stopAlarmManager() {
        val idsList = requestCodeFromPreference(false, 0)
        for (ids in idsList) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, TimerNotificationBroadCastReceiver::class.java)
            pendingIntent = PendingIntent.getBroadcast(context, ids, intent, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetTimerDateAsync : AsyncTask<Void, Void, List<TimeDataTable>>() {
        override fun doInBackground(vararg p0: Void?): List<TimeDataTable> {
            return PaymentRepository(context.applicationContext).queryDateInMillis(false) as ArrayList<TimeDataTable>
        }

        override fun onPostExecute(result: List<TimeDataTable>?) {
            super.onPostExecute(result)
            if (result != null && result.isNotEmpty()) {
                for (resultData in result) {
                    with(resultData){
                        for (i in 0..days) {
                            val calendar = Calendar.getInstance()
                            calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
                            calendar.timeInMillis = date
                            calendar.add(Calendar.DATE, ((days - i) * -1))
                            val millis : Long = calendar.timeInMillis

                            if (millis >= Calendar.getInstance().timeInMillis) {

                                val requestCode = AppUtils.randomNumbers()
                                requestCodeFromPreference(true, requestCode)
                                alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                                val intent = Intent(
                                    context,
                                    TimerNotificationBroadCastReceiver::class.java
                                )
                                intent.putExtra(ConstantUtils.TASK_SLNO, slNo)
                                pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, 0)

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                } else {
                                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}