package com.plaid.plaidypusbank.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.plaid.plaidypusbank.networking.BankApi
import com.plaid.plaidypusbank.notification.PlaidypusNotificationService.Companion.ACCEPTED
import com.plaid.plaidypusbank.notification.PlaidypusNotificationService.Companion.PUSH_ID
import com.plaid.plaidypusbank.storage.PendingPushStorage
import com.plaid.plaidypusbank.storage.UserStorage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class NotificationActionService : IntentService("NotificationActionService") {

  @Inject
  lateinit var bankApi: BankApi

  @Inject
  lateinit var userStorage: UserStorage

  @Inject
  lateinit var pendingPushStorage: PendingPushStorage

  override fun onHandleIntent(intent: Intent?) {
    Log.i("ZDS", "NOTIFICATION ACTION SERVICE")
    intent?.let { i ->
      runBlocking {
        intent.getStringExtra(PUSH_ID)?.let {
          val result = withContext(Dispatchers.IO) {
            userStorage.loadUser()?.let { user ->
              bankApi.respondToPushAsync(it, user.sessionId, i.getBooleanExtra(ACCEPTED, false))
            }
          }
          result?.await()
        }
      }
    }
    pendingPushStorage.clear()
  }
}
