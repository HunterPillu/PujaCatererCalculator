package com.caterer.puja.viewmodel

import com.caterer.puja.data.model.Dish
import com.caterer.puja.domain.model.CalculatedIngredient

data class MainUiState(
    val peopleInput: String = "",
    val dishes: List<Dish> = emptyList(),
    val selectedDishIds: Set<String> = emptySet(),
    val results: List<CalculatedIngredient> = emptyList(),
    val errorMessage: String? = null,
)

