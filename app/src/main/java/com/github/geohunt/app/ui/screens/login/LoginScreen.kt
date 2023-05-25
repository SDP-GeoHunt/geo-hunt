package com.github.geohunt.app.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.R

@Composable
fun LoginScreen(onSignInClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = stringResource(id = R.string.app_name),
            modifier = Modifier.fillMaxSize(.7f).testTag("logo")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(modifier = Modifier.testTag("signin-btn"), onClick = onSignInClick) {
            Text(stringResource(id = R.string.sign_in))
        }
    }
}