package com.example.githubuserinfo.util

import android.content.Context
import android.text.format.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

object DateUtils {

    /**
     * Formats an ISO 8601 date string to a locale-appropriate date format.
     *
     * @param context The context used to get locale settings
     * @param dateString The ISO 8601 formatted date string (e.g., "2024-01-01T00:00:00Z")
     * @return The formatted date string based on the device's locale
     *         (e.g., "January 1, 2024" for English, "2024年1月1日" for Japanese)
     *         Returns the original string if parsing fails
     */
    fun formatIsoDate(context: Context, dateString: String): String {
        return try {
            // Parse the ISO 8601 format date
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = inputFormat.parse(dateString)

            // Format using device locale settings
            // This will automatically format based on locale:
            // - English: "January 1, 2024"
            // - Japanese: "2024年1月1日"
            // - etc.
            val outputFormat = DateFormat.getMediumDateFormat(context)
            date?.let { outputFormat.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString
        }
    }
}