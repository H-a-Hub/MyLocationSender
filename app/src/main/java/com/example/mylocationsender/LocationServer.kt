package com.example.mylocationsender

import android.location.Location
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import java.util.Date
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// 位置情報管理サーバ
class LocationServer(private val hostUrl:String)
{
    // アクセス可能かチェック
    fun checkAccessible(
        onResponse: (statusCode: Int) -> Unit,         // コールバック関数
    ) {
        // ネットワーク操作をI/Oスレッドで実行します。
        // これにより、メインスレッドをブロックせずにネットワーク操作を行うことができます。
        CoroutineScope(Dispatchers.IO).launch {
            // リクエスト先のURL
            val url = URL(hostUrl)

            // URLに接続してGETリクエストを送信
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET" // メソッドをGETに設定

                // レスポンスコードを取得
                val responseCode = responseCode
                Logger.info(
                    "com.example.mylocationsender.LocationServer",
                    "responseCode:${responseCode}"
                )

                // レスポンスの読み取り
                inputStream.bufferedReader().use {
                    val response = it.readText()
                    Logger.info(
                        "com.example.mylocationsender.LocationServer",
                        "response:${response}"
                    )
                }

                onResponse(responseCode)
            }
        }
    }


    // 位置情報送信
    fun sendLocation(location: Location,
                     onResponse: (statusCode: Int) -> Unit)
    {
        // ネットワーク操作をI/Oスレッドで実行します。
        // これにより、メインスレッドをブロックせずにネットワーク操作を行うことができます。
        CoroutineScope(Dispatchers.IO).launch {
            Logger.info("com.example.mylocationsender.LocationServer", "sendLocation() start location:$location")

            val uriStr = hostUrl.removeSuffix("/") + "/api/regist_location"
            val url = URL(uriStr)  // serverUrlはベースURLのみ（例: https://your-server-endpoint.com/location）

            // 位置情報をJSON形式でリクエストボディに含める
            val jsonInputString = JSONObject().apply {
                put("user", "taro")
                put("timestamp", dateToString(Date(location.time)))
                put("latitude", location.latitude)
                put("longitude", location.longitude)
            }.toString()

            // URLに接続してPOSTリクエストを送信
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "POST" // メソッドをPOSTに設定
                doOutput = true // リクエストボディにデータを送信するためのフラグ

                // リクエストのヘッダーを設定
                setRequestProperty("Content-Type", "application/json; utf-8")
                setRequestProperty("Accept", "application/json")

                // リクエストボディにデータを書き込む
                outputStream.use { os ->
                    val writer = OutputStreamWriter(os, "UTF-8")
                    writer.write(jsonInputString)
                    writer.flush()
                }

                // レスポンスコードを取得
                val responseCode = responseCode
                Logger.info(
                    "com.example.mylocationsender.LocationServer",
                    "responseCode:${responseCode}"
                )

                // レスポンスの読み取り
                inputStream.bufferedReader().use {
                    val response = it.readText()
                    Logger.info(
                        "com.example.mylocationsender.LocationServer",
                        "response:${response}"
                    )
                }

                onResponse(responseCode)
            }
        }
    }
}

