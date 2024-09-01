import android.content.Context
import com.example.mylocationsender.dateToString
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Date

object Logger {

    private lateinit var logFile: File

    // 初期化関数
    fun initialize(context: Context) {
        // アプリケーション名を取得
        val appName = getApplicationName(context)

        // 内部ストレージの"log"ディレクトリにアプリ名を使ったログファイルを作成
        val logDir = File(context.filesDir, "log")
        if (!logDir.exists()) {
            logDir.mkdirs() // ディレクトリが存在しない場合は作成
        }

        // ログファイル名にアプリ名を使用
        logFile = File(logDir, "$appName-log.txt")
    }

    // INFOレベルのログを出力
    fun info(tag: String, message: String) {
        logToConsoleAndFile("INFO", tag, message)
    }

    // WARNレベルのログを出力
    fun warn(tag: String, message: String) {
        logToConsoleAndFile("WARNING", tag, message)
    }

    // ERRORレベルのログを出力
    fun error(tag: String, message: String) {
        logToConsoleAndFile("ERROR", tag, message)
    }

    // コンソールとファイルにログを出力する共通関数
    private fun logToConsoleAndFile(logLevel: String, tag: String, message: String) {
        val timestamp = dateToString(Date())
        val logMessage = "$timestamp <$logLevel> [$tag]: $message"

        // コンソールに出力
        println(logMessage)

        // ファイルに出力
        writeToFile(logMessage)
    }

    // ファイルにログを書き込む
    private fun writeToFile(logMessage: String) {
        try {
            FileWriter(logFile, true).use { writer ->
                writer.appendLine(logMessage)
            }
        } catch (e: IOException) {
            println("Failed to write log to file: ${e.message}")
        }
    }

    // アプリケーション名を取得する関数
    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(stringId)
    }
}
