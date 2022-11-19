package com.juniori.puzzle.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@SuppressLint("SimpleDateFormat")
private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
private val calendar = Calendar.getInstance()

fun String.toDate(): Date? = formatter.parse(this)

fun Date.toFullDate(): String {
    calendar.time = this
    return String.format(
        "%d년 %02d월 %02d일 %02d시 %02d분",
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DATE),
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE)
    )
}

fun Date.toTime(): String {
    calendar.time = this
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return if (hour > 12) {
        "오후 ${hour - 12}시"
    } else if (hour == 12) {
        "오후 ${hour}시"
    } else {
        "오전 ${hour}시"
    }
}