package com.samudev.spotlog.history


class HistoryPresenter(val historyView: HistoryContract.View) : HistoryContract.Presenter {

    init {
        historyView.presenter = this
    }

    override fun start() {
        historyView.showToast("Heeeeelo")
    }
}