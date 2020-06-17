package com.plaid.plaidypusbank.home.approve

import com.plaid.plaidypusbank.models.PendingPushApproval

sealed class ApproveState {
  object Unauthorized : ApproveState()
  data class Authorized(val pushApproval: PendingPushApproval?) : ApproveState()
}
