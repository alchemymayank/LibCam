package net.rmitsolutions.libcam

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import net.rmitsolutions.libcam.Constants.JPEG
import net.rmitsolutions.libcam.Constants.PNG
import net.rmitsolutions.libcam.Constants.SAVE_DIRECTORY_NAME
import net.rmitsolutions.libcam.Constants.WEBP
import net.rmitsolutions.libcam.Constants.logE
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SavePhoto {

    fun writePhotoFile(bitmap: Bitmap?, photoName: String, directoryName: String,
                       format: Bitmap.CompressFormat, autoIncrementNameByDate: Boolean, activity: Activity): String? {
        var photoName = photoName

        if (bitmap == null) {
            return null
        } else {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(format, 100, bytes)
            val df = SimpleDateFormat("yyyyMMddHHmmss")
            val date = df.format(Calendar.getInstance().time)
            if (format == PNG) {
                photoName = if (autoIncrementNameByDate) photoName + "_" + date + ".png" else "$photoName.png"
            } else if (format == JPEG) {
                photoName = if (autoIncrementNameByDate) photoName + "_" + date + ".jpeg" else "$photoName.jpeg"
            } else if (format == WEBP) {
                photoName = if (autoIncrementNameByDate) photoName + "_" + date + ".webp" else "$photoName.webp"
            }
            var wallpaperDirectory: File? = null

            try {
//                wallpaperDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/" + directoryName + "/")
                wallpaperDirectory = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES ), "$directoryName/$SAVE_DIRECTORY_NAME")
            } catch (ev: Exception) {
                try {
                    wallpaperDirectory = Environment.getExternalStorageDirectory()
                } catch (ex: Exception) {
                    try {
                        wallpaperDirectory = Environment.getDataDirectory()
                    } catch (e: Exception) {
                        wallpaperDirectory = Environment.getRootDirectory()
                    }
                }

            }

            if (wallpaperDirectory != null) {
                if (!wallpaperDirectory.exists()) {
                    wallpaperDirectory.exists()
                    wallpaperDirectory.mkdirs()
                }

                val f = File(wallpaperDirectory, photoName)
                try {
                    f.createNewFile()
                    val fo = FileOutputStream(f)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    activity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                            Uri.parse("file://" + f.absolutePath)))

                    try {
                        //Update the System
                        val u = Uri.parse(f.absolutePath)
                        activity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, u))
                    } catch (ex: Exception) {
                        logE("Exception : $ex")
                    }

                    return f.absolutePath
                } catch (ex: Exception) {
                    logE("Exception : $ex")
                    return null
                }

            } else {
                return null
            }
        }
    }
}