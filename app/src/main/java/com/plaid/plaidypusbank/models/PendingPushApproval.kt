package com.plaid.plaidypusbank.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PendingPushApproval(val title: String, val message: String, val pushId: String) : Parcelable
