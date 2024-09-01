import android.location.Location
import com.example.mylocationsender.dateToString
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
class LocationServer(val hostUrl:String)
{
    // 位置情報送信
    fun sendLocation(location: Location,
                     onSuccess: () -> Unit,         // 成功時のコールバック関数
                     onError: (Exception) -> Unit   // エラー時のコールバック関数
    )
    {
        // ネットワーク操作をI/Oスレッドで実行します。
        // これにより、メインスレッドをブロックせずにネットワーク操作を行うことができます。
        CoroutineScope(Dispatchers.IO).launch {

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

                Logger.info("LocationServer", "connect url:${urlConnection.url.toString()} method:${urlConnection.requestMethod.toString()}, json:${jsonInputString.toString()}")

                // 通信
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
                    withContext(Dispatchers.Main) { // メインスレッド
                        onSuccess()  // 成功時にコールバックを実行
                    }
                } else {
                    // エラーハンドリング
                    withContext(Dispatchers.Main) { // メインスレッド
                        onError(throw IllegalStateException("位置情報サーバへの通信失敗 code:${urlConnection.responseCode}"))
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { // メインスレッド
                    onError(e)  // エラー時にコールバックを実行
                }
            } finally {
                urlConnection.disconnect()
                Logger.info("LocationServer", "sendLocation() end")
            }
        }
    }
}

