package com.github.geohunt.app.ui.components.tutorial

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.geohunt.app.LoginActivity
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import com.github.geohunt.app.utility.replaceActivity
import com.google.accompanist.pager.PagerState
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

    Column(modifier = Modifier.fillMaxSize()) {
        TopButtons(
            activity = activity,
            pageState = pageState,
            scope = scope
        )

        HorizontalPager(
            count = items.size,
            state = pageState,
            modifier = Modifier
                .fillMaxHeight(0.9f)
                .fillMaxWidth(),
            content = {
                page -> TutorialSlideContent(items = items[page])
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
                    if (pageState.currentPage + 1 > 1) scope.launch {
                        pageState.scrollToPage(pageState.currentPage - 1)
                    }
                },
                modifier = Modifier.align(Alignment.CenterStart),
            ) {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowLeft,
                    contentDescription = "Back button"
                )
            }
        }

        // Button to skip the tutorial and go to the login screen
        TextButton(
            onClick = {
                activity.replaceActivity(i = Intent(activity, LoginActivity::class.java))
            },
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Text(
                text = "Skip",
                color = MaterialTheme.colorScheme.onBackground,
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
        ) {
            Icon(Icons.Outlined.KeyboardArrowRight,
                tint = Color.White,
                contentDescription = "Localized description")
        }
    }
}

/**
 * Displays the content of a list of tutorial slides
 *
 * @param items: The list of tutorial slides
 */
@Composable
fun TutorialSlideContent(items: TutorialSlides) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = items.icon),
            contentDescription = "Tutorial Image",
            alignment = Alignment.Center,
            modifier = Modifier
                .size(240.dp)
        )

        Spacer(modifier = Modifier.height(25.dp))

        Text(
            text = stringResource(id = items.title),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp,
            modifier = Modifier.testTag("Tutorial Title"),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = items.description),
            fontSize = 25.sp,
            fontWeight = FontWeight.Light,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .padding(10.dp)
                .testTag("Tutorial Description"),
            letterSpacing = 1.sp,
        )
    }
}