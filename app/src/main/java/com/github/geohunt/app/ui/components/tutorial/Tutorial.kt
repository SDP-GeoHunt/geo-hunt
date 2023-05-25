@file:OptIn(ExperimentalFoundationApi::class)

package com.github.geohunt.app.ui.components.tutorial

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.github.geohunt.app.R
import com.google.accompanist.pager.HorizontalPagerIndicator

/**
 * The main function for the tutorial that displays the
 * tutorial slides and the buttons for navigating through
 *
 * @param onExit The callback that will be called when the tutorial is finished.
 */
@Composable
fun Tutorial(onExit: () -> Unit) {
    val items = TutorialSlides.getData()
    val pageState = rememberPagerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("Tutorial screen layout")
    ) {
        TopButtons { onExit() }

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
            pagerState = pageState,
            pageCount = items.size,
            onExit = { onExit() }
        )
    }
}

/**
 * Displays the skip button
 *
 * @param onSkip When the skip button is clicked
 */
@Composable
fun TopButtons(
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Button to skip the tutorial and go to the login screen
        TextButton(
            onClick = {
                onSkip()
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .testTag("Skip button"),
        ) {
            Text(
                text = stringResource(id = R.string.tutorial_skip),
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
 * @param pagerState The state of the pager
 * @param pageCount The size of the tutorial slides
 * @param onExit Called when arrived at the end page
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomButtons(
    pagerState: PagerState,
    pageCount: Int,
    onExit: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val canGoLeft = pagerState.currentPage >= 1
        IconButton(
            onClick = {
                coroutineScope.launch { pagerState.scrollToPage(pagerState.currentPage - 1) }
            },
            enabled = canGoLeft,
            modifier = Modifier.testTag("Go back button")
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = stringResource(id = R.string.go_left))
        }

        Spacer(Modifier.weight(1f))

        HorizontalPagerIndicator(pagerState, pageCount)

        Spacer(Modifier.weight(1f))

        IconButton(
            modifier = Modifier.testTag("Go forward button"),
            onClick = {
                if (pagerState.currentPage >= pageCount - 1)
                    onExit()
                else
                    coroutineScope.launch { pagerState.scrollToPage(pagerState.currentPage + 1) }
            }
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = stringResource(id = R.string.go_right))
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