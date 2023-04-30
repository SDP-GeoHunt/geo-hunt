package com.github.geohunt.app.ui.components.profile

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.i18n.LocalUtils
import com.github.geohunt.app.model.database.api.EditedUser
import com.github.geohunt.app.ui.components.utils.ListDropdownMenu
import com.github.geohunt.app.utility.findActivity
import java.util.*


@Composable
fun LocalSelector(editedUser: MutableState<EditedUser>) {
//    val activity = LocalContext.current.findActivity()
//    val availableLocales = remember {
//        LocalUtils.getAvailableLocales()
//    }
//    val selectedLocale = remember { mutableStateOf<Locale?>(null) }
//
//    Row {
//
//        Text(
//            text = "Language",
//            fontSize = 20.sp,
//            modifier = Modifier
//                .align(Alignment.CenterVertically)
//                .padding(0.dp, 20.dp)
//        )
//
//        ListDropdownMenu(
//            state = selectedLocale,
//            elements = availableLocales,
//            toString = { locale ->
//                locale?.displayName ?: "System Default"
//            },
//            onValueChanged = { locale ->
//                if (locale?.toString() != editedUser.value.preferredLocale) {
//                    Log.i("GeoHunt", "Locale set to ${locale?.toString()}")
//                    editedUser.value = editedUser.value.copy(preferredLocale = locale?.toString())
//                    LocalUtils.useLocale(activity, locale)
//                }
//            }
//        )
//    }
}
