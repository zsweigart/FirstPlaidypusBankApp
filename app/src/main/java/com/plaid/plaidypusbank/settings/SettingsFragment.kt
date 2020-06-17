package com.plaid.plaidypusbank.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.plaid.plaidypusbank.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
  private val viewModel: SettingsViewModel by viewModels()

  lateinit var biometricManager: BiometricManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    biometricManager = BiometricManager.from(requireContext())
  }

  override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
    setPreferencesFromResource(R.xml.root_preferences, rootKey)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.fingerprintEnabled.observe(this.viewLifecycleOwner, Observer(::setFingerprintPref))
  }

  override fun onResume() {
    super.onResume()
    preferenceScreen.sharedPreferences
      .registerOnSharedPreferenceChangeListener(this)
  }

  override fun onPause() {
    super.onPause()
    preferenceScreen.sharedPreferences
      .unregisterOnSharedPreferenceChangeListener(this)
  }

  fun setFingerprintPref(fingerprintEnabled: Boolean) {
    val prefs: SharedPreferences.Editor = PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
    prefs.putBoolean("fingerprint", fingerprintEnabled)
    findPreference<SwitchPreferenceCompat>("fingerprint")?.let {
      if (it.isChecked != fingerprintEnabled) {
        it.isChecked = fingerprintEnabled
      }
    }

    prefs.apply()
  }

  override fun onSharedPreferenceChanged(
    sharedPreferences: SharedPreferences?,
    key: String?
  ) {
    when (key) {
      "fingerprint" -> {
        findPreference<SwitchPreferenceCompat>("fingerprint")?.let {
          if (it.isChecked) {
            if (biometricManager.canAuthenticate() == BiometricManager.BIOMETRIC_SUCCESS) {
              showBiometrics()
            } else {
              Toast.makeText(requireActivity(), "Fingerprint sensor not found or not setup", Toast.LENGTH_SHORT).show()
            }
          } else {
            viewModel.disableFingerprint()
          }
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
        }

        override fun onAuthenticationSucceeded(
          result: BiometricPrompt.AuthenticationResult
        ) {
          super.onAuthenticationSucceeded(result)
          Toast.makeText(
            requireActivity(),
            "Fingerprint addeed!", Toast.LENGTH_SHORT
          )
            .show()
          viewModel.enableFingerprint()
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          Toast.makeText(
            requireActivity(),
            "Unable to authenticate with fingerprint", Toast.LENGTH_SHORT
          )
            .show()
        }
      })

    biometricPrompt.authenticate(
      BiometricPrompt.PromptInfo.Builder()
        .setTitle("Setup Fingerprint for First Plaidypus Bank")
        .setSubtitle("Enable log in using your fingerprint credential")
        .setNegativeButtonText("SKIP")
        .build()
    )
  }
}
