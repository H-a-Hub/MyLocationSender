package com.example.mylocationsender

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun dateToString(date: Date) {
    val dateFormat = SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss.SSSZ", Locale.getDefault())
    val formattedTime = dateFormat.format(date)
}
