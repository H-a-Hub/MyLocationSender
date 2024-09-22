package com.example.mylocationsender

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun dateToString(date: Date) : String {
//    val dateFormat = DateFormat.getDateTimeInstance(
//        DateFormat.MEDIUM,  // 日付のスタイル
//        DateFormat.MEDIUM,  // 時刻のスタイル
//        L//ocale.getDefault()  // 端末のロケールに合わせる
//    )
//    return dateFormat.format(date)
//    val dateFormat = SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS", Locale.getDefault())
    val formattedTime = dateFormat.format(date)
    return formattedTime
}
