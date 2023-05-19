package com.github.geohunt.app.ui.components.bounties

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.geohunt.app.R
import com.github.geohunt.app.i18n.DateFormatUtils
import com.github.geohunt.app.model.Bounty
import com.github.geohunt.app.model.Location
import com.github.geohunt.app.ui.components.bounties.viewmodel.CreateBountyViewModel
import com.github.geohunt.app.ui.components.challengecreation.*
import com.github.geohunt.app.ui.components.utils.LocationPicker
import com.ireward.htmlcompose.HtmlText
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate

@Composable
fun CreateNewBounty(
    onFailure: (Throwable) -> Unit,
    onSuccess: (Bounty) -> Unit,
    viewModel: CreateBountyViewModel = viewModel(factory = CreateBountyViewModel.Factory)
) {
    val uriHandler = LocalUriHandler.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Create new bounty",
                fontSize = 35.sp
            )

            Spacer(Modifier.height(55.dp))

            val expirationDate = viewModel.expirationDate.collectAsState()
            val startingDate = viewModel.startingDate.collectAsState()
            val location = viewModel.location.collectAsState()
            val name = viewModel.name.collectAsState()

            BountySetting(
                startingDate = startingDate.value,
                expirationDate = expirationDate.value,
                location = location.value,
                name = name.value,
                setStartingDate = viewModel::withStartingDate,
                setExpirationDate = viewModel::withExpirationDate,
                setLocation = viewModel::withLocation,
                setName = viewModel::withName
            )

            Spacer(Modifier.height(15.dp))

            HtmlText(
                text = stringResource(id = R.string.challenge_create_agree_community_link),
                modifier = Modifier.padding(25.dp, 0.dp),
                linkClicked = { url ->
                    uriHandler.openUri(url)
                }
            )

            Spacer(Modifier.height(15.dp))

            Button(
                enabled = expirationDate.value != null &&
                          startingDate.value != null &&
                          location.value != null &&
                          name.value.isNotEmpty(),
                modifier = Modifier.testTag("create-btn"),
                onClick = { viewModel.create(onFailure, onSuccess) }) {
                Text(text = stringResource(R.string.create_bounty_button))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BountySetting(
    startingDate: LocalDate?,
    expirationDate: LocalDate?,
    location: Location?,
    name: String,
    setStartingDate: (LocalDate) -> Unit,
    setExpirationDate: (LocalDate) -> Unit,
    setLocation: (Location) -> Unit,
    setName: (String) -> Unit,
) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 10.dp),
        verticalArrangement = Arrangement.SpaceBetween) {

        val state = UseCaseState(embedded = false, onDismissRequest = {})

        CalendarDialog(state = state,
            selection = CalendarSelection.Period { first, second ->
                setStartingDate(first)
                setExpirationDate(second)
            })

        SettingLine(text = stringResource(R.string.name)) {
            TextField(value = name, onValueChange = setName, placeholder = {
                Text("Bounty Name")
            }, singleLine = true, modifier = Modifier.testTag("bounty-name-field"))
        }

        Spacer(modifier = Modifier.height(5.dp))

        SettingLine(text = stringResource(id = R.string.range_date)) {
            val dateString = startingDate?.run { expirationDate?.run {
                "from ${DateFormatUtils.formatDate(startingDate)} to ${DateFormatUtils.formatDate(expirationDate)}"
            } } ?: ""

            TextField(value = dateString,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.testTag("date-field").clickable { state.show() })
        }
        
        Spacer(modifier = Modifier.height(5.dp))

        SettingLine(text = stringResource(R.string.location)) {
            LocationPicker(location = location, setLocation = setLocation)
        }
    }
}

@Composable
private fun SettingLine(text: String, content: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {

        Text(
            text = text,
            fontSize = 19.sp
        )

        content()
    }
}

