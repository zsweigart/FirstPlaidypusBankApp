package com.plaid.plaidypusbank.networking

import com.plaid.plaidypusbank.networking.models.LoginRequest
import com.plaid.plaidypusbank.networking.models.LoginResponse
import com.plaid.plaidypusbank.networking.models.PushResponseRequest
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface BankRetrofitApi {

  @POST("/app/login")
  fun loginAsync(@Body loginRequest: LoginRequest): Deferred<Response<LoginResponse>>

  @POST("/app/logout")
  fun logoutAsync(@Body loginRequest: LoginRequest): Deferred<Response<LoginResponse>>

  @POST("/app/push/response")
  fun respondToPushAsync(@Body pushResponseRequest: PushResponseRequest): Deferred<Response<Any>>
}
