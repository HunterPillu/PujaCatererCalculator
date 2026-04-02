package com.caterer.puja

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.caterer.puja.ui.CateringCalculatorScreen
import com.caterer.puja.viewmodel.MainViewModel

@Composable
@Preview
fun App() {
    val viewModel = remember { MainViewModel() }

    MaterialTheme {
        CateringCalculatorScreen(
            state = viewModel.uiState,
            onPeopleInputChange = viewModel::updatePeopleInput,
            onToggleDish = viewModel::toggleDish,
            onCalculate = viewModel::calculate,
        )
    }
}