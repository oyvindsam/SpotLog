package com.samudev.spotlog.preference

import android.content.Context
import android.util.AttributeSet
import com.samudev.spotlog.R


class TimeoutPreference(context: Context, attributeSet: AttributeSet) : BaseDialogPref(context, attributeSet) {

    override val defaultValue: Int
        get() = PrefsFragment.PREF_TIMEOUT_DEFAULT

    override val layoutResourceId: Int
        get() = R.layout.pref_number_picker
}