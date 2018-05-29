package com.samudev.spotlog.history

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils.replace
import android.view.MenuItem
import com.samudev.spotlog.R
import com.samudev.spotlog.R.id.drawer_layout
import com.samudev.spotlog.background.SpotifyReceiver
import kotlinx.android.synthetic.main.history_act.*

class HistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var historyPresenter: HistoryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_act)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val historyFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
            as HistoryFragment? ?: HistoryFragment.newInstance().also {
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, it).commit()
        }

        historyPresenter = HistoryPresenter(historyFragment)

        registerBroadcastReceiver()
    }

    private fun registerBroadcastReceiver() {
        val spotifyIntent = IntentFilter("com.spotify.music.playbackstatechanged")
        this.registerReceiver(SpotifyReceiver(), spotifyIntent)
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

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {
                historyPresenter.start()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

}
