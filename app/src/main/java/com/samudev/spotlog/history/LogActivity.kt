package com.samudev.spotlog.history

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.samudev.spotlog.R
import com.samudev.spotlog.data.AppDatabase
import kotlinx.android.synthetic.main.history_act.*

class LogActivity : AppCompatActivity() {

    companion object {
        private val LOG_TAG: String = LogActivity::class.java.simpleName
    }

    private lateinit var logPresenter: LogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_act)

        setSupportActionBar(toolbar)

        val historyListFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as LogFragment? ?: LogFragment.newInstance().also {
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, it).commit()
        }

        val db = AppDatabase.getAppDatabase(applicationContext) ?: throw IllegalStateException("Database not found!")

        logPresenter = LogPresenter(db, historyListFragment)
    }

}
