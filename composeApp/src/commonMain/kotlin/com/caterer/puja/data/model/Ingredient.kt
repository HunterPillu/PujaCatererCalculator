package com.caterer.puja.data.model

enum class UnitType {
    GRAM,
    ML,
    PIECE,
}

data class Ingredient(
    val dishId: Int,
    val name: String,
    val perPersonQty: Double,
    val unit: UnitType,
)

