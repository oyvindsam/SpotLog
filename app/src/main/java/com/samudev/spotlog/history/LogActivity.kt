package com.samudev.spotlog.history

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import com.samudev.spotlog.R
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.data.db.AppDatabase
import kotlinx.android.synthetic.main.history_act.*

class LogActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    companion object {
        private val LOG_TAG: String = LogActivity::class.java.simpleName
    }

    private lateinit var logPresenter: LogPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_act)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val historyListFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as LogFragment? ?: LogFragment.newInstance().also {
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, it).commit()
        }

        val db = AppDatabase.getAppDatabase(applicationContext) ?: throw IllegalStateException("Database not found!")

        logPresenter = LogPresenter(db, historyListFragment)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {
                logPresenter.loadSongs()
            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {
                logPresenter.start()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
