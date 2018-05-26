package net.rmitsolutions.libcam

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import java.util.ArrayList
import java.util.HashMap

class LibPermissions {

    private var activity: Activity? = null
    private var fragment: Fragment? = null
    private var fragmentV4: android.support.v4.app.Fragment? = null
    private var permissions: Array<String>? = null
    private var allPermissions: Array<String>? = null
    private var task: Runnable? = null


    constructor(activity: Activity, permissions: Array<String>) {
        this.activity = activity
        this.permissions = permissions
        this.allPermissions = permissions
    }

    constructor(fragment: Fragment, permissions: Array<String>) {
        this.fragment = fragment
        this.permissions = permissions
        this.allPermissions = permissions
    }

    constructor(fragmentV4: android.support.v4.app.Fragment, permissions: Array<String>) {
        this.fragmentV4 = fragmentV4
        this.permissions = permissions
        this.allPermissions = permissions
    }


    fun permissionsNeeded(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun askPermissions(task: Runnable) {
        //In case the developer want to do something after the permissions are granted
        this.task = task
        if (permissionsNeeded()) {
            requestPermissions()
        } else {
            runPendingTask()
        }
    }

    fun askPermissions(task: Runnable, operationType: String) {
        //In case the developer want to do something after the permissions are granted
        this.task = task
        if (permissionsNeeded()) {
            requestPermissions(operationType)
        } else {
            runPendingTask()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions() {
        executeRequestPermission(null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestPermissions(operationType: String) {
        executeRequestPermission(operationType)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun executeRequestPermission(operationType: String?) {
        var context: Context? = null
        if (activity != null) {
            context = activity
        } else {
            context = if (fragment != null) fragment!!.activity else fragmentV4!!.context
        }

        val permissionListFault = ArrayList<String>()
        //obtain the list of permissions faults
        for (permission in permissions!!) {
            if (ContextCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionListFault.add(permission)
            }
        }

        var runPendingTaskFlag = true
        //Then only if there are permissions to be requested, only request those
        if (permissionListFault.size > 0) {
            //If there are permissions to be requested then we can redefine the original array with the ones we have to take care
            permissions = permissionListFault.toTypedArray()
            runPendingTaskFlag = false
        } else {
            runPendingTaskFlag = true
            //But if every permission is granted then go a head and do what you want
            runPendingTask()
        }

        //validate if have permissions or not for call the request permissions
        if (permissionListFault.size > 0 && !runPendingTaskFlag) {
            var isCurrentOpetationType = false
            var x = 0
            while (this.allPermissions!!.size > x) {

                var isSuccessPermission = true
                var i = 0
                while (permissionListFault.size > i) {
                    if (permissionListFault[i] == this.allPermissions!![x]) {
                        isSuccessPermission = false
                    }
                    i++
                }

                if (isSuccessPermission) {
                    if (operationType == allPermissions!![x]) {
                        isCurrentOpetationType = true
                        runPendingTask()
                        break
                    }
                }
                x++
            }

            if (!isCurrentOpetationType) {
                //Request permissions is being lint, but that is not a problem in the askPermissions method there is a validation to prevent it in lowe API
                //Adding a suppress warning annotation is worst because since android studio 2.3 annotated methods can be marked as dangerous
                activity?.requestPermissions(permissions!!, RC_PERMISSIONS_ACTIVITY)
                        ?: (fragment?.requestPermissions(permissions!!, RC_PERMISSIONS_FRAGMENT)
                                ?: fragmentV4?.requestPermissions(permissions!!, RC_PERMISSIONS_FRAGMENT))
            }
        }
    }

    fun permissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray): Map<String, Boolean> {
        //Returning a map is better to let developer know the permissions and what happened
        val map = HashMap<String, Boolean>()
        if (RC_PERMISSIONS_ACTIVITY == requestCode || RC_PERMISSIONS_FRAGMENT == requestCode) {
            var validation = true
            for (i in permissions.indices) {
                val result = grantResults[i]
                map[permissions[i]] = result == PackageManager.PERMISSION_GRANTED
                if (PackageManager.PERMISSION_GRANTED != result) {
                    validation = false
                }
            }
            if (validation) {
                runPendingTask()
            }
        }

        return map
    }

    private fun runPendingTask() {
        if (task != null) {
            task!!.run()
        }
    }

    companion object {
        private val RC_PERMISSIONS_ACTIVITY = 878
        private val RC_PERMISSIONS_FRAGMENT = 879
    }
}