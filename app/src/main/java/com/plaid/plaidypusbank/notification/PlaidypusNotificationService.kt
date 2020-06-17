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
import com.plaid.plaidypusbank.home.HomeActivity
import com.plaid.plaidypusbank.models.PendingPushApproval
import com.plaid.plaidypusbank.receiver.NotificationActionReceiver
import com.plaid.plaidypusbank.storage.PendingPushStorage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlaidypusNotificationService : FirebaseMessagingService() {

  @Inject
  lateinit var pendingPushStorage: PendingPushStorage

  companion object {
    const val NOTIFICATION_ID = 100

    const val PUSH_ID = "push_id"
    const val ACCEPTED = "accepted"
    const val APPROVE_REQUEST = "accepted"
  }

  override fun onNewToken(token: String) {
    super.onNewToken(token)
  }

  override fun onMessageReceived(message: RemoteMessage) {
    super.onMessageReceived(message)
    Log.i("ZDS", "" + message.messageId)
    Log.i("ZDS", "" + message.data.entries.joinToString { "${it.key} : ${it.value}\n" })

    createChannel()

    val title = message.data.get("title") ?: "Data Access Request"
    val content = message.data.get("body") ?: "Do you wish to grant access"
    val pushId = message.data.get("pushId") ?: ""
    val pendingPushApproval = PendingPushApproval(title, content, pushId)
    pendingPushStorage.savePendingPush(pendingPushApproval)

    val notification: Notification =
      NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
        .setContentTitle(title)
        .setContentText(content)
        .setStyle(NotificationCompat.BigTextStyle().bigText(content))
        .addAction(getApproveAction(pushId))
        .addAction(getRejectAction(pushId))
        .setSmallIcon(R.drawable.plaid)
        .setContentIntent(getContentIntent())
        .setAutoCancel(true)
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

  private fun getContentIntent(): PendingIntent {
    val intent = Intent(this, HomeActivity::class.java).apply {
      putExtra(APPROVE_REQUEST, true)
    }
    return PendingIntent.getActivity(this, 300, intent, PendingIntent.FLAG_CANCEL_CURRENT)
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
