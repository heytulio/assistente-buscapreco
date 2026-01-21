package com.troot.assistentebuscapreco.data.local

import android.content.Context

class ProfilePrefs(context: Context) {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var hasCompletedOnboarding: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_DONE, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_DONE, value).apply()

    var userName: String
        get() = prefs.getString(KEY_USER_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_USER_NAME, value.trim()).apply()

    /**
     * Preferências como CSV simples.
     * Ex: "eletrônicos,games,mercado"
     */
    var preferencesCsv: String
        get() = prefs.getString(KEY_PREFS_CSV, "") ?: ""
        set(value) = prefs.edit().putString(KEY_PREFS_CSV, value.trim()).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val PREFS_NAME = "assistente_buscapreco_prefs"
        private const val KEY_ONBOARDING_DONE = "onboarding_done"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_PREFS_CSV = "user_prefs_csv"
    }
}
