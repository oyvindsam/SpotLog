package com.samudev.spotlog

import android.content.Intent
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class SpotLogTileService : TileService() {

    private val LOG_TAG: String = SpotLogTileService::class.java.simpleName

    private val loggerServiceIntent by lazy { Intent(applicationContext, LoggerService::class.java) }

    override fun onClick() {
        when (qsTile.state) {
            Tile.STATE_ACTIVE -> {
                stopService(loggerServiceIntent)
                qsTile.state = Tile.STATE_INACTIVE
            }
            else -> {
                startService(loggerServiceIntent)
                qsTile.state = Tile.STATE_ACTIVE
            }
        }
        qsTile.updateTile()
    }

}


