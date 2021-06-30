package com.simonedifonzo.academic.classes

import java.text.SimpleDateFormat
import java.util.*

object Utils {
    val currentTimeStamp: String
        get() {
            val sdfDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss") //dd/MM/yyyy
            val now = Date()
            return sdfDate.format(now)
        }

    fun transformToArabic(romanNumeral: String): String {

        when (romanNumeral) {
            "I" -> return "1"
            "II" -> return "2"
        }

        return "invalid number"
    }
}