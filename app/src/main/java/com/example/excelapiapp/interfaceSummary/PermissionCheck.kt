package com.example.excelapiapp.interfaceSummary

import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat

interface PermissionCheck {
    
    fun isExternalStorageWriteable(): Boolean {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            Log.d("value", "msg: Yes, it is writable")
            return true
        } else {
            return false
        }
    }

    fun checkPermission(context: Context?, permission: String): Boolean {
        val check: Int = ContextCompat.checkSelfPermission(context as Context, permission)
        Log.d("value", "check: ${check == PackageManager.PERMISSION_GRANTED}")
        return (check == PackageManager.PERMISSION_GRANTED)
    }



}