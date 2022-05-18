package com.appNutrity.nutrity.uielements

import android.app.Activity
import android.app.AlertDialog
import com.appNutrity.nutrity.R

class Loading(private val currentActivity: Activity) {
    private lateinit var dialog: AlertDialog

    fun startDialog() {
        // Set View
        val inflater = currentActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)
        // Set Dialog
        val builder = AlertDialog.Builder(currentActivity)
        with(builder) {
            setView(dialogView)
            setCancelable(true)
            dialog = create()
        }
        dialog.show()
    }
    fun isDismiss() {
        dialog.dismiss()
    }
}