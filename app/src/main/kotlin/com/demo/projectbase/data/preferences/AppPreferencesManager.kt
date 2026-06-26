package com.demo.projectbase.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class AppPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    var isOnboardingComplete: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false)
        set(value) = prefs.edit { putBoolean(KEY_ONBOARDING_COMPLETE, value) }

    private companion object {
        const val PREFS_NAME = "app_prefs"
        const val KEY_ONBOARDING_COMPLETE = "onboarding_complete"
    }
}
