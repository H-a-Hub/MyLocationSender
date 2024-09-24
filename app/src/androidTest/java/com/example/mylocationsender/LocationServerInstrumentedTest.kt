package com.example.mylocationsender

import android.location.Location
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

//　通信を伴うユニットテストはAndroidテストで行うこと
//  通常のユニットテスト環境では、ネットワークアクセスが禁止されている

@RunWith(AndroidJUnit4::class)
class LocationServerInstrumentedTest {

    @Test
    fun testSendLocationToServer() = runBlocking {
        // テスト対象のLocationServerインスタンスを作成
        val server = LocationServer("https://your-server-endpoint.com")

        // ダミーのLocationオブジェクトを作成
        val testLocation = Location("").apply {
            latitude = 35.681236
            longitude = 139.767125
            time = System.currentTimeMillis()
        }

        // 実際にサーバに送信して、成功・失敗を確認
        var success = false
        var error: Exception? = null

        server.sendLocation(
            location = testLocation,
            onSuccess = {
                success = true
            },
            onError = { e ->
                error = e
            }
        )

        // 成功または失敗をアサート
        assertTrue("Location sending failed with error: $error", success)
    }
}
