package com.sensorapp.impactlib

import android.os.Bundle
import android.text.InputType
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        if (context == null) {
            return
        }

        val prefItems = arguments?.getParcelableArrayList<PrefItem>("PREFS") ?: listOf()

        prefItems.forEach {
            val pref = EditTextPreference(requireContext())
            pref.key = it.key
            pref.title = it.title
            pref.setDefaultValue(it.default.toString())
            pref.summaryProvider = EditTextPreference.SimpleSummaryProvider.getInstance()
            pref.setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER + InputType.TYPE_NUMBER_FLAG_DECIMAL
            }

            preferenceScreen.addPreference(pref)
        }
    }

    companion object {
        fun newInstance(bundle: Bundle?): SettingsFragment {
            val settingsFragment = SettingsFragment()
            settingsFragment.arguments = bundle
            return settingsFragment
        }
    }
}