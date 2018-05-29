package com.samudev.spotlog.history

import com.samudev.spotlog.BasePresenter
import com.samudev.spotlog.BaseView


interface HistoryContract {

    interface View : BaseView<Presenter> {
        fun showToast(message: String)

    }

    interface Presenter : BasePresenter {

    }
}
