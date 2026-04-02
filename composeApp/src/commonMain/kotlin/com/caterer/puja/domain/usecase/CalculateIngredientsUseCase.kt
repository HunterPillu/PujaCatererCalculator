package com.caterer.puja.domain.usecase

import com.caterer.puja.data.model.Ingredient
import com.caterer.puja.data.model.UnitType
import com.caterer.puja.domain.model.CalculatedIngredient
import kotlin.math.round

class CalculateIngredientsUseCase {
    operator fun invoke(
        selectedDishIds: Set<String>,
        peopleCount: Int,
        allIngredients: List<Ingredient>,
    ): List<CalculatedIngredient> {
        if (peopleCount <= 0 || selectedDishIds.isEmpty()) return emptyList()

        val totals = linkedMapOf<Pair<String, UnitType>, Double>()

        allIngredients
            .asSequence()
            .filter { it.dishId in selectedDishIds }
            .forEach { ingredient ->
                val key = ingredient.name to ingredient.unit
                val total = ingredient.perPersonQty * peopleCount
                totals[key] = (totals[key] ?: 0.0) + total
            }

        return totals
            .map { (key, totalQty) ->
                val (name, unit) = key
                CalculatedIngredient(
                    name = name,
                    quantityText = formatQuantity(totalQty, unit),
                )
            }
            .sortedBy { it.name }
    }

    private fun formatQuantity(value: Double, unit: UnitType): String {
        return when (unit) {
            UnitType.GRAM -> {
                if (value >= 1000.0) "${round2(value / 1000.0)} kg" else "${round2(value)} g"
            }
            UnitType.ML -> {
                if (value >= 1000.0) "${round2(value / 1000.0)} liter" else "${round2(value)} ml"
            }
            UnitType.PIECE -> "${round2(value)} pcs"
        }
    }

    private fun round2(value: Double): String {
        val rounded = round(value * 100.0) / 100.0
        return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
    }
}

