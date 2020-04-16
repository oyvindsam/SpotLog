package com.developments.samu.spotlog.preference

import android.content.Context
import android.content.res.TypedArray
import android.os.Parcelable
import androidx.preference.DialogPreference
import android.util.AttributeSet
import android.widget.NumberPicker


/**
 * Implements most of the logic for a DialogPreference, including rotation events. See implementation
 * in TimeoutPreference, and UI impl in TimeoutPickerDialogFrag
 */
abstract class BaseDialogPref(context: Context, attributeSet: AttributeSet) :
        DialogPreference(context, attributeSet), NumberPicker.OnValueChangeListener {

    abstract val defaultValue: Int
    abstract override fun getDialogLayoutResource(): Int

    var selectedValue = 0

    fun saveValue() = persistInt(selectedValue)

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getInt(index, 0) ?: defaultValue
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        selectedValue = if (defaultValue is Int) defaultValue else getPersistedInt(this.defaultValue)
    }

    // get the value from the numberpicker so it survives configuration change
    override fun onValueChange(numberPicker: NumberPicker, from: Int, to: Int) {
        selectedValue = to
    }

    override fun onSaveInstanceState(): Parcelable {

        val superState = super.onSaveInstanceState()

        //if (isPersistent) return superState

        val myState = SavedState(superState)
        myState.value = selectedValue
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state !is SavedState) {
            return super.onRestoreInstanceState(state)
        }
        super.onRestoreInstanceState(state.superState)
        selectedValue = state.value
    }
}