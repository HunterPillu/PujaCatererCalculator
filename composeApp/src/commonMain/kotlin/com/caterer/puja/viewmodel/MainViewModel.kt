package com.caterer.puja.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.caterer.puja.data.repo.CateringRepository
import com.caterer.puja.data.repo.InMemoryCateringRepository
import com.caterer.puja.domain.usecase.CalculateIngredientsUseCase

class MainViewModel(
    private val repository: CateringRepository = InMemoryCateringRepository(),
    private val calculateIngredients: CalculateIngredientsUseCase = CalculateIngredientsUseCase(),
) {
    var uiState by mutableStateOf(
        MainUiState(dishes = repository.getDishes()),
    )
        private set

    fun updatePeopleInput(value: String) {
        uiState = uiState.copy(
            peopleInput = value.filter { it.isDigit() },
            errorMessage = null,
        )
    }

    fun toggleDish(dishId: Int) {
        val selected = uiState.selectedDishIds.toMutableSet()
        if (!selected.add(dishId)) {
            selected.remove(dishId)
        }
        uiState = uiState.copy(selectedDishIds = selected, errorMessage = null)
    }

    fun calculate() {
        val people = uiState.peopleInput.toIntOrNull()

        if (people == null || people <= 0) {
            uiState = uiState.copy(errorMessage = "Enter valid number of people")
            return
        }

        if (uiState.selectedDishIds.isEmpty()) {
            uiState = uiState.copy(errorMessage = "Select at least one dish")
            return
        }

        val calculated = calculateIngredients(
            selectedDishIds = uiState.selectedDishIds,
            peopleCount = people,
            allIngredients = repository.getIngredients(),
        )

        uiState = uiState.copy(
            results = calculated,
            errorMessage = null,
        )
    }

    fun buildShareText(): String {
        val header = "Samaan list"
        if (uiState.results.isEmpty()) return header

        val body = uiState.results.joinToString(separator = "\n") {
            "${it.name}: ${it.quantityText}"
        }
        return "$header\n$body"
    }
}

