package com.github.geohunt.app.ui

import android.Manifest
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.github.geohunt.app.database.Database
import com.github.geohunt.app.database.models.Challenge
import com.github.geohunt.app.database.models.Location
import com.github.geohunt.app.sensor.PhotoRequest
import com.github.geohunt.app.sensor.rememberLocationRequestState
import com.github.geohunt.app.sensor.rememberPermissionsState


//@Composable
//private fun CreateChallengeSubmitView(isPublic: Boolean, database: Database, picture: Picture,
//                                      onChallengeCreated: (Challenge) -> Unit = {},
//                                      onCancelled: (Exception) -> Unit = {}) {
//    val context = LocalContext.current
//    val locationPermissionsState = rememberMultiplePermissionsState(
//        listOf(
//            android.Manifest.permission.ACCESS_COARSE_LOCATION,
//            android.Manifest.permission.ACCESS_FINE_LOCATION
//        )
//    )
//
//    val locationRequester = ForegroundLocationRequest(context as Activity)
//        .getCurrentLocationRequester();
//    val subtitleStringState = remember {
//        mutableStateOf("Finding out your location")
//    }
//
//    LaunchedEffect(true) {
//        locationRequester()
//            .addOnSuccessListener {
//                subtitleStringState.value = "Submitting to the server"
//            }
//            .addOnFailureListener {
//                Toast.makeText(context, "Cannot retrieve location", Toast.LENGTH_LONG).show()
//            }
//            .flatMap { location ->
//                database.createChallenge(
//                    picture = picture,
//                    location = location,
//                    isPublic = isPublic
//                )
//            }
//            .addOnSuccessListener {
//                onChallengeCreated(it)
//            }
//            .addOnFailureListener { exception ->
//                Toast.makeText(context, "Failed to register to database $exception", Toast.LENGTH_LONG).show()
//                onCancelled(exception)
//            }
//    }
//
//    Box(
//        contentAlignment = Alignment.Center,
//        modifier = Modifier
//            .fillMaxHeight()
//            .fillMaxWidth(),
//    ) {
//        Column(
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Title("Creating challenge")
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            Subtitle(subtitleStringState.value)
//        }
//    }
//}
//
//@Composable
//private fun CreateChallengeFormView(onCancelled: (Exception) -> Unit,
//                                    onChallengeCreatedClicked: (Boolean, Picture) -> Unit)
//{
//    val context = LocalContext.current
//    val takePhotoIconPainter: Painter = painterResource(id = com.github.geohunt.app.R.drawable.photo_camera_svgrepo_com)
//    val nullablePhotoPainterState = remember { mutableStateOf<Painter?>(null) }
//    val photoState = remember { mutableStateOf<Bitmap?>(null) }
//    val publicAvailableCheckboxState = remember { mutableStateOf(false) }
//
//    val cameraLauncher = attachPermissionToLaunch(
//            Manifest.permission.CAMERA,
//            launcher = rememberLauncherForActivityResult(
//                    contract = ActivityResultContracts.TakePicturePreview(),
//                    onResult = { bitmap ->
//                        if (bitmap != null) {
//                            photoState.value = bitmap
//                            nullablePhotoPainterState.value = BitmapPainter(bitmap.asImageBitmap())
//                        }
//                    }))
//
//    val communityGuidelinesUrl = stringResource(R.string.community_guidelines_url);
//    val openCommunityGuidelinesIntent = remember {
//        Intent(Intent.ACTION_VIEW, Uri.parse(communityGuidelinesUrl))
//    }
//
//    Box(
//            contentAlignment = Alignment.Center,
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxWidth()
//    ) {
//        Column(
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
//                modifier = Modifier.verticalScroll(state = rememberScrollState())
//        ) {
//            Title(text = "Create Challenge", textAlign = TextAlign.Center)
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Subtitle(text = "Challenge your friends to find the current location", textAlign = TextAlign.Center)
//
//            Spacer(modifier = Modifier.height(30.dp))
//
//            Box(contentAlignment = Alignment.Center,
//                    modifier = Modifier.fillMaxWidth(if (nullablePhotoPainterState.value == null) 0.5f else 1.0f))
//            {
//                Image(painter = nullablePhotoPainterState.value ?: takePhotoIconPainter,
//                        contentDescription = "Take a photo!",
//                        modifier = Modifier.clickable {
//                            cameraLauncher(null)
//                        })
//            }
//
//            Spacer(modifier = Modifier.height(15.dp))
//
//            LinkText(linkTextData = listOf(
//                    LinkTextData(
//                            text = "By creating a challenge, you agree to GeoHunt's "
//                    ),
//                    LinkTextData(
//                            text = "Community Guidelines",
//                            tag = "",
//                            annotation = "",
//                            onClick = {
//                                context.startActivity(openCommunityGuidelinesIntent);
//                            }
//                    ),
//                    LinkTextData(
//                            text = "."
//                    )
//            ), style = Typography.h5, primaryColor = WeakColor)
//
//            Spacer(modifier = Modifier.height(35.dp))
//
//            LabelledSwitch(
//                    checked = publicAvailableCheckboxState.value,
//                    onCheckedChange = { publicAvailableCheckboxState.value = it },
//                    label = "Public challenge"
//            )
//
//            Spacer(modifier = Modifier.height(45.dp))
//
//            Box(modifier = Modifier.padding(10.dp, 0.dp)) {
//                Button(
//                        onClick = {
//                            onChallengeCreatedClicked(publicAvailableCheckboxState.value,
//                                    Picture(photoState.value!!))
//                        },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(50.dp),
//                        shape = Shapes.medium,
//                        enabled = (nullablePhotoPainterState.value != null)
//                ) {
//                    Text(text = "Create Challenge")
//                }
//            }
//        }
//    }
//}

