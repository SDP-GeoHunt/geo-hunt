package com.github.geohunt.app.ui.components.profile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.geohunt.app.model.EditedUser


@Composable
fun LocalSelector(editedUser: MutableState<EditedUser>) {
// TODO
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
