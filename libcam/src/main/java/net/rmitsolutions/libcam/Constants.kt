package net.rmitsolutions.libcam

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log

object Constants {

    val TAKE_PHOTO = 0
    val SELECT_PHOTO = 1
    val LANDSCAPE_CAMERA = 2
    val NORMAL_CAMERA = 3
    val CROP_PHOTO = 203

    //Constants for permissions
    val CAMERA = "android.permission.CAMERA"
    val EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE"
    val ACCESS_LOCATION = "android.permission.ACCESS_COARSE_LOCATION"


    //the max of quality photo
    val BEST_QUALITY_PHOTO = 4000


    var realPath: String? = null

    var myPhoto : Bitmap? = null

    var mCurrentPhotoPath : String = ""
    var mCurrentImageName : String = ""

    var globalBitmapUri : Uri? = null


    // Bitmap Format
    val JPEG: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG
    val PNG : Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
    val WEBP : Bitmap.CompressFormat = Bitmap.CompressFormat.WEBP

    val DEFAULT_BITMAP_FORMAT = JPEG
    val DEFAULT_DIRECTORY_NAME = "LibCamera"
    val SAVE_DIRECTORY_NAME = "SavePhoto"
    val DEFAULT_FILE_PREFIX = DEFAULT_DIRECTORY_NAME

    //validate if the string isnull or empty
    fun notNullNotFill(validate: String?): Boolean {
        return if (validate != null) {
            validate.trim { it <= ' ' } != ""
        } else {
            false
        }
    }

    fun logE(message : String){
        Log.e(DEFAULT_DIRECTORY_NAME, message)
    }

    fun logD(message: String){
        Log.d(DEFAULT_DIRECTORY_NAME, message)
    }
}