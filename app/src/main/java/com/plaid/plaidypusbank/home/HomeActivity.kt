package com.plaid.plaidypusbank.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.plaid.plaidypusbank.R

class HomeActivity : AppCompatActivity() {
  lateinit var navController: NavController

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.home_activity)
    navController = Navigation.findNavController(this@HomeActivity, R.id.mainNavigationFragment)
  }
}
