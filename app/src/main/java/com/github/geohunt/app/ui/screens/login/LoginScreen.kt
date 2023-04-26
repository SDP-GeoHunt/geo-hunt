package com.github.geohunt.app.ui.screens.login

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.LoginActivity
import com.github.geohunt.app.R
import com.github.geohunt.app.ui.theme.md_theme_light_primary
import com.github.geohunt.app.ui.theme.seed

@OptIn(ExperimentalTextApi::class)
@Composable
fun LoginScreen(context: Context) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            textAlign = TextAlign.Center,
            style = TextStyle(
                brush = Brush.linearGradient(listOf(md_theme_light_primary, seed))
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(modifier = Modifier.testTag("signin-btn"), onClick = {
            val intent = Intent(context, LoginActivity::class.java)
            intent.putExtra("login", 1)
            context.startActivity(intent)
        }) {
            Text(stringResource(id = R.string.sign_in))
        }
    }
}