package com.github.geohunt.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.github.geohunt.app.database.Database
import com.github.geohunt.app.database.DatabaseFactory
import com.github.geohunt.app.database.firebase.FirebaseDatabase
import com.github.geohunt.app.ui.CreateChallengeView
import com.github.geohunt.app.ui.theme.GeoHuntTheme


class ComposeActivity : ComponentActivity() {

    private lateinit var database : Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = DatabaseFactory.createDatabaseHandle(this)

        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
//                    Greeting("Android")
                    CreateChallengeView(database)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    GeoHuntTheme {
        Greeting("Android")
    }
}