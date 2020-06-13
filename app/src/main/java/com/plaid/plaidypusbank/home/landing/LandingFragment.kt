package com.plaid.plaidypusbank.home.landing

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.home.HomeActivity
import com.plaid.plaidypusbank.home.main.HomeMainFragment
import com.plaid.plaidypusbank.home.main.HomeMainViewModel

class LandingFragment : Fragment() {

  val viewModel: HomeMainViewModel by viewModels()

  companion object {
    fun newInstance() = HomeMainFragment()
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val root = inflater.inflate(R.layout.home_landing_fragment, container, false)
    val imageView = root.findViewById<ImageView>(R.id.logo)
    val title = root.findViewById<TextView>(R.id.title)

    root.findViewById<TextView>(R.id.landing_login).setOnClickListener {
      val extras = FragmentNavigatorExtras(
        imageView to imageView.transitionName,
        title to title.transitionName
      )
      (requireActivity() as HomeActivity).navController.navigate(R.id.home_login_action, null, null, extras)
    }
    return root
  }

}
