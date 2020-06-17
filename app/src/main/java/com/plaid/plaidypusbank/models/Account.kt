package com.plaid.plaidypusbank.models

import androidx.annotation.DrawableRes
import com.plaid.plaidypusbank.R

sealed class Account(val name: String, val balance: String, @DrawableRes val iconRes: Int, val actions: List<String>) {

  object Checking : Account("Checking", "$100.00", R.drawable.checking, listOf("Deposit", "Pay", "Statements"))

  object Savings : Account("Savings", "$200.00", R.drawable.savings, listOf("Transfer", "Statements"))

  object Credit : Account("Savings", "$300.00", R.drawable.credit, listOf("Pay", "Statements"))

  object Investment : Account("Savings", "$400.00", R.drawable.investment, listOf("Deposit", "Statements"))
}
