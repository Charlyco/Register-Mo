package com.register.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentResolver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.getString
import com.register.app.R
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

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

    fun formatToDDMMYYYY(time: String): String {
        val date = LocalDateTime.parse(time)
        val formatter = DateTimeFormatter.ofPattern("MMM dd yyyy")
        return date.format(formatter)
    }

    suspend fun createDeviceId(dataStoreManager: DataStoreManager) {
        if (dataStoreManager.readDeviceId() == null) {
            val deviceId = UUID.randomUUID().toString()
            dataStoreManager.writeDeviceId(deviceId)
        }
    }

    fun normaliseString(input: String): String {
        return input.trim().lowercase(Locale.ROOT)
    }

        fun createNotificationChannel(context: Context) {
            val name = getString(context, R.string.channel_name)
            val descriptionText = getString(context, R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(context, R.string.register_notification_channel_id), name, importance )
            channel.description = descriptionText

            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    fun convertBitmapToJPEG(context: Context, bitmap: Bitmap): File? {
        // Create a new file to save the JPEG image
        val jpegFile = File(context.filesDir, "captured_image.jpg")

        return try {
            // Convert Bitmap to JPEG and save it to the file
            val outputStream = FileOutputStream(jpegFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            jpegFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun takePhoto(cameraActivityResult: ActivityResultLauncher<Void?>) {
        //val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraActivityResult.launch()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ImageSourceChooserDialog(
        filePicker: ManagedActivityResultLauncher<String, Uri?>,
        cameraActivityResult: ActivityResultLauncher<Void?>,
        onDismiss: (Boolean) -> Unit
    ) {
        ModalBottomSheet(
            onDismissRequest = {onDismiss(false)},
            modifier = Modifier.fillMaxWidth(),
            sheetState = rememberModalBottomSheetState(),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Text(
                text = stringResource(id = R.string.choose_source),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        takePhoto(cameraActivityResult)
                        onDismiss(false)
                    },
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .width(160.dp)
                ) {
                    Text(text = stringResource(id = R.string.camera))
                }
                Button(
                    onClick = {
                        filePicker.launch("image/*")
                        onDismiss(false)
                    },
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(160.dp)
                ) {
                    Text(text = stringResource(id = R.string.from_file))
                }
            }
        }
    }
}