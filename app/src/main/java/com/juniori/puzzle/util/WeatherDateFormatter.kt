package com.juniori.puzzle.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@SuppressLint("SimpleDateFormat")
private val formatter = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
private val calendar = Calendar.getInstance()

fun String.toDate(): Date? = formatter.parse(this)

fun Date.toFullDate(): String {
    calendar.time = this
    return "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH)+1}월 ${calendar.get(Calendar.DATE)}일" +
            "${calendar.get(Calendar.DAY_OF_MONTH)} ${calendar.get(Calendar.HOUR)}시 " +
            "${calendar.get(Calendar.MINUTE)}분"
}

fun Date.toTime(): String {
    calendar.time = this
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    return if (hour >= 12) {
        if (hour == 12) {
            "오후 ${hour}시"
        } else {
            "오후 ${hour - 12}시"
        }
    } else {
        "오전 ${hour}시"
    }
}