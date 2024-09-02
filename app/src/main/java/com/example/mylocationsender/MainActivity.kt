package com.example.mylocationsender

import LocationServer
import Logger
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val _locator = Locator(this)
    private lateinit var _config: Config
    private lateinit var _locationSv: LocationServer
    private lateinit var _locationTextView: TextView

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        Logger.initialize(this)
        Logger.info("MainActivity", "onCreate() start")

        // 設定JSON読み込み
        _config = readConfig(resources)

        // 画面部品の参照
        _locationTextView = findViewById(R.id.locationTextView)

        // 位置情報管理サーバ指定
        _locationSv = LocationServer(_config.server_url)

        // 位置情報サービス開始
        _locator.startService(_config.locate_interval_ms, onLocationUpdate)

        Logger.info("MainActivity", "onCreate() end")
    }

    // 許可リクエストの結果を処理
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Logger.info("MainActivity", "onRequestPermissionsResult() start requestCode:$requestCode, results:$grantResults")

        // 位置情報取得リクエストの結果
        if (requestCode == Locator.PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 位置情報サービス開始
                _locator.startService(_config.locate_interval_ms, onLocationUpdate)
            } else {
                // 許可が与えられなかった場合の処理
                _locationTextView.text = getString(R.string.not_permit_location)
            }
        }
    }

    private val onLocationUpdate: (Location) -> Unit = { location ->
        // サーバに位置情報を送信
        _locationSv.sendLocation(location,
            onSuccess = {
                // 通信成功はユーザーに見せる必要なし
            },
            onError = { e ->
                Logger.error("MainActivity", "e:$e")
                // TODO: 通信エラーが起きていることをトースト表示したい
            })

        // 画面にLocationをテキスト表示
        _locationTextView.text = getString(R.string.location_info, dateToString(Date(location.time)), location.latitude, location.longitude)

        // TODO: マップViewで現在位置を表示
    }


    override fun onDestroy() {
        Logger.info("MainActivity", "onDestroy() start")
        super.onDestroy()

        _locator.stopService()

        Logger.info("MainActivity", "onDestroy() end")
    }
}
