package com.caterer.puja

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.caterer.puja.data.repo.DatabaseCateringRepository
import com.caterer.puja.data.seed.DatabaseSeeder
import com.caterer.puja.ui.CateringCalculatorScreen
import com.caterer.puja.viewmodel.MainViewModel

@Composable
@Preview
fun App(platformContext: Any? = null) {
    val repository = remember(platformContext) { DatabaseCateringRepository(platformContext) }
    val viewModel = remember(repository) { MainViewModel(repository = repository) }

    LaunchedEffect(platformContext) {
        runCatching {
            DatabaseSeeder.seedIfNeeded(platformContext)
            viewModel.reloadData()
        }
    }

    MaterialTheme {
        CateringCalculatorScreen(
            state = viewModel.uiState,
            onPeopleInputChange = viewModel::updatePeopleInput,
            onToggleDish = viewModel::toggleDish,
            onCalculate = viewModel::calculate,
        )
    }
}