package com.plaid.plaidypusbank.networking

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.plaid.plaidypusbank.BuildConfig
import com.plaid.plaidypusbank.networking.models.LoginRequest
import com.plaid.plaidypusbank.networking.models.PushResponseRequest
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BankApi @Inject constructor() {

  companion object {
    const val BASE_URL = "http://19ee4830e4f2.ngrok.io"
  }

  private val retrofit: Retrofit

  private val bankRetrofitApi: BankRetrofitApi

  init {
    val okHttpClientBuilder = OkHttpClient.Builder()
    okHttpClientBuilder.callTimeout(30, TimeUnit.SECONDS)

    if (BuildConfig.DEBUG) {
      okHttpClientBuilder.addNetworkInterceptor(StethoInterceptor())
    }

    retrofit = Retrofit.Builder()
      .baseUrl(BASE_URL)
      .client(okHttpClientBuilder.build())
      .addCallAdapterFactory(CoroutineCallAdapterFactory())
      .addConverterFactory(GsonConverterFactory.create())
      .build()
    bankRetrofitApi = retrofit.create(BankRetrofitApi::class.java)
  }

  suspend fun login(email: String, password: String, pushToken: String) =
    //Response.success(LoginResponse("session-b572cd20-83a2-4cec-838e-8d0430a6b2c5"))
    bankRetrofitApi.loginAsync(LoginRequest(email, password, pushToken)).await()

  suspend fun respondToPushAsync(sessionToken: String, pushId: String, approved: Boolean) =
    bankRetrofitApi.respondToPushAsync(PushResponseRequest(sessionToken, pushId, approved))
}
