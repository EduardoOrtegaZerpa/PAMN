/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.cupcake

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.cupcake.data.DataSource
import com.example.cupcake.data.OrderUiState
import com.example.cupcake.ui.*

enum class Screen(@StringRes val titleResId: Int) {
    Start(R.string.app_name),
    Flavor(R.string.choose_flavor),
    Pickup(R.string.choose_pickup_date),
    Summary(R.string.order_summary)
}

@Composable
fun CupcakeApp() {
    val viewModel: OrderViewModel = viewModel()
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { AppBarWithNavigation(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Start.name) { StartScreen(viewModel, navController) }
            composable(Screen.Flavor.name) { FlavorScreen(viewModel, navController, uiState) }
            composable(Screen.Pickup.name) { PickupScreen(viewModel, navController, uiState) }
            composable(Screen.Summary.name) { SummaryScreen(viewModel, navController, uiState) }
        }
    }
}

@Composable
fun AppBarWithNavigation(navController: NavHostController) {
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(currentBackStack?.destination?.route ?: Screen.Start.name)

    TopAppBar(
        title = { Text(stringResource(currentScreen.titleResId)) },
        navigationIcon = {
            if (navController.previousBackStackEntry != null) {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun StartScreen(viewModel: OrderViewModel, navController: NavHostController) {
    StartOrderScreen(
        quantityOptions = DataSource.quantityOptions,
        onNextButtonClicked = {
            viewModel.setQuantity(it)
            navController.navigate(Screen.Flavor.name)
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
    )
}

@Composable
fun FlavorScreen(viewModel: OrderViewModel, navController: NavHostController, uiState: OrderUiState) {
    val context = LocalContext.current
    SelectOptionScreen(
        subtotal = uiState.price,
        onNextButtonClicked = { navController.navigate(Screen.Pickup.name) },
        onCancelButtonClicked = { resetOrder(viewModel, navController) },
        options = DataSource.flavors.map { id -> context.resources.getString(id) },
        onSelectionChanged = { viewModel.setFlavor(it) },
        modifier = Modifier.fillMaxHeight()
    )
}

@Composable
fun PickupScreen(viewModel: OrderViewModel, navController: NavHostController, uiState: OrderUiState) {
    SelectOptionScreen(
        subtotal = uiState.price,
        onNextButtonClicked = { navController.navigate(Screen.Summary.name) },
        onCancelButtonClicked = { resetOrder(viewModel, navController) },
        options = uiState.pickupOptions,
        onSelectionChanged = { viewModel.setDate(it) },
        modifier = Modifier.fillMaxHeight()
    )
}

@Composable
fun SummaryScreen(viewModel: OrderViewModel, navController: NavHostController, uiState: OrderUiState) {
    val context = LocalContext.current
    OrderSummaryScreen(
        orderUiState = uiState,
        onCancelButtonClicked = { resetOrder(viewModel, navController) },
        onSendButtonClicked = { subject, summary -> shareOrder(context, subject, summary) },
        modifier = Modifier.fillMaxHeight()
    )
}

private fun resetOrder(viewModel: OrderViewModel, navController: NavHostController) {
    viewModel.resetOrder()
    navController.popBackStack(Screen.Start.name, inclusive = false)
}

private fun shareOrder(context: Context, subject: String, summary: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, summary)
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.new_cupcake_order)))
}
