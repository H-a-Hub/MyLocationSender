package com.example.mylocationsender

import LocationServer
import Logger
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import java.util.Date

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
            _locationSv?.sendLocation(location,
                onSuccess = {
                    // 通信成功はユーザーに見せる必要なし
                },
                onError = {e ->
                    Logger.error("MainActivity", "e:${e.toString()}")
                    // TODO: 通信エラーが発生していることを画面上に表示したい
                })

            // 画面にLocationをテキスト表示
            var locationTextView: TextView = findViewById(R.id.locationTextView)
            locationTextView.text = "date:${dateToString(Date(location.time))}\nlatitude:${location.latitude}\nlongitude:${location.longitude}"

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
