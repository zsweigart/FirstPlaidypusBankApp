package com.plaid.plaidypusbank.landing

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.home.HomeActivity

class LandingActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.home_landing_fragment)

    findViewById<TextView>(R.id.landing_login).setOnClickListener {
      startActivity(Intent(this@LandingActivity, HomeActivity::class.java))
    }
  }
}
