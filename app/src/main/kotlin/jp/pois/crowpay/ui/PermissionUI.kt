package jp.pois.crowpay.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import jp.pois.crowpay.R

fun confirmPermissions(activity: ComponentActivity, callback: (Boolean, Array<String>?) -> Unit) {
    Log.d("confirmPermissionsPrev", permissions.joinToString())

    val permissions = permissions.filter {
        ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_DENIED
    }.toTypedArray()

    Log.d("confirmPermissions", permissions.joinToString())

    if (permissions.isEmpty()) {
        callback(true, null)
        return
    }

    if (permissions.any { ActivityCompat.shouldShowRequestPermissionRationale(activity, it) }) {
        AlertDialog.Builder(activity)
            .setMessage(activity.getString(R.string.permission_excuse))
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                callback(false, permissions)
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                callback(false, null)
            }
    } else {
        callback(false, permissions)
    }
}

private val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_WIFI_STATE,
    Manifest.permission.CHANGE_WIFI_STATE,
    Manifest.permission.ACCESS_COARSE_LOCATION,
    Manifest.permission.ACCESS_FINE_LOCATION
) else arrayOf(
    Manifest.permission.BLUETOOTH,
    Manifest.permission.BLUETOOTH_ADMIN,
    Manifest.permission.ACCESS_WIFI_STATE,
    Manifest.permission.CHANGE_WIFI_STATE,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
