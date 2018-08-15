package com.samudev.spotlog.preference

import android.content.Context
import android.content.res.TypedArray
import android.os.Parcelable
import android.support.v7.preference.DialogPreference
import android.util.AttributeSet
import com.samudev.spotlog.R


class NumberPickerPreference(context: Context, attributeSet: AttributeSet) : DialogPreference(context, attributeSet) {

    var selectedValue = 1
        set(value) {
            field = value
            persistInt(value)
        }

    override fun onGetDefaultValue(a: TypedArray?, index: Int): Any {
        return a?.getInt(index, 0) ?: PREF_MAX_SONGS_DEFAULT
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        if (defaultValue == null) {
            selectedValue = getPersistedInt(PREF_MAX_SONGS_DEFAULT)
        }
        else selectedValue = defaultValue as Int
    }

    override fun getDialogLayoutResource() = R.layout.pref_number_picker


    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        if (isPersistent) return superState

        val myState = SavedState(superState)
        myState.value = selectedValue
        return myState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state == null || state.javaClass != SavedState::class.java) {
            return super.onRestoreInstanceState(state)
        }
        val myState = state as SavedState
        super.onRestoreInstanceState(myState.superState)
        selectedValue = myState.value
    }

    companion object {
        const val PREF_MAX_SONGS_DEFAULT = 30
        const val PREF_MAX_SONGS_KEY = "PREF_MAX_SONGS_KEY"
    }
}