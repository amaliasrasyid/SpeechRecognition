package com.wisnu.speechrecognition.utils

import android.util.Log
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FormatDate {
    fun outputDateFormat(pattern: String): SimpleDateFormat {
        return SimpleDateFormat(pattern, Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    fun format(date: String?, oldPattern: String, newPattern: String): String {
        var formattedTime = ""

        if (date != null) {
            val sdf = outputDateFormat(oldPattern)
            try {
                val parseDate = sdf.parse(date)
                parseDate?.also {
                    formattedTime = outputDateFormat(newPattern).format(parseDate)
                }
            } catch (e: ParseException) {
                Log.e("TAG", "formateDate: ${e.printStackTrace()}")
            }
            return formattedTime
        }
        return formattedTime
    }
}