package airport.transfer.sale.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import airport.transfer.sale.R
import airport.transfer.sale.storage.Preferences


class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Log.d("Firebase", "Refreshed token: " + refreshedToken!!)

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Preferences.savePushToken(this, refreshedToken)
        super.onTokenRefresh()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val orderChannel = NotificationChannel(MessagingService.CHANNEL_ORDER, getString(R.string.order), NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                with(orderChannel){
                    enableLights(true)
                    lightColor = Color.RED
                    vibrationPattern = longArrayOf(0, 1000, 500, 1000)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(orderChannel)

        }
    }
}