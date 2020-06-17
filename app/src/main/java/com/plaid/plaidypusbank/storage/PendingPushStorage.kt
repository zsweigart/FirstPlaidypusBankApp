package com.plaid.plaidypusbank.storage

import android.app.Application
import android.content.Context
import androidx.annotation.WorkerThread
import com.google.gson.Gson
import com.plaid.plaidypusbank.models.PendingPushApproval
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingPushStorage @Inject constructor(application: Application, private val gson: Gson) {

  companion object {
    const val PENDING_PUSH_SHARED_PREFS = "pending_push_shared_prefs"
    const val PENDING_PUSH_KEY = "pending_push"
  }

  private val sharedPrefs = application.getSharedPreferences(PENDING_PUSH_SHARED_PREFS, Context.MODE_PRIVATE)

  fun savePendingPush(pendingPushApproval: PendingPushApproval) {
    sharedPrefs.edit().putString(PENDING_PUSH_KEY, gson.toJson(pendingPushApproval)).apply()
  }

  @WorkerThread
  fun loadPendingPush(): PendingPushApproval? =
    gson.fromJson(sharedPrefs.getString(PENDING_PUSH_KEY, ""), PendingPushApproval::class.java)

  fun clear() {
    sharedPrefs.edit().clear().apply()
  }
}
