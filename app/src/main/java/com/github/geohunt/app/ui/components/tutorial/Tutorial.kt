@file:OptIn(ExperimentalFoundationApi::class)

package com.github.geohunt.app.ui.components.tutorial

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.LoginActivity
import kotlinx.coroutines.launch
import com.github.geohunt.app.utility.replaceActivity
import kotlinx.coroutines.CoroutineScope

/**
 * The main function for the tutorial that displays the
 * tutorial slides and the buttons for navigating through
 *
 * @param activity The activity that the tutorial is being displayed in
 */
@Composable
fun Tutorial(activity: ComponentActivity) {
    val items = TutorialSlides.getData()
    val scope = rememberCoroutineScope()
    val pageState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("Tutorial screen layout")
    ) {
        TopButtons(
            activity = activity,
            pageState = pageState,
            scope = scope
        )

        HorizontalPager(
            pageCount = items.size,
            state = pageState,
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth()
                .testTag("Current page is ${pageState.currentPage}"),
            pageContent = {
                page -> TutorialSlideContent(slide = items[page])
            },
        )

        BottomButtons(
            activity = activity,
            pageState = pageState,
            scope = scope,
            slidesCount = items.size,
            index = pageState.currentPage,
            onButtonClick = {
                if (pageState.currentPage + 1 < items.size) scope.launch {
                    pageState.scrollToPage(pageState.currentPage + 1)
                }
            }
        )
    }
}

/**
 * Displays the buttons for moving to the next slide
 * and skipping the tutorial at the top of the screen
 *
 * @param activity The activity that the tutorial is being displayed in
 * @param pageState The state of the pager that the tutorial is using
 * @param scope The coroutine scope that the tutorial is running in
 */
@Composable
fun TopButtons(
    activity: ComponentActivity,
    pageState: PagerState,
    scope: CoroutineScope,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Button to go to the previous tutorial slide
        if (pageState.currentPage != 0) {
            Button(
                onClick = {
                    if (pageState.currentPage > 0) scope.launch {
                        pageState.scrollToPage(pageState.currentPage - 1)
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .testTag("Go back button"),
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = "Go back button"
                )
            }
        }

        // Button to skip the tutorial and go to the login screen
        TextButton(
            onClick = {
                activity.replaceActivity(i = Intent(activity, LoginActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .testTag("Skip button"),
        ) {
            Text(
                text = "Skip",
                color = MaterialTheme.colors.onBackground,
                fontSize = 16.sp,
            )
        }
    }
}

/**
 * Displays the button for moving to the next tutorial slide
 * at the bottom of the tutorial screen
 *
 * @param activity The current activity
 * @param pageState The state of the pager
 * @param scope The coroutine scope
 * @param slidesCount The size of the tutorial slides
 * @param index The index of the current tutorial slide
 * @param onButtonClick The function to call when the button is clicked
 */
@Composable
fun BottomButtons(
    activity: ComponentActivity,
    pageState: PagerState,
    scope: CoroutineScope,
    slidesCount: Int,
    index: Int,
    onButtonClick: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        FloatingActionButton(
            onClick = {
                if (pageState.currentPage + 1 < slidesCount)
                    scope.launch { pageState.scrollToPage(pageState.currentPage + 1) }
                else activity.replaceActivity(Intent(activity, LoginActivity::class.java))
            },
            containerColor = Color.Black,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .clip(RoundedCornerShape(15.dp, 15.dp, 15.dp, 15.dp))
                .testTag("Go forward button"),
        ) {
            Icon(Icons.Outlined.KeyboardArrowRight,
                tint = Color.White,
                contentDescription = "Go forward button",
            )
        }
    }
}

/**
 * Displays the content of a slide
 *
 * @param slide: The list of tutorial slides
 */
@Composable
fun TutorialSlideContent(slide: TutorialSlides) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = slide.icon),
            contentDescription = "Tutorial Image",
            alignment = Alignment.Center,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .testTag("Tutorial Image")
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(id = slide.title),
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            modifier = Modifier
                .testTag("Tutorial Title"),
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.onBackground,
            )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = slide.description),
            fontSize = 20.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.h1,
            color = MaterialTheme.colors.onBackground,
            modifier = Modifier
                .testTag("Tutorial Description"),
            letterSpacing = 1.sp,
        )
    }
}