package com.dani.eatsbalance.model.hardware


import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CameraHelper(private val context: Context) {

    companion object {
        private const val AUTHORITY = "com.dani.eatsbalance.fileprovider"
        private const val DATE_FORMAT = "yyyyMMdd_HHmmss"
        private const val IMG_PREFIX = "MEAL_"
        private const val IMG_SUFFIX = ".jpg"
    }

    // Crea un archivo temporal para la foto
    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date())
        val imageFileName = IMG_PREFIX + timeStamp + "_"
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            imageFileName,
            IMG_SUFFIX,
            storageDir
        )
    }

    // Obtener URI desde el archivo de la imagen usando FileProvider
    fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            AUTHORITY,
            file
        )
    }

    // Guarda una imagen permanentemente en la galería
    fun saveImageToGallery(bitmap: Bitmap, title: String): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$title.jpg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val contentResolver: ContentResolver = context.contentResolver
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            uri?.let {
                contentResolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(it, contentValues, null, null)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }

        return uri
    }

    // Guarda una imagen en el directorio privado de la aplicación
    fun saveImageToInternalStorage(bitmap: Bitmap, fileName: String): String {
        val directory = context.getDir("mealImages", Context.MODE_PRIVATE)
        val file = File(directory, "$fileName.jpg")

        try {
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }

        return file.absolutePath
    }

    // Cargar imagen desde el almacenamiento interno
    fun loadImageFromInternalStorage(filePath: String): Bitmap? {
        return try {
            BitmapFactory.decodeFile(filePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}