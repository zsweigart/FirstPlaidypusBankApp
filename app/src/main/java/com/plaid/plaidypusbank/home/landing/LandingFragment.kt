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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.home.HomeActivity
import com.plaid.plaidypusbank.home.main.HomeMainViewModel
import com.plaid.plaidypusbank.models.User
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandingFragment : Fragment() {

  @Suppress("Unused")
  private val sharedViewModel: HomeMainViewModel by viewModels()
  private val landingViewModel: LandingViewModel by viewModels()

  private lateinit var imageView: ImageView
  private lateinit var title: TextView
  private lateinit var emailField: EditText
  private lateinit var passwordField: EditText
  private lateinit var loginButton: TextView
  private lateinit var progressBar: ProgressBar
  private lateinit var landingLoginButtons: ConstraintLayout
  private lateinit var fingerprintLogin: ImageView

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
    fingerprintLogin = root.findViewById(R.id.fingerprint_login)

    fingerprintLogin.setOnClickListener {
      showBiometrics(BiometricManager.from(requireContext()))
    }

    passwordField.setOnEditorActionListener { _, actionId, _ ->
      if (actionId == EditorInfo.IME_ACTION_GO
        || actionId == EditorInfo.IME_ACTION_DONE
        || actionId == EditorInfo.IME_ACTION_NEXT
      ) {
        landingViewModel.login(emailField.text.toString(), passwordField.text.toString())
        hideKeyboard(root)
        return@setOnEditorActionListener true
      }
      return@setOnEditorActionListener false
    }

    landingViewModel.landingState.observe(viewLifecycleOwner, Observer(::updateUi))

    root.findViewById<TextView>(R.id.landing_login).setOnClickListener {
      landingViewModel.login(emailField.text.toString(), passwordField.text.toString())
    }

    return root
  }

  private fun updateUi(landingState: LandingState) {
    when (landingState) {
      is LandingState.Login -> showLogin(landingState.user)
      LandingState.Loading -> showProgress()
      is LandingState.LoggedIn -> {
        passwordField.text.clear()
        maybeShowEnableDialogPrompt()
        landingViewModel.landingState.value = LandingState.Login(landingState.user)
      }
      is LandingState.Error -> showLogin(landingState.user)
    }
  }

  override fun onResume() {
    super.onResume()
    landingViewModel.initialize()
  }

  private fun maybeShowEnableDialogPrompt() {
    val biometricManager = BiometricManager.from(requireActivity())
    if (landingViewModel.fingerprintEnabled || biometricManager.canAuthenticate() != BiometricManager.BIOMETRIC_SUCCESS) {
      goToHome()
      return
    } else {
      AlertDialog.Builder(requireContext(), R.style.AlertDialogTheme)//, R.style.AlertDialogCustom)
        .setTitle("Enable Fingerprint")
        .setMessage("Do you want to enable fingerprint for easier log in next time?")
        .setPositiveButton("ENABLE") { d, _ ->
          showBiometrics(biometricManager)
        }
        .setNegativeButton("SKIP") { d, _ ->
          goToHome()
        }
        .show()
    }
  }

  private fun showBiometrics(biometricManager: BiometricManager) {
    val executor = ContextCompat.getMainExecutor(requireContext())
    val biometricPrompt = BiometricPrompt(this, executor,
      object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(
          errorCode: Int,
          errString: CharSequence
        ) {
          super.onAuthenticationError(errorCode, errString)
          Toast.makeText(
            requireActivity(),
            "Unable to authenticate with fingerprint", Toast.LENGTH_SHORT
          )
            .show()
          goToHome()
        }

        override fun onAuthenticationSucceeded(
          result: BiometricPrompt.AuthenticationResult
        ) {
          super.onAuthenticationSucceeded(result)
          Toast.makeText(
            requireActivity(),
            "Fingerprint  addeed!", Toast.LENGTH_SHORT
          )
            .show()
          landingViewModel.startSession()
          landingViewModel.enableFingerprint()
          goToHome()
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          Toast.makeText(
            requireActivity(),
            "Unable to authenticate with fingerprint", Toast.LENGTH_SHORT
          )
            .show()
          goToHome()
        }
      })

    biometricPrompt.authenticate(
      BiometricPrompt.PromptInfo.Builder()
        .setTitle("Setup Fingerprint for First Plaidypus Bank")
        .setSubtitle("Log in using your fingerprint credential")
        .setNegativeButtonText("SKIP")
        .build()
    )
  }

  private fun goToHome() {
    val extras = FragmentNavigatorExtras(
      imageView to imageView.transitionName,
      title to title.transitionName
    )
    (requireActivity() as HomeActivity).navController.navigate(R.id.home_login_action, null, null, extras)
  }

  private fun showLogin(user: User?) {
    progressBar.visibility = View.INVISIBLE
    landingLoginButtons.visibility = View.VISIBLE
    if (user == null || !landingViewModel.fingerprintEnabled) {
      fingerprintLogin.visibility = View.GONE
    } else {
      fingerprintLogin.visibility = View.VISIBLE
    }
  }

  private fun showProgress() {
    progressBar.visibility = View.VISIBLE
    landingLoginButtons.visibility = View.INVISIBLE
  }

  private fun hideKeyboard(view: View) {
    val inputManager: InputMethodManager =
      view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val binder = view.windowToken
    inputManager.hideSoftInputFromWindow(
      binder,
      InputMethodManager.HIDE_NOT_ALWAYS
    )
  }
}
