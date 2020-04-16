package com.developments.samu.spotlog.preference

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.NumberPicker
import com.developments.samu.spotlog.R


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

    // should already be correct by its valueChangeListener
    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) prefImpl.saveValue()
    }

    companion object {

        fun newInstance(preference: Preference): TimeoutPickerDialogFrag {
            return TimeoutPickerDialogFrag().apply {
                arguments = Bundle(1).apply { putString(ARG_KEY, preference.key) }
            }
        }
    }

}