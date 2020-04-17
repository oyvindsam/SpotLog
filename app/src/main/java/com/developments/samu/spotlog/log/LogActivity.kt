package com.developments.samu.spotlog.log

import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.developments.samu.spotlog.R
import com.developments.samu.spotlog.databinding.LogActivityBinding

private val LOG_TAG: String = LogActivity::class.java.simpleName

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: LogActivityBinding = DataBindingUtil.setContentView(this, R.layout.log_activity)

        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
                .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Adds up button when there is a destination in the back stack.
        NavigationUI.setupActionBarWithNavController(this, navController)

    }

    override fun onSupportNavigateUp() = findNavController(R.id.nav_host_fragment).navigateUp()
}
