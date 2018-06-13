package com.samudev.spotlog.history

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.samudev.spotlog.R
import android.content.Intent
import android.support.v4.widget.DrawerLayout
import com.samudev.spotlog.R.id.drawer_layout
import com.samudev.spotlog.R.id.nav_view
import com.samudev.spotlog.data.Song
import com.samudev.spotlog.history.HistoryAdapter.HistoryItemListener


class HistoryActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
        HistoryItemListener {

    companion object {
        private val LOG_TAG: String = HistoryActivity::class.java.simpleName
    }

    private lateinit var historyPresenter: HistoryPresenter
    private lateinit var spotifyReceiver: SpotifyReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history_act)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        val drawerLayout = findViewById<DrawerLayout>(drawer_layout)

        val toggle = ActionBarDrawerToggle(
                this, findViewById(drawer_layout), toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        findViewById<NavigationView>(nav_view).setNavigationItemSelectedListener(this)

        val historyListFragment = supportFragmentManager.findFragmentById(R.id.contentFrame)
                as HistoryFragment? ?: HistoryFragment.newInstance().also {
            supportFragmentManager.beginTransaction().add(R.id.contentFrame, it).commit()
        }

        historyPresenter = HistoryPresenter(historyListFragment)
        spotifyReceiver = SpotifyReceiver()

        registerBroadcastReceiver()
    }

    override fun onSongClick(song: Song?) {
        Log.v(LOG_TAG, "song $song clicked!")
        Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show()
    }

    private fun registerBroadcastReceiver() {
        val spotifyIntent = IntentFilter("com.spotify.music.playbackstatechanged")
        this.registerReceiver(spotifyReceiver, spotifyIntent)
    }

    private fun logSong(song: Song) {
        (supportFragmentManager.findFragmentById(R.id.contentFrame)
                as HistoryFragment).also { it.logSong(song) }
    }

    inner class SpotifyReceiver : BroadcastReceiver() {
        val PLAYBACKSTATE_CHANGED = "com.spotify.music.playbackstatechanged"

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || context !is HistoryActivity) return
            if (intent.action.equals(PLAYBACKSTATE_CHANGED)) {
                Log.v("Context from intent: ", " $context")
                val song = Song(
                        intent.getStringExtra("id"),
                        intent.getStringExtra("artist"),
                        intent.getStringExtra("album"),
                        intent.getStringExtra("track"),
                        intent.getIntExtra("length", 0)
                )
                logSong(song)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        registerBroadcastReceiver()
    }

    override fun onPause() {
        super.onPause()

        this.unregisterReceiver(spotifyReceiver)
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
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

        findViewById<DrawerLayout>(drawer_layout).closeDrawer(GravityCompat.START)
        return true
    }

}
