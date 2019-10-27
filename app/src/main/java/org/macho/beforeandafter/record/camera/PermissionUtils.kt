package org.macho.beforeandafter.record.camera

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object PermissionUtils {
    fun requestPermission(activity: Activity, requestCode: Int, vararg permissions: String): Boolean {
        var permissionsNeeded: MutableList<String> = mutableListOf()

        for (s in permissions) {
            if (ContextCompat.checkSelfPermission(activity, s) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(s)
            }
        }

        if (permissionsNeeded.size == 0) {
            return true
        } else {
            ActivityCompat.requestPermissions(activity, permissionsNeeded.toTypedArray(), requestCode)
            return false
        }
    }

    fun permissionGranted(requestCode: Int, permissionCode: Int, grantResults: IntArray): Boolean {
        return requestCode == permissionCode
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
    }
}