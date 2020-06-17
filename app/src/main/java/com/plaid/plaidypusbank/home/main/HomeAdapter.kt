package com.plaid.plaidypusbank.home.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.models.Account

class HomeAdapter : RecyclerView.Adapter<AccountHolder>() {

  private val items = listOf<Account>(Account.Checking, Account.Savings, Account.Credit, Account.Investment)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountHolder {
    val inflater = LayoutInflater.from(parent.context)
    return AccountHolder(inflater.inflate(R.layout.account_layout, parent, false))
  }

  override fun getItemCount(): Int = items.size

  override fun onBindViewHolder(holder: AccountHolder, position: Int) {
    holder.bind(items[position])
  }
}

class AccountHolder(view: View) : RecyclerView.ViewHolder(view) {

  private val icon: ImageView = view.findViewById(R.id.account_icon)
  private val name: TextView = view.findViewById(R.id.account_name)
  private val balance: TextView = view.findViewById(R.id.account_balance)
  private val action1: TextView = view.findViewById(R.id.account_action_one)
  private val action2: TextView = view.findViewById(R.id.account_action_two)
  private val action3: TextView = view.findViewById(R.id.account_action_three)

  fun bind(account: Account) {
    icon.setBackgroundResource(account.iconRes)
    name.text = account.name
    balance.text = account.balance
    action1.text = account.actions[0]
    action2.text = account.actions[1]
    if (account.actions.size > 2) {
      action3.text = account.actions[2]
      action3.visibility = View.VISIBLE
    }
  }
}
