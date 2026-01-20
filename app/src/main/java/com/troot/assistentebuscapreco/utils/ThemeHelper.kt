package com.troot.assistentebuscapreco.utils

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    private const val PREFS_NAME = "theme_preferences"
    private const val KEY_THEME = "selected_theme"

    const val THEME_LIGHT = 0
    const val THEME_DARK = 1
    const val THEME_SYSTEM = 2

    fun applyTheme(context: Context) {
        val theme = getSavedTheme(context)
        when (theme) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun setTheme(context: Context, themeMode: Int) {
        val prefs = getPreferences(context)
        prefs.edit().putInt(KEY_THEME, themeMode).apply()
        applyThemeWithAnimation(context, themeMode)
    }

    // NOVA FUNÇÃO: Aplica tema com animação suave
    private fun applyThemeWithAnimation(context: Context, themeMode: Int) {
        if (context is Activity) {
            // Salva posição do scroll antes de recreate
            context.window.setWindowAnimations(android.R.style.Animation_Dialog)
        }

        when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            THEME_SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    fun getSavedTheme(context: Context): Int {
        val prefs = getPreferences(context)
        return prefs.getInt(KEY_THEME, THEME_SYSTEM)
    }

    fun isDarkMode(context: Context): Boolean {
        return when (getSavedTheme(context)) {
            THEME_DARK -> true
            THEME_LIGHT -> false
            else -> {
                val nightMode = context.resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK
                nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
            }
        }
    }

    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
}
