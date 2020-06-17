package com.plaid.plaidypusbank.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.plaid.plaidypusbank.notification.PlaidypusNotificationService.Companion.ACCEPTED
import com.plaid.plaidypusbank.notification.PlaidypusNotificationService.Companion.NOTIFICATION_ID
import com.plaid.plaidypusbank.notification.PlaidypusNotificationService.Companion.PUSH_ID
import com.plaid.plaidypusbank.service.NotificationActionService

class NotificationActionReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    Log.i("ZDS", "NOTIFICATION ACTION RECEIVER")
    context?.let { c ->
      intent?.let { i ->
        i.getStringExtra(PUSH_ID)?.let { pushId ->
          c.startService(
            Intent(c, NotificationActionService::class.java).apply {
              putExtra(PUSH_ID, pushId)
              putExtra(ACCEPTED, i.getBooleanExtra(ACCEPTED, false))
            })
        }

        val manager = NotificationManagerCompat.from(c)
        manager.cancel(NOTIFICATION_ID)
      }
    }
  }
}
