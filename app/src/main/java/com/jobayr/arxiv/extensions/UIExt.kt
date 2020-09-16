package com.jobayr.arxiv.extensions

import android.app.Activity
import androidx.appcompat.app.AlertDialog
import com.jobayr.arxiv.R
import com.kaopiz.kprogresshud.KProgressHUD

fun Activity.showExitDialog() {
    AlertDialog.Builder(this)
        .setTitle(getString(R.string.confirmation))
        .setMessage(getString(R.string.are_you_sure_to_exit))
        .setPositiveButton(getString(R.string.yes)) { _, _ ->
            finishAffinity()
        }
        .setNegativeButton(getString(R.string.cancel), null)
        .show()
}

fun Activity.waitingDialog(): KProgressHUD {
    return KProgressHUD.create(this)
        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
        .setLabel(getString(R.string.please_wait))
        .setCancellable(false)
        .setAnimationSpeed(2)
        .setDimAmount(0.5f)
}