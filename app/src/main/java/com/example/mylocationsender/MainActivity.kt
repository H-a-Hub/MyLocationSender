package com.example.mylocationsender

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Date

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val _locator = Locator(this)
    private lateinit var _config: Config
    private lateinit var _locationSv: LocationServer
    private lateinit var _locationTextView: TextView
    private lateinit var _mapView: MapView
    private lateinit var _mapController: GoogleMap

    // アクティビティが最初に作成されるときに呼ばれる
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
        _mapView = findViewById(R.id.mapView)

        _mapView.onCreate(savedInstanceState)
        // マップの同期開始
        // Googleのサーバーから地図データが取得されるプロセスで、AndroidManifest.xmlのAPIキーが利用されます。
        _mapView.getMapAsync(this)

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

    // Google Map 準備完了通知
    override fun onMapReady(map: GoogleMap) {

        /*
         GoogleMapを使うことで、MapView上にマーカーを追加したり、カメラ位置を変更したり、地図の設定を行うことができます。
         */
        _mapController = map
        _mapController.uiSettings.isZoomControlsEnabled = true

        _mapController.setOnMarkerClickListener { marker ->
            // マーカークリックイベント
            true
        }
    }

    private val onLocationUpdate: (Location) -> Unit = { location ->

        Logger.info("MainActivity", "onLocationUpdate() location:$location")

        // サーバに位置情報を送信
        _locationSv.sendLocation(location,
            onResponse = { statusCode ->
                if (statusCode != 201) {
                    // TODO: 登録エラーをユーザーに通知したい
                    Logger.warn("MainActivity", "not save")
                }
            })

        // 画面にLocationをテキスト表示
        _locationTextView.text = getString(R.string.location_info, dateToString(Date(location.time)), location.latitude, location.longitude)

        // マップViewで現在位置をマーク＆カメラ移動
        val currentLatLng = LatLng(location.latitude, location.longitude)
        _mapController.addMarker(MarkerOptions().position(currentLatLng).title("現在位置"))
        _mapController.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }

    // アクティビティがフォアグラウンドに出て、操作可能な状態になる
    override fun onResume() {
        Logger.info("MainActivity", "onResume() start")
        super.onResume()
        _mapView.onResume()
    }

    // アクティビティが完全に画面外に移動したときに呼ばれる
    override fun onPause() {
        Logger.info("MainActivity", "onPause() start")
        super.onPause()
        _mapView.onPause()
    }

    // アクティビティが終了し、メモリから削除される際に呼ばれる
    override fun onDestroy() {
        Logger.info("MainActivity", "onDestroy() start")
        super.onDestroy()
        _mapView.onDestroy()

        _locator.stopService()

        Logger.info("MainActivity", "onDestroy() end")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        _mapView.onLowMemory()
    }
}
