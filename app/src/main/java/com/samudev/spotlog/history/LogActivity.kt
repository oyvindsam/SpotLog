package com.samudev.spotlog.history

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.samudev.spotlog.R
import com.samudev.spotlog.databinding.LogActivityBinding

class LogActivity : AppCompatActivity() {

    companion object {
        private val LOG_TAG: String = LogActivity::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LogActivityBinding = DataBindingUtil.setContentView(this, R.layout.log_activity)

        setSupportActionBar(binding.toolbar)

        val historyListFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as LogFragment? ?: LogFragment.newInstance().also {
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, it).commit()
        }

    }

}
