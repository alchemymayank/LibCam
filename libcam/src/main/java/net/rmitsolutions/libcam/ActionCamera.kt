package net.rmitsolutions.libcam

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.util.Log
import com.theartofdev.edmodo.cropper.CropImage
import net.rmitsolutions.libcam.Constants.DEFAULT_DIRECTORY_NAME
import net.rmitsolutions.libcam.Constants.DEFAULT_FILE_PREFIX
import net.rmitsolutions.libcam.Constants.SELECT_PHOTO
import net.rmitsolutions.libcam.Constants.TAKE_PHOTO
import net.rmitsolutions.libcam.Constants.logD
import net.rmitsolutions.libcam.Constants.logE
import net.rmitsolutions.libcam.Constants.mCurrentPhotoPath
import java.io.File

class ActionCamera {

    private lateinit var activity : Activity
    private lateinit var privateInformation: PrivateInformation
    private lateinit var image : File
    private var resizePhoto: Int = 0
    private lateinit var pictureUtils: PictureUtils

    constructor(activity: Activity, resizePhoto:  Int, pictureUtils: PictureUtils){
        this.resizePhoto = if (resizePhoto <= 0) Constants.BEST_QUALITY_PHOTO else resizePhoto
        this.activity = activity
        this.privateInformation = PrivateInformation()
        this.pictureUtils = pictureUtils
        this.setResizePhoto(this.resizePhoto)

    }

    fun takePhoto(): Boolean {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraImageUri = getFileUri(DEFAULT_DIRECTORY_NAME, DEFAULT_FILE_PREFIX)
        return if (cameraImageUri!=null){
            logD("Camera Uri not null")
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            if (cameraIntent.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(cameraIntent, Constants.TAKE_PHOTO)
            }
            true
        }else {
            logE("Unable to upload photo")
            false
        }
    }

    fun takePhoto(dirName : String, filePrefix : String): Boolean {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val cameraImageUri = getFileUri(dirName, filePrefix)
        return if (cameraImageUri!=null){
            logD("Cammera Uri not null")
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            if (cameraIntent.resolveActivity(activity.packageManager) != null) {
                activity.startActivityForResult(cameraIntent, Constants.TAKE_PHOTO)
            }
            true
        }else {
            logE("Unable to upload photo")
            false
        }
    }

    fun sourceUri(): Uri? {
        val fileUri = Uri.parse(Environment.getExternalStorageDirectory().path
                + File.separator + "LibCamera" + File.separator + "LibCamera" + File.separator)
        return if (fileUri.isAbsolute) fileUri else null

    }

    fun sourceUri(directory: String): Uri? {
        val fileUri = Uri.parse(Environment.getExternalStorageDirectory().path
                + File.separator + "LibCamera" + File.separator + directory + File.separator)
        return if (fileUri.isAbsolute) fileUri else null
    }



    private fun getFileUri(dirName: String, fileName: String): Uri? {
        if (isExternalStorageAvailable()){
            var file = File(activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$dirName/$DEFAULT_DIRECTORY_NAME")
            if (file !=null){
                logD("File not null")
                image = createImageFile(file,fileName)!!
            }else{
                logD("File is null")
                file = File(activity.filesDir, dirName)
                image = createImageFile(file,fileName)!!
            }
            if (image.exists()){
                logD("Image exist")
                return getUriFilePath(image)
            }else if (!file.mkdirs()) {
                Log.e(DEFAULT_DIRECTORY_NAME, "Directory not created")
                return null
            }
            return getUriFilePath(image)
        }else {
            logD("External storage not available")
            val file = File(activity.filesDir, dirName)
            image = createImageFile(file,fileName)!!
            return getUriFilePath(image)
        }

    }


    private fun getUriFilePath(file: File): Uri? {
        return if (android.os.Build.VERSION.SDK_INT >= 24) {
            logD("SDK >= 24")
            FileProvider.getUriForFile(activity, activity.packageName+ ".provider", file)

        }else {
            logD("SDK < 24")
            logD(file.absolutePath)
            Uri.fromFile(file)
        }
    }

    private fun createImageFile(directory : File, fileName: String): File? {
        logD("Inside create image file")
        if (!directory.exists() && !directory.mkdirs()){
            directory.exists()
            directory.mkdirs()
        }
        val image = File.createTempFile(
                fileName, /* prefix */
                ".jpg", /* suffix */
                directory      /* directory */
        )
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    fun resultPhoto(requestCode: Int, resultCode: Int, data: Intent): Bitmap? {
        return if (resultCode == Activity.RESULT_OK ){
            if (requestCode == TAKE_PHOTO){
                return BitmapFactory.decodeFile(image.absolutePath)
            }else {
                null
            }
        }else {
            null
        }
    }

    fun resultPhoto(requestCode: Int, resultCode: Int, data: Intent, rotate : Int): Bitmap? {
        return if (resultCode == Activity.RESULT_OK ){
            if (requestCode == TAKE_PHOTO){
                return pictureUtils.rotateImage(BitmapFactory.decodeFile(image.absolutePath), rotate.toFloat())
            }else {
                null
            }
        }else {
            null
        }
    }

    fun resultPhoto(data : Intent): Bitmap? {
        return if (data!= null){
            val contentUri = data.data
            try {
                MediaStore.Images.Media.getBitmap(activity.contentResolver, contentUri)
            }catch (e: Exception){
                logE("Exception : $e")
                null
            }
        }else {
            null
        }
    }

    fun resultPhoto(data: Intent, rotate: Int): Bitmap? {
        return if (data!= null){
            val contentUri = data.data
            try {
                return pictureUtils.rotateImage(MediaStore.Images.Media.getBitmap(activity.contentResolver, contentUri), rotate.toFloat())
            }catch (e: Exception){
                logE("Exception : $e")
                null
            }
        }else {
            null
        }
    }

    private fun setResizePhoto(resizePhoto: Int) {
        this.resizePhoto = resizePhoto * 40
        if (resizePhoto in 1..Constants.BEST_QUALITY_PHOTO)
            this.resizePhoto = resizePhoto
        else {
            this.resizePhoto = Constants.BEST_QUALITY_PHOTO
        }
    }

    private fun isExternalStorageAvailable(): Boolean {
        val state = Environment.getExternalStorageState()
        if (state == Environment.MEDIA_MOUNTED){
            return true
        }
        return false
    }


    fun selectedPicture(headerName: String): Boolean {
        try {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            activity.startActivityForResult(
                    Intent.createChooser(intent, if (headerName != "") headerName else DEFAULT_FILE_PREFIX),
                    SELECT_PHOTO)
            return true
        } catch (e: Exception) {
            logE("Exception : $e")
            return false
        }

    }

    fun cropImage(uri: Uri) {
        CropImage.activity(uri).start(activity)
    }

    fun cropImageActivityResult(requestCode: Int, resultCode: Int,data: Intent): Uri? {
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK){
                val uri = result.uri
                return uri
            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                val error = result.error
                logD("Error : $error")
                return null
            }
        }
        return null
    }

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

    fun calculateInSampleSize(
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

    fun selectPicture() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, SELECT_PHOTO)
    }
}