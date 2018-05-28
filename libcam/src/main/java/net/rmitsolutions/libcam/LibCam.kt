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
import net.rmitsolutions.libcam.Constants.TAKE_PHOTO

class LibCam {

    private lateinit var libPermissions : LibPermissions
    private lateinit var activity : Activity
    private lateinit var privateInformation: PrivateInformation
    private lateinit var actionCamera: ActionCamera
    private lateinit var savePhoto: SavePhoto
    private lateinit var pictureUtils: PictureUtils


    constructor(activity : Activity, LibPermissions: LibPermissions, resizePhoto : Int){
        this.activity = activity
        this.libPermissions = LibPermissions
        this.privateInformation = PrivateInformation()
        this.savePhoto = SavePhoto()
        this.pictureUtils = PictureUtils()
        this.actionCamera = ActionCamera(activity, resizePhoto, this.pictureUtils)

    }

    // To take photo with dynamic directory name
    fun takePhoto(directoryName: String, filePrefix: String){
        val runnable = Runnable {
            actionCamera.takePhoto(directoryName, filePrefix)
        }
        askPermission(runnable, CAMERA)
    }

    // To take photo with default directory name
    fun takePhoto(){
        val runnable = Runnable {
            actionCamera.takePhoto()
        }
        askPermission(runnable, CAMERA)
    }

    // To select picture from gallery
    fun selectPicture(){
        val runnable = Runnable {
            actionCamera.selectPicture()
        }
        askPermission(runnable, EXTERNAL_STORAGE)
    }

    // To get the Uri of default folder where storing images
    fun sourceUri(): Uri? {
        return actionCamera.sourceUri()
    }

    // To get the Uri by provide name of the directory
    // 1> LibCam
    // 2> SavePhoto
    fun sourceUri(directory : String): Uri? {
        return actionCamera.sourceUri(directory)
    }

    fun askPermission(task: Runnable){
        libPermissions.askPermissions(task)
    }


    fun askPermission(task: Runnable, operationType: String){
        libPermissions.askPermissions(task, operationType)
    }

    // It is used to get Image inforrmation
    fun getImageInfo(path : String): PrivateInformationObject? {
        return privateInformation.getImageInformation(path)
    }

    // Used to save photo in SavePhoto folder
    // bitmap = bitmap object
    // photoName = prefix of image file name
    // format = Default (JPEG)
    // autoConcatenateNameByDate = true if you want to concatenate file prefix by date and time
    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName : String, autoConcatenateNameByDate : Boolean): String? {
        return savePhoto.writePhotoFile(bitmap,photoName,DEFAULT_DIRECTORY_NAME,DEFAULT_BITMAP_FORMAT,autoConcatenateNameByDate, activity)
    }

    // Used to save photo in SavePhoto folder
    // bitmap = bitmap object
    // photoName = prefix of image file name
    // format = JPEG, PNG and WEBP
    // autoConcatenateNameByDate = true if you want to concatenate file prefix by date and time
    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName: String, format : Bitmap.CompressFormat, autoConcatenateNameByDate: Boolean): String? {
        return savePhoto.writePhotoFile(bitmap, photoName, DEFAULT_DIRECTORY_NAME, format, autoConcatenateNameByDate, activity)
    }

    // Used to save photo in SavePhoto folder
    // bitmap = bitmap object
    // photoName = prefix of image file name
    // directoryName = directory name in which you want to save
    // format = Default (JPEG)
    // autoConcatenateNameByDate = true if you want to concatenate file prefix by date and time
    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName: String, directoryName : String, autoConcatenateNameByDate: Boolean): String? {
        return savePhoto.writePhotoFile(bitmap,photoName, directoryName, DEFAULT_BITMAP_FORMAT,autoConcatenateNameByDate, activity)
    }

    // Used to save photo in SavePhoto folder
    // bitmap = bitmap object
    // photoName = prefix of image file name
    // directoryName = directory name in which you want to save
    // format = JPEG, PNG and WEBP
    // autoConcatenateNameByDate = true if you want to concatenate file prefix by date and time
    fun savePhotoInMemoryDevice(bitmap: Bitmap, photoName: String, directoryName: String, format: Bitmap.CompressFormat, autoConcatenateNameByDate: Boolean): String? {
        return savePhoto.writePhotoFile(bitmap, photoName, directoryName, format, autoConcatenateNameByDate, activity)
    }


    // Called when onActivityResultCalled
    // Called for capture photo
    // Called for select photo
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

    // Called when onActivityResultCalled
    // Called for capture photo
    // Called for select photo
    // Rotation = angle to rotate the Image
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


    // Used to rotate Image
    fun rotatePicture(rotate: Int){

    }

    // Used to rotate Image
    // bitmap = bitmap object
    // rotate = angle to rotate image
    fun rotatePicture(bitmap: Bitmap, rotate: Int){
        if (bitmap != null){
            pictureUtils.rotateImage(bitmap, rotate.toFloat())
        }
    }

    // Used to resize Image

    fun resizePhoto(bitmap: Bitmap, maxImageSize: Float, filter: Boolean): Bitmap {
        return pictureUtils.resizePhoto(bitmap,maxImageSize,filter)
    }

    // Used to crop image
    fun cropImage(uri: Uri){
        actionCamera.cropImage(uri)
    }

    // Used for crop image activity result
    fun cropImageActivityResult(requestCode: Int,resultCode: Int,data: Intent): Uri? {
        return actionCamera.cropImageActivityResult(requestCode, resultCode,data)
    }


    // Used to load bitmap from Uri
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

    // Used to get bitmap from byte array
    fun getByteArrayToBitmap(byteArray: ByteArray): Bitmap? {
        return pictureUtils.getByteArrayToBitmap(byteArray)
    }

    fun getBitmapToByteArray(bitmap: Bitmap, quality : Int): ByteArray? {
        return pictureUtils.getBitmapToByteArray(bitmap, quality)
    }

    // Used to get bitmap from base 64 string
    fun getBitmapFromBase64String(base64String : String): Bitmap? {
        return pictureUtils.getBitmapFromBase64String(base64String)
    }

    // Used to get base 64 string from bitmap
    // bitmap = bitmap object
    // quality = 0-100 Image compressed quality
    fun getBase64StringFromBitmap(bitmap : Bitmap, quality: Int): String? {
        return pictureUtils.getBase64StringFromBitmap(bitmap,quality)
    }

    // Used to get bitmap from Uri
    fun getBitmapUri(context: Context, bitmap: Bitmap): Uri? {
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path)
    }
}