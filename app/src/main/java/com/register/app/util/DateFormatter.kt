package com.register.app.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateFormatter {
    companion object {
        fun formatDateTime(dateString: String): String {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

            // Parse the date string into a LocalDateTime object
            val localDateTime: LocalDateTime = LocalDateTime.parse(dateString)

            // Format the LocalDateTime object into the desired format
            return formatter.format(localDateTime)
        }
    }
}