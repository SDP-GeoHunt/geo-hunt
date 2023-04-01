package com.github.geohunt.app.ui.controller

import android.graphics.Bitmap
import com.github.geohunt.app.model.LazyRef
import com.github.geohunt.app.ui.components.navigation.Route

/**
 * Navigate to the challenge view on a specific challenge ID
 *
 * @param cid the challenge id to navigate to
 */
fun NavController.viewChallenge(cid: String) {
    this.navigateTo("challenge-view/$cid")
}

/**
 * Navigate to the image view on a specific image ID
 *
 * @param iid the image unique identifier
 */
fun NavController.viewImage(iid: String) {
    this.navigateTo("image-view/$iid")
}

/**
 * Go to the "explore" pages
 */
fun NavController.explore() {
    this.navigateTo(Route.Explore.route)
}

/**
 * Open the claim "form" for a challenge with a given ID
 *
 * @param cid the unique identifier of the challenge to be claimed
 */
fun NavController.claim(cid: String) {
    this.navigateTo("claim-challenge/$cid")
}
