package com.plaid.plaidypusbank.home

import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.plaid.plaidypusbank.PlaidypusApplication
import com.plaid.plaidypusbank.R
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
  }

  override fun onBackPressed() {
    if (navController.currentDestination?.id == R.id.home_main_nav_item) {
      AlertDialog.Builder(this, R.style.AlertDialogTheme)//, R.style.AlertDialogCustom)
        .setTitle("Logout")
        .setMessage("Are you sure you want to logout?")
        .setPositiveButton("LOGOUT") { d, _ ->
          userManager.logout()
          (application as PlaidypusApplication).onLogout()
          d.dismiss()
          super.onBackPressed()
        }
        .setNegativeButton("CANCEL") { d, _ ->
          d.dismiss()
        }
        .show()
    } else {
      super.onBackPressed()
    }
  }
}
