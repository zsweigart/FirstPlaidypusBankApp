package com.plaid.plaidypusbank.home.approve

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.plaid.plaidypusbank.R
import com.plaid.plaidypusbank.home.HomeActivity
import com.plaid.plaidypusbank.notification.PlaidypusNotificationService
import com.plaid.plaidypusbank.service.NotificationActionService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ApproveFragment : Fragment() {

  private val approveViewModel: ApproveViewModel by viewModels()

  lateinit var imageView: ImageView
  lateinit var title: TextView
  lateinit var pendingTitle: TextView
  lateinit var pendingMessage: TextView

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    val root = inflater.inflate(R.layout.home_approve_fragment, container, false)

    root.findViewById<ImageView>(R.id.approve_deny).setOnClickListener {
      requireContext().startService(
        Intent(requireContext(), NotificationActionService::class.java).apply {
          putExtra(PlaidypusNotificationService.PUSH_ID, approveViewModel.pendingPushApproval?.pushId)
          putExtra(PlaidypusNotificationService.ACCEPTED, false)
        })
      approveViewModel.clearPending()
      goToHome()
    }

    root.findViewById<ImageView>(R.id.approve_approve).setOnClickListener {
      requireContext().startService(
        Intent(requireContext(), NotificationActionService::class.java).apply {
          putExtra(PlaidypusNotificationService.PUSH_ID, approveViewModel.pendingPushApproval?.pushId)
          putExtra(PlaidypusNotificationService.ACCEPTED, true)
        })
      approveViewModel.clearPending()
      goToHome()
    }

    imageView = root.findViewById(R.id.logo)
    title = root.findViewById(R.id.title)
    pendingTitle = root.findViewById(R.id.approve_title)
    pendingMessage = root.findViewById(R.id.approve_message)

    approveViewModel.approveState.observe(viewLifecycleOwner, Observer(::updateUi))

    return root
  }

  private fun updateUi(approveState: ApproveState) {
    if (approveViewModel.user == null || approveViewModel.pendingPushApproval == null) {
      approveViewModel.clearState()
      goToLanding()
    }
    when (approveState) {
      is ApproveState.Authorized -> {
        approveState.pushApproval?.let {
          pendingTitle.text = it.title
          pendingMessage.text = it.message
        }
      }
      ApproveState.Unauthorized -> {
        val biometricManager = BiometricManager.from(requireContext())
        if (approveViewModel.fingerprintEnabled && biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
          showBiometrics()
        } else {
          goToLanding()
        }
      }
    }
  }

  private fun showBiometrics() {
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
          goToLanding()
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
          approveViewModel.startSession()
          approveViewModel.approveState.value = ApproveState.Authorized(approveViewModel.pendingPushApproval)
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          Toast.makeText(
            requireActivity(),
            "Unable to authenticate with fingerprint", Toast.LENGTH_SHORT
          )
            .show()
          goToLanding()
        }
      })

    biometricPrompt.authenticate(
      BiometricPrompt.PromptInfo.Builder()
        .setTitle("Authorize Account for First Plaidypus Bank")
        .setSubtitle("Log in using your fingerprint credential")
        .setNegativeButtonText("SKIP")
        .build()
    )
  }

  private fun goToLanding() {
    (requireActivity() as HomeActivity).navController.navigate(R.id.home_approve_unauthorized_action)
  }

  private fun goToHome() {

    val extras = FragmentNavigatorExtras(
      imageView to imageView.transitionName,
      title to title.transitionName
    )
    (requireActivity() as HomeActivity).navController.navigate(R.id.home_approve_complete_action, null, null, extras)
  }
}
