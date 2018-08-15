package com.samudev.spotlog.preference

import android.os.Parcel
import android.os.Parcelable
import android.preference.Preference


class SavedState : Preference.BaseSavedState {
    // Member that holds the setting's value
    // Change this data type to match the type saved by your Preference
    internal var value: Int = 0

    constructor(superState: Parcelable) : super(superState)

    constructor(source: Parcel) : super(source) {
        // Get the current preference's value
        value = source.readInt()  // Change this to read the appropriate data type
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        super.writeToParcel(dest, flags)
        // Write the preference's value
        dest.writeInt(value)  // Change this to write the appropriate data type
    }

    companion object {

        // Standard creator object using an instance of this class
        val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {

            override fun createFromParcel(inn: Parcel): SavedState {
                return SavedState(inn)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}