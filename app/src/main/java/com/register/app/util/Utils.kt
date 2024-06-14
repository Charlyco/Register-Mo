package com.register.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

object Utils {
    fun getFileNameFromUri(contentResolver: ContentResolver, fileUri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = contentResolver.query(fileUri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }

    fun copyTextToClipboard(context: Context, text: String) {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboardManager.setPrimaryClip(clip)
    }

    fun getTextFromClipboard(context: Context): String {
        val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboardManager.primaryClip
        if (clip != null && clip.itemCount > 0) {
            val item = clip.getItemAt(0)
            return item.text.toString()
        }
        return ""
    }

    fun LocalDateTime.toMills() = this.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

    fun Long.toLocalDateTime(): LocalDateTime {
        return LocalDateTime.ofEpochSecond(this / 1000, 0, ZoneOffset.UTC)
    }

    // Define an extension function to format LocalDate
    fun LocalDate.formatToYYYYMMDD(): String {
        return this.toString()
    }
}