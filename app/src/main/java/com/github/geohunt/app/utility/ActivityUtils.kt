package com.github.geohunt.app.utility

import android.app.Activity
import android.content.Intent

/**
 * Replaces the current activity by a new one.
 */
fun Activity.replaceActivity(i: Intent) {
    i.flags = i.flags or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_TASK_ON_HOME
    startActivity(i)
}