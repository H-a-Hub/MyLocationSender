import android.location.Location
import com.example.mylocationsender.dateToString
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import org.json.JSONObject
import java.util.Date

// 位置情報管理サーバ
class LocationServer(val hostUrl:String)
{
    // 位置情報送信
    fun sendLocation(location: Location)
    {
        Logger.info("LocationServer", "sendLocation() start location:${location.toString()}")

        val url = URL(hostUrl)  // serverUrlはベースURLのみ（例: https://your-server-endpoint.com/location）
        val urlConnection = url.openConnection() as HttpURLConnection

        try {
            urlConnection.requestMethod = "POST"  // POSTリクエストを使用
            urlConnection.doOutput = true
            urlConnection.setRequestProperty("Content-Type", "application/json; utf-8")
            urlConnection.setRequestProperty("Accept", "application/json")

            // 位置情報をJSON形式でリクエストボディに含める
            val jsonInputString = JSONObject().apply {
                put("timestamp", dateToString(Date(location.time)))
                put("latitude", location.latitude)
                put("longitude", location.longitude)
            }.toString()

            Logger.info("LocationServer", "request url:${urlConnection.url.toString()} method:${urlConnection.requestMethod.toString()}, json:${jsonInputString.toString()}")

            // データを送信
            urlConnection.outputStream.use { os ->
                val writer = OutputStreamWriter(os, "UTF-8")
                writer.write(jsonInputString)
                writer.flush()
                writer.close()
            }

            // サーバの応答を処理
            Logger.info("LocationServer", "response code:${urlConnection.responseCode}, message:${urlConnection.responseMessage}")
            if (urlConnection.responseCode == HttpURLConnection.HTTP_OK) {
                // サーバからの応答を処理
            } else {
                // エラーハンドリング
                throw IllegalStateException("サーバからエラー応答 code:${urlConnection.responseCode}")
            }
        } finally {
            urlConnection.disconnect()
            Logger.info("LocationServer", "sendLocation() end")
        }
    }
}

