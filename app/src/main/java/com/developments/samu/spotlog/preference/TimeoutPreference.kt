package com.developments.samu.spotlog.preference

import android.content.Context
import android.util.AttributeSet
import com.developments.samu.spotlog.R


class TimeoutPreference(context: Context, attributeSet: AttributeSet) : BaseDialogPref(context, attributeSet) {

    override val defaultValue: Int
        get() = PrefsFragment.PREF_TIMEOUT_DEFAULT

    override fun getDialogLayoutResource(): Int = R.layout.pref_number_picker
}