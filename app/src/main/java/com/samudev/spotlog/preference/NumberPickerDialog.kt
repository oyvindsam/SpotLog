package com.samudev.spotlog.preference

import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceDialogFragmentCompat
import android.view.View
import android.widget.NumberPicker
import com.samudev.spotlog.R


class NumberPickerDialogFrag : PreferenceDialogFragmentCompat() {

    private lateinit var numPicker: NumberPicker

    override fun onBindDialogView(view: View?) {
        super.onBindDialogView(view)
        if (view == null) return
        numPicker = view.findViewById(R.id.dialog_number_picker)
        with(numPicker) {
            maxValue = 100
            minValue = 1
            value = (preference as NumberPickerPreference).selectedValue
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) (preference as NumberPickerPreference).selectedValue = numPicker.value
    }

    companion object {

        fun newInstance(preference: Preference): NumberPickerDialogFrag {
            val fragment = NumberPickerDialogFrag()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, preference.key)
            fragment.arguments = bundle
            return fragment
        }
    }

}