package net.rmitsolutions.libcam

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File

class PictureUtils {
    //===============================================================================
    // Utils methods, resize and get Photo Uri and others
    //================================================================================


    /**
     * Rotate the bitmap if the image is in landscape camera
     * @param source
     * @param angle
     * @return
     */
    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val retVal: Bitmap
        val matrix = Matrix()
        matrix.postRotate(angle)
        retVal = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        return retVal
    }

    /**
     * This method resize the photo
     *
     * @param realImage    the bitmap of image
     * @param maxImageSize the max image size percentage
     * @param filter       the filter
     * @return a bitmap of the photo rezise
     */
    fun resizePhoto(realImage: Bitmap, maxImageSize: Float,
                    filter: Boolean): Bitmap {
        val ratio = Math.min(
                maxImageSize / realImage.width,
                maxImageSize / realImage.height)
        val width = Math.round(ratio * realImage.width)
        val height = Math.round(ratio * realImage.height)

        val newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter)
        return newBitmap
    }

    // Get base 64 string from bitmap with image quality
    fun getBase64StringFromBitmap(bitmap : Bitmap, quality: Int): String? {
        val byteArrayStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayStream)
        val byteArray = byteArrayStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    //Get bitmap from base 64 string
    fun getBitmapFromBase64String(base64String : String): Bitmap? {
        val bitmapArray = Base64.decode(base64String, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.size)
        return if (bitmap !=null){
            bitmap
        }else {
            null
        }
    }

    // Get bitmap from byte array
    fun getBitmapToByteArray(bitmap: Bitmap, quality : Int): ByteArray? {
        val byteArrayStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayStream)
        return byteArrayStream.toByteArray()
    }

    // Get byte array from bitmap
    fun getByteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        return if (bitmap !=null){
            bitmap
        }else {
            null
        }
    }


    // Decode bitmap using string path with required width and required height
    fun decodeBitmapFromPath(currentPath : String, reqWidth: Int, reqHeight: Int): Bitmap? {
        val bitmapFile = File(currentPath)
        return if (bitmapFile.exists()){
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(bitmapFile.absolutePath, options)
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false
            BitmapFactory.decodeFile(bitmapFile.absolutePath, options)
        }else{
            null
        }
    }

    // Calculating sample size of bitmap with required width and height
    private fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}