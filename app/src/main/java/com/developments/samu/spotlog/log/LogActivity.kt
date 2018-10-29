package com.developments.samu.spotlog.log

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.developments.samu.spotlog.R
import com.developments.samu.spotlog.databinding.LogActivityBinding

private val LOG_TAG: String = LogActivity::class.java.simpleName

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LogActivityBinding = DataBindingUtil.setContentView(this, R.layout.log_activity)

        setSupportActionBar(binding.toolbar)

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        // Adds up button when there is a destination in the back stack.
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()
}
