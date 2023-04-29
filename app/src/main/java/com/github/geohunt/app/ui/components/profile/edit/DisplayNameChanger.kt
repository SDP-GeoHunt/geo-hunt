package com.github.geohunt.app.ui.components.profile.edit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import com.github.geohunt.app.R

@Composable
fun DisplayNameChanger(value: String, onChange: (String) -> Any) {

    Row {
       TextField(
           modifier = Modifier
               .fillMaxWidth()
               .testTag("display-name-input"),
           value = value,
           onValueChange = { onChange(it) },
           label = { Text(stringResource(id = R.string.display_name)) },
           singleLine = true
       )
    }
}
