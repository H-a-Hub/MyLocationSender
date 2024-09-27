package com.example.mylocationsender

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.net.HttpURLConnection

//　通信を伴うユニットテストはAndroidテストで行うこと
//  通常のユニットテスト環境では、ネットワークアクセスが禁止されている

@RunWith(AndroidJUnit4::class)
class LocationServerInstrumentedTest {

    private val _hostUrl = "https://my-location-monitor-93009588055.asia-east1.run.app"

    @Test
    fun testCheckAccessible() = runBlocking {

        // CompletableDeferredを使用して、非同期処理の完了を待機する
        val deferred = CompletableDeferred<Unit>()

        // テスト対象のLocationServerインスタンスを作成
        val server = LocationServer(_hostUrl)

        // 実際にサーバに送信して、成功・失敗を確認
        server.checkAccessible { statusCode ->
            assertEquals("status code is not success", HttpURLConnection.HTTP_OK, statusCode)
        }

        // コールバックが完了するまで待機する
        deferred.await()
    }


    @Test
    fun testSendLocationToServer() = runBlocking {

        // CompletableDeferredを使用して、非同期処理の完了を待機する
        val deferred = CompletableDeferred<Unit>()

        // テスト対象のLocationServerインスタンスを作成
        val server = LocationServer(_hostUrl)

        // ダミーのLocationオブジェクトを作成
        val testLocation = Location("").apply {
            latitude = 35.681236
            longitude = 139.767125
            time = System.currentTimeMillis()
        }

        // 実際にサーバに送信して、成功・失敗を確認
        server.sendLocation(
            location = testLocation,
            onResponse = { statusCode ->
                assertEquals("status code is not success", HttpURLConnection.HTTP_CREATED, statusCode)
            })

        // コールバックが完了するまで待機する
        deferred.await()
    }
}
