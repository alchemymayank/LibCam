package net.rmitsolutions.libcamdemo

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import net.rmitsolutions.libcam.Constants.CROP_PHOTO
import net.rmitsolutions.libcam.Constants.SELECT_PHOTO
import net.rmitsolutions.libcam.Constants.TAKE_PHOTO
import net.rmitsolutions.libcam.Constants.logD
import net.rmitsolutions.libcam.LibCam
import net.rmitsolutions.libcam.LibPermissions

class MainActivity : AppCompatActivity() {


    private lateinit var libPermissions: LibPermissions
    private lateinit var libCam : LibCam
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val permissions = arrayOf<String>(Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)

        libPermissions = LibPermissions(this, permissions)
        libCam = LibCam(this, libPermissions, 100)
    }

    fun openCamera(view: View){
        showPictureDialog()
    }

    fun cropImage(view: View){
        if (bitmap!=null){
            val uri = libCam.getBitmapUri(this,bitmap)
            libCam.cropImage(uri!!)
        }
    }

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallary() {
        libCam.selectPicture()
    }

    private fun takePhotoFromCamera() {
        libCam.takePhoto()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode){
            TAKE_PHOTO ->{
                if (data !=null){
                    bitmap = libCam.resultPhoto(requestCode, resultCode, data)!!
                    Glide.with(this).load(bitmap).into(imageViewCamera)
                }
            }
            SELECT_PHOTO ->{
                if (data!=null){
                    bitmap = libCam.resultPhoto(requestCode, resultCode, data)!!
                    Glide.with(this).load(bitmap).into(imageViewCamera)
                }else{
                    logD("Data is null")
                }

            }
            CROP_PHOTO ->{
                if (data!=null){
                    val uri= libCam.cropImageActivityResult(requestCode, resultCode, data)
                    imageViewCamera.setImageURI(uri)
                }else {
                    logD("Data is null")
                }
            }
        }
    }
}
