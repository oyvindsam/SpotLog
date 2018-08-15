package com.samudev.spotlog.preference

import android.content.Context
import android.content.res.TypedArray
import android.os.Parcelable
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import android.widget.NumberPicker

abstract class BaseDialogPref(context: Context, attributeSet: AttributeSet) :
        DialogPreference(context, attributeSet), NumberPicker.OnValueChangeListener {

    abstract val layoutResourceId: Int
    abstract val defaultValue: Int

    var selectedValue = 1
        set(value) {
            field = value
            persistInt(value)
        }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getInt(index, 0) ?: defaultValue
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        if (defaultValue == null) {
            selectedValue = getPersistedInt(this.defaultValue)
        }
        else selectedValue = defaultValue as Int
    }

    override fun getDialogLayoutResource() = layoutResourceId

    // get the value from the numberpicker so it survices configuration change
    override fun onValueChange(numberPicker: NumberPicker, from: Int, to: Int) {
        selectedValue = to
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) return superState

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