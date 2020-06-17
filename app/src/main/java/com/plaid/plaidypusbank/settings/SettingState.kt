package com.plaid.plaidypusbank.settings

sealed class SettingState {
  object Default : SettingState()
  object BiometricsPrompt : SettingState()
}
