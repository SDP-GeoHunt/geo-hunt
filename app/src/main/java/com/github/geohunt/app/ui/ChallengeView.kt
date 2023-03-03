package com.github.geohunt.app.ui

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.geohunt.app.database.models.Challenge
import com.github.geohunt.app.database.models.Location
import com.github.geohunt.app.rememberLauncherForActivityWithPermission
import com.github.geohunt.app.ui.theme.Shapes
import com.github.geohunt.app.ui.theme.Typography
import com.github.geohunt.app.ui.theme.WeakColor

@Composable
fun ViewChallengeCompose(challenge: Challenge = Challenge(0, 0, Location(0.0, 0.0))) {
    Column {

    }
}

@Preview
@Composable
fun CreateChallengeView() {
    val photoIconSvg : Painter = painterResource(id = com.github.geohunt.app.R.drawable.photo_camera_svgrepo_com)
    val photoPainter = remember {
        mutableStateOf<Painter?>(null)
    }
    val publicCheckboxState = remember {
        mutableStateOf(false)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicturePreview(),
            onResult = { bitmap ->
        if (bitmap != null) {
            photoPainter.value = BitmapPainter(bitmap.asImageBitmap())
        }
    })
    val safeCameraLauncher = rememberLauncherForActivityWithPermission(
            launcher = cameraLauncher,
            permission = Manifest.permission.CAMERA
    )

    Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxHeight()
    ) {
        Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Title(text = "Create Challenge", textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(5.dp))

            Subtitle(text = "Challenge your friends to find the current location", textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(30.dp))

            Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(if (photoPainter.value == null) 0.5f else 1.0f))
            {
                Image(painter = photoPainter.value ?: photoIconSvg,
                contentDescription = "Take a photo!",
                modifier = Modifier.clickable {
                    safeCameraLauncher(null)
                })
            }

            Spacer(modifier = Modifier.height(15.dp))

            LinkText(linkTextData = listOf(
                    LinkTextData(
                            text = "By creating a challenge, you agree to GeoHunt's "
                    ),
                    LinkTextData(
                            text = "Community Guidelines",
                            tag = "",
                            annotation = "https://www.github.com/",
                            onClick = {}
                    ),
                    LinkTextData(
                            text = "."
                    )
            ), style = Typography.h5, primaryColor = WeakColor)

            Spacer(modifier = Modifier.height(35.dp))

            LabelledSwitch(
                    checked = publicCheckboxState.value,
                    onCheckedChange = { publicCheckboxState.value = it },
                    label = "Public challenge"
            )

            Spacer(modifier = Modifier.height(35.dp))

            Box(modifier = Modifier.padding(10.dp, 0.dp)) {
                Button(
                        onClick = {},
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                        shape = Shapes.medium,
                        enabled = (photoPainter.value != null)
                ) {
                    Text(text = "Create Challenge")
                }
            }
        }
    }
}
