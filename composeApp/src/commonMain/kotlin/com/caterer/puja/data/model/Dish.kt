package com.caterer.puja.data.model

data class Dish(
    val id: Int,
    val name: String,
    val category: String
)

fun filterDishesByCategory(category: String, dishes: List<Dish>): List<Dish> {
    val query = category.trim()
    return dishes.filter { it.category.equals(query, ignoreCase = true) }
}




