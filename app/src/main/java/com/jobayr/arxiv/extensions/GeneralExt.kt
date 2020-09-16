package com.jobayr.arxiv.extensions

import android.app.Activity
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.jobayr.arxiv.R

fun AppCompatActivity.initToolbar(title: String) {
    supportActionBar?.title = title
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    supportActionBar?.setHomeAsUpIndicator(
        ResourcesCompat.getDrawable(
            resources,
            R.drawable.icon_back,
            theme
        )
    )
}

fun Activity.launch(className: Class<*>) = startActivity(Intent(this, className))

fun View.onClick(onClick: () -> Unit) {
    this.setOnClickListener {
        onClick()
    }
}

fun View.makeVisible() {
    this.apply {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
    }
}

fun View.makeGone() {
    this.apply {
        if (visibility == View.VISIBLE) {
            visibility = View.GONE
        }
    }
}

fun Activity.showMsg(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
