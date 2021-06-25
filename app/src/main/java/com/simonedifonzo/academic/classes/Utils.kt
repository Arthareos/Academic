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
}