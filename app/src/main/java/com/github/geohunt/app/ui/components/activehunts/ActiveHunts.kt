package com.github.geohunt.app.ui.components.activehunts

import androidx.compose.runtime.Composable
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.model.database.api.Challenge

@Composable
fun ActiveHunts(challenges: List<LazyRef<Challenge>>) {
    TitleText()
    
    ActiveHuntsList(challenges = challenges)
}

@Composable
fun TitleText() {
    
}

@Composable
fun ActiveHuntsList(challenges: List<LazyRef<Challenge>>) {
    
}