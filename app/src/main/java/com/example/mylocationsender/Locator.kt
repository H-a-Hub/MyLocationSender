package com.example.mylocationsender

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

// 位置情報機能制御
class Locator(val _activity: Activity)
{
    private lateinit var _fusedLocationClient: FusedLocationProviderClient
    private lateinit var _locationRequest: LocationRequest
    private lateinit var _locationCallback: LocationCallback

    companion object {
        // 位置情報機能許可へのリクエストコード
        public val PERMISSION_REQUEST_CODE = 1001
    }

    fun startService(updateInterval: Long = 5000, callback: (Location) -> Unit)
    {
        Logger.info("Locator", "startService() start interval:$updateInterval")

        // 位置情報クライアントを初期化
        _fusedLocationClient = LocationServices.getFusedLocationProviderClient(_activity)

        // LocationRequest.Builderを使用して位置情報リクエストを作成
        _locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(updateInterval) // 最速5秒
            .build()

        // 位置情報が更新された時のコールバック
        _locationCallback = object : LocationCallback()
        {
            override fun onLocationResult(locationResult: LocationResult) {
                Logger.info("Locator", "onLocationResult")

                locationResult.lastLocation?.let { location ->
                    Logger.info("Locator", "locationResult: location:${location.toString()}")
                    callback(location)
                }
            }
        }

        // パーミッションの確認とリクエスト
        if (ActivityCompat.checkSelfPermission(
                _activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                _activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Logger.info("Locator", "requestPermissions")

            ActivityCompat.requestPermissions(
                _activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
            return
        }

        // 位置情報の更新を開始
        _fusedLocationClient.requestLocationUpdates(_locationRequest, _locationCallback, Looper.getMainLooper())

        Logger.info("Locator", "startService() end")
    }

    fun stopService()
    {
        Logger.info("Locator", "stopService()")

        // 位置情報の更新を停止
        _fusedLocationClient.removeLocationUpdates(_locationCallback)
    }
}