package com.plaid.plaidypusbank.home.landing

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.home.HomeActivity
import com.plaid.plaidypusbank.home.main.HomeMainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : Fragment() {

  private val sharedViewModel: HomeMainViewModel by viewModels()
  private val landingViewModel: LandingViewModel by viewModels()

  private lateinit var imageView: ImageView
  private lateinit var title: TextView
  private lateinit var emailField: EditText
  private lateinit var passwordField: EditText
  private lateinit var loginButton: TextView
  private lateinit var progressBar: ProgressBar
  private lateinit var landingLoginButtons: ConstraintLayout

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    val root = inflater.inflate(R.layout.home_landing_fragment, container, false)
    imageView = root.findViewById(R.id.logo)
    title = root.findViewById(R.id.title)
    emailField = root.findViewById(R.id.landing_email)
    passwordField = root.findViewById(R.id.landing_password)
    loginButton = root.findViewById(R.id.landing_login)
    progressBar = root.findViewById(R.id.landing_progress_bar)
    landingLoginButtons = root.findViewById(R.id.landing_login_fields)

    passwordField.setOnEditorActionListener { v, actionId, event ->
      if (actionId == EditorInfo.IME_ACTION_GO
        || actionId == EditorInfo.IME_ACTION_DONE
        || actionId == EditorInfo.IME_ACTION_NEXT
      ) {
        landingViewModel.login(emailField.text.toString(), passwordField.text.toString())
        hideKeyboard(root)
        true
      }
      false
    }

    landingViewModel.landingState.observe(viewLifecycleOwner, Observer(::updateUi))

    root.findViewById<TextView>(R.id.landing_login).setOnClickListener {
      landingViewModel.login(emailField.text.toString(), passwordField.text.toString())
    }

    return root
  }

  private fun updateUi(landingState: LandingState) {
    when (landingState) {
      LandingState.Login -> showLogin()
      LandingState.Loading -> showProgress()
      LandingState.LoggedIn -> {
        passwordField.text.clear()
        goToHome()
        landingViewModel.landingState.value = LandingState.Login
      }
      is LandingState.Error -> showLogin()
    }
  }

  private fun goToHome() {
    val extras = FragmentNavigatorExtras(
      imageView to imageView.transitionName,
      title to title.transitionName
    )
    (requireActivity() as HomeActivity).navController.navigate(R.id.home_login_action, null, null, extras)
  }

  private fun showLogin() {
    progressBar.visibility = View.INVISIBLE
    landingLoginButtons.visibility = View.VISIBLE
  }

  private fun showProgress() {
    progressBar.visibility = View.VISIBLE
    landingLoginButtons.visibility = View.INVISIBLE
  }

  fun hideKeyboard(view: View) {
    val inputManager: InputMethodManager =
      view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val binder = view.windowToken
    inputManager.hideSoftInputFromWindow(
      binder,
      InputMethodManager.HIDE_NOT_ALWAYS
    )
  }
}
