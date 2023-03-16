package com.github.geohunt.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.github.geohunt.app.model.database.Database
import com.github.geohunt.app.ui.components.CreateNewChallenge
import com.github.geohunt.app.ui.theme.GeoHuntTheme


class ComposeActivity : ComponentActivity() {

    private lateinit var database : Database

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        database = Database.createDatabaseHandle(this)

        setContent {
            GeoHuntTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    CreateNewChallenge(database,
                        onChallengeCreated = {
                            Log.i("GeoHunt", "The challenge was successfully created")
                        },
                        onFailure = {
                            Log.e("GeoHunt", "Failed because of exception $it")
                        }
                    )
                }
            }
        }
    }
}
