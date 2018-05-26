package net.rmitsolutions.libcam

import android.media.ExifInterface
import net.rmitsolutions.libcam.Constants.notNullNotFill
import java.io.IOException

class PrivateInformation {

    //properties
    val privateInformationObject: PrivateInformationObject

    //constructor
    init {
        privateInformationObject = PrivateInformationObject()
    }

    //================================================================================
    // Exif interface methods
    //================================================================================
    private fun getAllFeatures(realPath: String): ExifInterface? {
        if (realPath != "") {
            var exif: ExifInterface? = null
            try {
                exif = ExifInterface(realPath)
                return exif
            } catch (e: IOException) {
                return exif
            }

        } else {
            return null
        }
    }

    fun getImageInformation(realPath: String): PrivateInformationObject? {
        try {
            val exif = getAllFeatures(realPath)
            if (exif != null) {

                val latLong = FloatArray(2)
                try {
                    exif.getLatLong(latLong)
                    privateInformationObject.latitude = latLong[0]
                    privateInformationObject.longitude =latLong[1]
                } catch (ex: Exception) {
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF))) {
                    privateInformationObject.latitudeReference = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF))) {
                    privateInformationObject.longitudeReference= exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_DATETIME))) {
                    privateInformationObject.dateTimeTakePhoto = exif.getAttribute(ExifInterface.TAG_DATETIME)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_ORIENTATION))) {
                    privateInformationObject.orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_ISO))) {
                    privateInformationObject.iso = exif.getAttribute(ExifInterface.TAG_ISO)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP))) {
                    privateInformationObject.dateStamp = exif.getAttribute(ExifInterface.TAG_GPS_DATESTAMP)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH))) {
                    privateInformationObject.imageLength = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH))) {
                    privateInformationObject.imageWidth = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_MODEL))) {
                    privateInformationObject.modelDevice = exif.getAttribute(ExifInterface.TAG_MODEL)
                }

                if (notNullNotFill(exif.getAttribute(ExifInterface.TAG_MAKE))) {
                    privateInformationObject.makeCompany = exif.getAttribute(ExifInterface.TAG_MAKE)
                }
                return privateInformationObject
            } else {
                return null
            }
        } catch (ex: Exception) {
            return null
        }

    }
}