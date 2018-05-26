package net.rmitsolutions.libcam

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import net.rmitsolutions.libcam.Constants.CAMERA
import net.rmitsolutions.libcam.Constants.DEFAULT_BITMAP_FORMAT
import net.rmitsolutions.libcam.Constants.DEFAULT_DIRECTORY_NAME
import net.rmitsolutions.libcam.Constants.EXTERNAL_STORAGE
import net.rmitsolutions.libcam.Constants.SELECT_PHOTO

class LibCam {

    private lateinit var libPermissions : LibPermissions
    private lateinit var activity : Activity
    private lateinit var privateInformation: PrivateInformation
    private lateinit var actionCamera: ActionCamera
    private lateinit var savePhoto: SavePhoto
    private lateinit var pictureUtils: PictureUtils

    val TAKE_PHOTO = 0


    constructor(activity : Activity, LibPermissions: LibPermissions, resizePhoto : Int){
        this.activity = activity
        this.libPermissions = LibPermissions
        this.privateInformation = PrivateInformation()
        this.savePhoto = SavePhoto()
        this.pictureUtils = PictureUtils()
        this.actionCamera = ActionCamera(activity, resizePhoto, this.pictureUtils)

    }

    fun takePhoto(directoryName: String, filePrefix: String){
        val runnable = Runnable {
            actionCamera.takePhoto(directoryName, filePrefix)
        }
        askPermission(runnable, CAMERA)
    }

    fun takePhoto(){
        val runnable = Runnable {
            actionCamera.takePhoto()
        }
        askPermission(runnable, CAMERA)
    }

    fun selectPicture(){
        val runnable = Runnable {
            actionCamera.selectPicture()
        }
        askPermission(runnable, EXTERNAL_STORAGE)
    }

    fun sourceUri(): Uri? {
        return actionCamera.sourceUri()
    }

    fun sourceUri(directory : String): Uri? {
        return actionCamera.sourceUri(directory)
    }

    fun askPermission(task: Runnable){
        libPermissions.askPermissions(task)
    }

    fun askPermission(task: Runnable, operationType: String){
        libPermissions.askPermissions(task, operationType)
    }

    fun getImageInfo(path : String): PrivateInformationObject? {
        return privateInformation.getImageInformation(path)
    }


    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName : String, autoConcatenateNameByDate : Boolean): String? {
        return savePhoto.writePhotoFile(bitmap,photoName,DEFAULT_DIRECTORY_NAME,DEFAULT_BITMAP_FORMAT,autoConcatenateNameByDate, activity)
    }

    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName: String, format : Bitmap.CompressFormat, autoConcatenateNameByDate: Boolean): String? {
        return savePhoto.writePhotoFile(bitmap, photoName, DEFAULT_DIRECTORY_NAME, format, autoConcatenateNameByDate, activity)
    }

    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName: String, directoryName : String, autoConcatenateNameByDate: Boolean): String? {
        return savePhoto.writePhotoFile(bitmap,photoName, directoryName, DEFAULT_BITMAP_FORMAT,autoConcatenateNameByDate, activity)
    }

    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName: String, directoryName: String, format: Bitmap.CompressFormat, autoConcatenateNameByDate: Boolean): String? {
        return savePhoto.writePhotoFile(bitmap, photoName, directoryName, format, autoConcatenateNameByDate, activity)
    }


    fun resultPhoto(requestCode : Int, resultCode : Int, data : Intent): Bitmap? {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == TAKE_PHOTO) {
                return actionCamera.resultPhoto(requestCode,resultCode, data)
            }else if (requestCode == SELECT_PHOTO){
                return actionCamera.resultPhoto(data)
            }
        }
        return null
    }

    fun resultPhoto(requestCode: Int, resultCode: Int, data: Intent, rotation : Int): Bitmap? {
        if (resultCode == Activity.RESULT_OK ){
            if (requestCode == TAKE_PHOTO){
                return actionCamera.resultPhoto(requestCode, resultCode, data, rotation)
            }else if (requestCode == SELECT_PHOTO){
                return actionCamera.resultPhoto(data, rotation)
            }
        }
        return null
    }


    fun rotatePicture(rotate: Int){

    }

    fun rotatePicture(bitmap: Bitmap, rotate: Int){
        if (bitmap != null){
            pictureUtils.rotateImage(bitmap, rotate.toFloat())
        }
    }

    fun resizePhoto(bitmap: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
        return pictureUtils.resizePhoto(bitmap,maxImageSize,filter)
    }

    fun cropImage(uri: Uri){
        actionCamera.cropImage(uri)
    }

    fun cropImageActivityResult(requestCode: Int,resultCode: Int,data: Intent): Uri? {
        return actionCamera.cropImageActivityResult(requestCode, resultCode,data)
    }


    fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return MediaStore.Images.Media.getBitmap(activity.contentResolver,uri);
    }

    // filter = false will result in a blocky, pixellated image.
    // filter = true will give you smoother edges.
    fun createScaledBitmap(bitmap: Bitmap, width : Int, height : Int, filter : Boolean): Bitmap? {
        return Bitmap.createScaledBitmap(bitmap, width, height, filter)
    }

    fun decodeBitmapFromPath(currentPath : String, reqWidth: Int, reqHeight: Int): Bitmap? {
        return pictureUtils.decodeBitmapFromPath(currentPath,reqWidth,reqHeight)
    }

    fun getByteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return pictureUtils.getByteArrayToBitmap(byteArray)
    }

    fun getBitmapToByteArray(bitmap: Bitmap, quality : Int): ByteArray? {
        return pictureUtils.getBitmapToByteArray(bitmap, quality)
    }

    fun getBitmapFromBase64String(base64String : String): Bitmap? {
        return pictureUtils.getBitmapFromBase64String(base64String)
    }

    fun getBase64StringFromBitmap(bitmap : Bitmap, quality: Int): String? {
        return pictureUtils.getBase64StringFromBitmap(bitmap,quality)
    }

    fun getBitmapUri(context: Context, bitmap: Bitmap): Uri? {
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
}