package com.samudev.spotlog.history

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.samudev.spotlog.R
import kotlinx.android.synthetic.main.history_act.*

class LogActivity : AppCompatActivity() {

    companion object {
        private val LOG_TAG: String = LogActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_act)

        setSupportActionBar(toolbar)

        val historyListFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as LogFragment? ?: LogFragment.newInstance().also {
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, it).commit()
        }

    }

}
