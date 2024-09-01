package com.example.mylocationsender

import LocationServer
import Logger
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private val _locator = Locator(this)
    private var _locationSv: LocationServer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Logger.initialize(this)
        Logger.info("MainActivity", "onCreate() start")

        // 設定JSON読み込み
        val config = readConfig(resources)

        // 位置情報管理サーバ指定
        _locationSv = LocationServer(config.server_url)

        // 位置情報サービス開始
        _locator.startService(config.locate_interval_ms, { location ->
            // サーバに位置情報を送信
            _locationSv?.sendLocation(location)

            // TODO: 画面にLocationをテキスト表示

            // TODO: マップViewで現在位置を表示
        })

        Logger.info("MainActivity", "onCreate() end")
    }

    override fun onDestroy() {
        Logger.info("MainActivity", "onDestroy() start")
        super.onDestroy()

        _locator.stopService()

        Logger.info("MainActivity", "onDestroy() end")
    }
}
