package com.example.studentcheck

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

class PermisionUtil {

    companion object {

        fun hasPermissions(context: Context?, vararg permissions: String): Boolean {
            if (context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }

    }

}