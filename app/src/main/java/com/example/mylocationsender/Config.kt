package com.example.mylocationsender

import android.content.res.Resources
import java.io.BufferedReader
import java.io.InputStreamReader
import com.google.gson.Gson

data class Config(
    val server_url: String,
    val locate_interval_ms: Long,
)

//fun parseUserJson(jsonString: String): Config {
//    val gson = Gson()
//    return gson.fromJson(jsonString, Config::class.java)
//}

// res > raw > config.json を解析する
fun readConfig(resources: Resources): Config {

    Logger.info("Config", "readJson() start")

    val inputStream = resources.openRawResource(R.raw.config)
    val bufferedReader = BufferedReader(InputStreamReader(inputStream))
    val stringBuilder = StringBuilder()

    bufferedReader.forEachLine { line ->
        stringBuilder.append(line)
    }

    val jsonString = stringBuilder.toString()

    val gson = Gson()
    val config = gson.fromJson(jsonString, Config::class.java)

    Logger.info("Config", "readJson() end config:$config")

    return config
}
