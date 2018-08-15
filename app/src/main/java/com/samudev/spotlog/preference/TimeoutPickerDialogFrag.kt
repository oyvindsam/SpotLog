package com.samudev.spotlog.preference

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.NumberPicker
import com.samudev.spotlog.R


class TimeoutPickerDialogFrag : PreferenceDialogFragmentCompat() {

    private lateinit var numPicker: NumberPicker
    private lateinit var prefImpl: TimeoutPreference

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        if (view == null) return

        numPicker = view.findViewById(R.id.pref_log_size_number_picker)
        prefImpl = preference as TimeoutPreference
        with(numPicker) {
            maxValue = 30
            minValue = 5
            value = prefImpl.selectedValue
            setOnValueChangedListener(prefImpl)
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) prefImpl.selectedValue = numPicker.value
    }

    companion object {

        fun newInstance(preference: Preference): TimeoutPickerDialogFrag {
            return TimeoutPickerDialogFrag().apply {
                arguments = Bundle(1).apply { putString(ARG_KEY, preference.key) }
            }
        }
    }

}