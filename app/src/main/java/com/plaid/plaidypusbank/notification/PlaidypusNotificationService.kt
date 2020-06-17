package com.plaid.plaidypusbank.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.receiver.NotificationActionReceiver

class PlaidypusNotificationService : FirebaseMessagingService() {

  companion object {
    const val NOTIFICATION_ID = 100

    const val PUSH_ID = "push_id"
    const val ACCEPTED = "accepted"
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
  }

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    Log.i("ZDS", "" + message.messageId)
    Log.i("ZDS", "" + message.data.entries.joinToString { "${it.key} : ${it.value}\n" })

    createChannel()

    val notification: Notification =
      NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
        .setContentTitle(message.notification?.title ?: "Data Access Request")
        .setContentText(message.notification?.body ?: "Do you wish to grant access")
        .setStyle(
          NotificationCompat.BigTextStyle()
            .bigText(message.notification?.body ?: "Do you wish to grant access")
        )
        .addAction(getApproveAction(message.data["pushId"] ?: ""))
        .addAction(getRejectAction(message.data["pushId"] ?: ""))
        .setSmallIcon(R.drawable.plaid)
        .build()
    val manager = NotificationManagerCompat.from(applicationContext)
    manager.notify(NOTIFICATION_ID, notification)
  }

  private fun createChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      // Create the NotificationChannel
      val name = getString(R.string.default_notification_channel_id)
      val descriptionText = getString(R.string.app_name)
      val importance = NotificationManager.IMPORTANCE_DEFAULT
      val mChannel = NotificationChannel(getString(R.string.default_notification_channel_id), name, importance)
      mChannel.description = descriptionText
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
      notificationManager.createNotificationChannel(mChannel)
    }
  }

  private fun getApproveAction(pushId: String): NotificationCompat.Action {
    val approveIntent =
      Intent(this, NotificationActionReceiver::class.java).setAction(getString(R.string.notification_action_approve))
        .apply {
          putExtra(PUSH_ID, pushId)
          putExtra(ACCEPTED, true)
        }
    val approvePendingIntent: PendingIntent =
      PendingIntent.getBroadcast(this, 1, approveIntent, PendingIntent.FLAG_CANCEL_CURRENT)
    return NotificationCompat.Action(R.drawable.done, "Accept", approvePendingIntent)
  }

  private fun getRejectAction(pushId: String): NotificationCompat.Action {
    val rejectIntent =
      Intent(this, NotificationActionReceiver::class.java).setAction(getString(R.string.notification_action_reject))
        .apply {
          putExtra(PUSH_ID, pushId)
          putExtra(ACCEPTED, false)
        }
    val rejectPendingIntent: PendingIntent =
      PendingIntent.getBroadcast(this, 1, rejectIntent, PendingIntent.FLAG_CANCEL_CURRENT)
    Log.i("ZDS", "ADD REJECT ACTION")
    return NotificationCompat.Action(R.drawable.done, "Reject", rejectPendingIntent)
  }
}
