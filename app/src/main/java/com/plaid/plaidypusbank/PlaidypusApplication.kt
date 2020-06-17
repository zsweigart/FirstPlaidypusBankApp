package com.plaid.plaidypusbank

import android.app.Application
import com.facebook.stetho.Stetho
import com.google.firebase.iid.FirebaseInstanceId
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltAndroidApp
class PlaidypusApplication : Application() {

  companion object {
    const val TAG = "PlaidypusApplication"
  }

  override fun onCreate() {
    super.onCreate()
    if (BuildConfig.DEBUG) {
      Stetho.initializeWithDefaults(this)
    }
  }

  fun onLogout() {
    GlobalScope.launch {
      withContext(Dispatchers.IO) {
        FirebaseInstanceId.getInstance().deleteInstanceId()
      }
    }
  }
}
