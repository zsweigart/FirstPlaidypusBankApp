package com.plaid.plaidypusbank.home

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.plaid.plaidypusbank.PlaidypusApplication
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.notification.PlaidypusNotificationService
import com.plaid.plaidypusbank.state.UserManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {
  lateinit var navController: NavController

  @Inject
  lateinit var userManager: UserManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.home_activity)
    navController = Navigation.findNavController(this@HomeActivity, R.id.mainNavigationFragment)
    if (intent.hasExtra(PlaidypusNotificationService.APPROVE_REQUEST)) {
      navController.navigate(R.id.home_approve_nav_item)
    }
  }

  override fun onBackPressed() {
    when (navController.currentDestination?.id) {
      R.id.home_main_nav_item -> {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)//, R.style.AlertDialogCustom)
          .setTitle("Logout")
          .setMessage("Are you sure you want to logout?")
          .setPositiveButton("LOGOUT") { d, _ ->
            userManager.logout()
            (application as PlaidypusApplication).onLogout()
            d.dismiss()
            navController.navigate(R.id.home_landing_nav_item)
          }
          .setNegativeButton("CANCEL") { d, _ ->
            d.dismiss()
          }
          .show()
      }
      R.id.home_approve_nav_item -> {
        if (userManager.sessionActive) {
          navController.navigate(R.id.home_approve_back_action)
        } else {
          navController.navigate(R.id.home_approve_unauthorized_action)
        }
      }
      else -> {
        super.onBackPressed()
      }
    }
  }
}
