package airport.transfer.sale.firebase

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import airport.transfer.sale.R
import airport.transfer.sale.mvp.model.PushModel
import airport.transfer.sale.ui.activity.MainActivity_
import org.greenrobot.eventbus.EventBus


class MessagingService : FirebaseMessagingService() {

    companion object {
        const val CHANNEL_ORDER = "channel_order"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        System.out.println("!!!!!PushReceived")
        val data = remoteMessage?.data ?: return
        val title = data["title"]
        val text = data["body"]
        val builder = android.support.v4.app.NotificationCompat.Builder(applicationContext, CHANNEL_ORDER)
                .setSmallIcon(R.mipmap.ic_launcher_rounded)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher_rounded))
                .setContentTitle(title)
                .setContentText(text)
                .setShowWhen(true)
                .setOngoing(false)
                .setDefaults(Notification.DEFAULT_SOUND)
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val orderId = data["order_id"]
        val taskIntent = Intent(applicationContext, MainActivity_::class.java)
        taskIntent.putExtra("order_id", orderId)
        val contentIntent = PendingIntent.getActivity(applicationContext, 0, taskIntent, 0)
        val message = builder.setContentIntent(contentIntent)
                .build()
        manager.notify(orderId?.toIntOrNull() ?: 0, message)
        EventBus.getDefault().post(PushModel())
        super.onMessageReceived(remoteMessage)
    }
}