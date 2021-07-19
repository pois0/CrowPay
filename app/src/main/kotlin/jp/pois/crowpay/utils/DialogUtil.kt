package jp.pois.crowpay.utils

import android.app.Activity
import android.app.AlertDialog
import android.util.Log
import jp.pois.crowpay.R

fun Activity.showFailedDialog() {
    AlertDialog.Builder(this)
        .setMessage(R.string.fail_to_start_nearby)
        .setCancelable(false)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            finish()
            dialog.dismiss()
        }
        .show()
}

fun Activity.showCommunicationErrorDialog() {
    Log.d("showCommunicationErrorDialog", Throwable().stackTraceToString())
    AlertDialog.Builder(this)
        .setMessage(R.string.communication_error_occurred)
        .setCancelable(false)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            finish()
            dialog.dismiss()
        }
        .show()
}

fun Activity.showErrorOccurredDialog() {
    AlertDialog.Builder(this)
        .setMessage(R.string.error_occurred)
        .setCancelable(false)
        .setPositiveButton(R.string.ok) { dialog, _ ->
            finish()
            dialog.dismiss()
        }
        .show()
}
