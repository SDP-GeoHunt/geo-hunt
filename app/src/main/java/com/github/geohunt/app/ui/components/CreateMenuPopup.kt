package com.github.geohunt.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CreateMenuPopup(
    state: ModalBottomSheetState,
    createChallenge: () -> Unit,
    createBounty: () -> Unit,
    scope: CoroutineScope,
    content: @Composable () -> Unit
) {

    ModalBottomSheetLayout(sheetState = state, sheetContent = {
        Column {
            ListItem(
                text = { Text("Create new Challenge") },
                icon = {
                    Icon(
                        Icons.Default.PostAdd,
                        contentDescription = "New challenge icon"
                    )
                },

                modifier = Modifier.clickable {
                    scope.launch { state.hide() }
                    createChallenge()
                }
            )

            ListItem(
                text = { Text("Create new Bounty") },
                icon = {
                    Icon(
                        Icons.Default.PlaylistAdd,
                        contentDescription = "New bounty icon"
                    )
                },

                modifier = Modifier.clickable {
                    scope.launch { state.hide() }
                    createBounty()
                }
            )
        }
    }) {
        content()
    }
}