package com.sensorapp.impactlib

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        context?.let {
            val pref = EditTextPreference(it)
            pref.key = "age"
            pref.title = "Age"
            pref.setDefaultValue("52")
            pref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            pref.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }

            preferenceScreen.addPreference(pref)
        }
    }
}