package com.udayasreesoftwaresolution.mybusinessanalysis.notificationpackage

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.AsyncTask
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.udayasreesoftwaresolution.mybusinessanalysis.R
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.repository.PaymentRepository
import com.udayasreesoftwaresolution.mybusinessanalysis.roompackage.tables.PaymentTable
import com.udayasreesoftwaresolution.mybusinessanalysis.ui.activities.HomeActivity
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.AppSharedPreference
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.ConstantUtils
import com.udayasreesoftwaresolution.mybusinessanalysis.utilpackage.VersionSharedPreference
import java.text.SimpleDateFormat
import java.util.*



class TimerNotificationBroadCastReceiver : BroadcastReceiver() {

    private var context: Context? = null
    private lateinit var notificationManagerNotify: NotificationManager
    private val ADMIN_CHANNEL_ID = "admin_channel"
    private var NOTIFICATION_TYPE = ""
    private lateinit var sharedPreference : AppSharedPreference

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        sharedPreference = AppSharedPreference(context?.applicationContext!!)
        if (sharedPreference.getUserSignInStatus()) {
            val bundle = intent?.extras
            if (bundle != null) {
                if (bundle.containsKey(ConstantUtils.TASK_SLNO)) {
                    NOTIFICATION_TYPE = ConstantNotification.NOTIFY_PAYMENT
                    GetTodayNotificationData(bundle.getInt(ConstantUtils.TASK_SLNO)).execute()
                }
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    inner class GetTodayNotificationData(private val slNo : Int) : AsyncTask<Void, Void, PaymentTable>() {
        override fun doInBackground(vararg p0: Void?): PaymentTable {
            return PaymentRepository(context).queryPaymentBySlNo(slNo) as PaymentTable
        }

        override fun onPostExecute(result: PaymentTable?) {
            super.onPostExecute(result)
            if (result != null) {
                val calendar = Calendar.getInstance()
                calendar.timeZone = TimeZone.getTimeZone("Asia/Calcutta")
                val dateFormat = SimpleDateFormat("MMM dd yyyy", Locale.US)
                val currentDate = dateFormat.format(calendar.timeInMillis)
                val chequeDate = dateFormat.format(result.dateInMillis)
                with(result) {
                    for (i in 0..preDays) {
                        calendar.timeInMillis = dateInMillis
                        calendar.add(Calendar.DATE, ((preDays - i) * -1))
                        val receiverDate = dateFormat.format(calendar.timeInMillis)
                        if (currentDate == receiverDate) {
                            simpleOfflineNotification(
                                "Pay to $clientName",
                                "Payable amount of Rs.$payAmount",
                                "Payable amount of Rs.$payAmount/- is to pay on $chequeDate with Cheque $chequeNumber ."
                            )
                        }
                    }
                }
            }
        }
    }

    private fun simpleOfflineNotification(title: String, amount : String, description: String) {
        val notificationId: Int = Random().nextInt(60000)

        val intent = Intent(context?.applicationContext, HomeActivity::class.java)
        when(NOTIFICATION_TYPE) {
            NOTIFICATION_TYPE -> {
                intent.putExtra(ConstantNotification.NOTIFICATION_KEY, ConstantNotification.NOTIFY_PAYMENT)
            }

            else -> {

            }
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        val bitmapIcon = BitmapFactory.decodeResource(context?.resources, R.drawable.ic_notify_badges)
        val builder = NotificationCompat.Builder(context!!, ADMIN_CHANNEL_ID)
        builder
            .setAutoCancel(true)
            .setOngoing(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_notify_badges)
            .setLargeIcon(bitmapIcon)
            .setContentTitle(title)
            .setContentText(amount)
            .setStyle(NotificationCompat.BigTextStyle().bigText(description))
            .setContentIntent(pendingIntent)
            .setNumber(notificationId)

        notificationManagerNotify = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannel()
        }

        notificationManagerNotify.notify(notificationId, builder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupChannel() {
        val adminChannelName = context?.getString(R.string.notifications_admin_channel_name)
        val adminChannelDescription = context?.getString(R.string.notifications_admin_channel_description)

        val adminChannel: NotificationChannel =
            NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_HIGH)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.GREEN
        adminChannel.enableVibration(true)

        notificationManagerNotify.createNotificationChannel(adminChannel)
    }

}