//@Composable
//fun CreateChallengeView(database: Database,
//                        onChallengeCreated: (Challenge) -> Unit = {},
//                        onCancelled: (Exception) -> Unit = {}
//) {
//    val isPublicState = remember { mutableStateOf<Boolean?>(null) }
//    val pictureState = remember { mutableStateOf<Picture?>(null); }
//
//
//    if (isPublicState.value != null && pictureState.value != null) {
//        CreateChallengeSubmitView(isPublicState.value!!, database, pictureState.value!!, onChallengeCreated, onCancelled)
//    }
//    else {
//        CreateChallengeFormView(onCancelled){ isPublic: Boolean, picture: Picture ->
//            isPublicState.value = isPublic
//            pictureState.value = picture
//        }
//    }
//}


@Composable
fun CreateChallengeView(database: Database,
                        onChallengeCreated: (Challenge) -> Unit = {},
                        onFailure: (Throwable) -> Unit = {})
{
    val photoRequest = PhotoRequest()
    
    photoRequest.CameraView(onImageCaptured = {}, onError = {})
}

//@Composable
//fun CreateChallengeView(database: Database,
//                        onChallengeCreated: (Challenge) -> Unit = {},
//                        onFailure: (Exception) -> Unit = {})
//{
//    val context = LocalContext.current
//    val locationRequest = rememberLocationRequestState()
//    val location = remember {
//        mutableStateOf<Location?>(null)
//    }
//
//    Column(
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally,
//        modifier = Modifier.verticalScroll(state = rememberScrollState())
//    ) {
//        Text(if (location.value != null) "location is ${location.value}" else "location is ???")
//
//        Button(onClick = {
//            locationRequest.launchLocationRequest()
//                .thenAccept {
//                    location.value = it
//                }
//                .exceptionally {
//                    Toast.makeText(context, "exception: $it", Toast.LENGTH_SHORT).show()
//                    null
//                }
//        }) {
//            Text(text = "Get Location")
//        }
//    }
//}
//
