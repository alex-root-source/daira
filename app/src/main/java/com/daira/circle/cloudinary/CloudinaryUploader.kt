package com.daira.circle.data.cloudinary

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.TimeUnit

object CloudinaryConfig {
    const val CLOUD_NAME = "tawlaww3"
    const val UPLOAD_PRESET = "Alexander"
}

class CloudinaryUploader(private val context: Context) {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun upload(uri: Uri, mediaType: String): String = withContext(Dispatchers.IO) {
        val extension = if (mediaType == "video") "mp4" else "jpg"
        val tempFile = File.createTempFile("daira_upload", ".$extension", context.cacheDir)

        try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(tempFile).use { output -> input.copyTo(output) }
            } ?: throw IllegalStateException("تعذر قراءة الملف المختار")

            val mime = if (mediaType == "video") "video/mp4" else "image/jpeg"
            val resourceType = if (mediaType == "video") "video" else "image"

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("upload_preset", CloudinaryConfig.UPLOAD_PRESET)
                .addFormDataPart("file", tempFile.name, tempFile.asRequestBody(mime.toMediaTypeOrNull()))
                .build()

            val request = Request.Builder()
                .url("https://api.cloudinary.com/v1_1/${CloudinaryConfig.CLOUD_NAME}/$resourceType/upload")
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                if (!response.isSuccessful || bodyString == null) {
                    throw IllegalStateException("فشل رفع الملف (${response.code}) — تأكد من صحة إعدادات Cloudinary")
                }
                JSONObject(bodyString).getString("secure_url")
            }
        } finally {
            tempFile.delete()
        }
    }
}
