package com.github.geohunt.app.i18n

import android.app.Activity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.github.geohunt.app.BuildConfig
import java.util.*

object LocalUtils {
    fun getAvailableLocales() =
        Locale.getAvailableLocales().toList()
            .map<Locale, Locale?> { it }
            .filter { locale ->
                BuildConfig.TRANSLATION_ARRAY.toList().contains(locale.toString())
            } + null

    fun useLocale(activity: Activity, locale: Locale?) {
        if (locale == null) {
            // Use the AndroidX APIs to reset to the system locale
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.getEmptyLocaleList()
            )
        }
        else {
            val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(locale.toLanguageTag())
            activity.runOnUiThread {
                // Call this on the main thread as it may require Activity.restart()
                AppCompatDelegate.setApplicationLocales(appLocale)
            }
        }
    }
}