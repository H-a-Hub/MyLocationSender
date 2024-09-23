package com.example.mylocationsender

import LocationServer
import android.location.Location
import org.junit.Assert.assertFalse
import org.junit.Test
import kotlinx.coroutines.test.runTest
//import kotlin.test.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import io.mockk.every
import io.mockk.mockk
import kotlin.time.Duration.Companion.seconds


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class LocationServerTest {

    @Test
    @OptIn(ExperimentalTime::class)
    fun test_true_sv_sendLocation() = runTest(timeout = 60.seconds) {

        try {
            val serverUrl = "https://my-location-monitor-93009588055.asia-east1.run.app/"
            val _locationSv = LocationServer(serverUrl)

            // Location クラスのモックを作成
            val location = mockk<Location>()
            // モックされたメソッドに対して返り値を設定
            every { location.latitude } returns 38.111
            every { location.longitude } returns 100.11
            every { location.toString() } returns "Location[gps 38.111,100.11]"

            // サーバに位置情報を送信
            _locationSv.sendLocation(location,
                onSuccess = {

                },
                onError = { e ->
                    assertFalse("sendLocatin false", true)
                })

        } catch (e: Exception) {
            assertFalse("sendLocatin false", true)
        }
    }
}