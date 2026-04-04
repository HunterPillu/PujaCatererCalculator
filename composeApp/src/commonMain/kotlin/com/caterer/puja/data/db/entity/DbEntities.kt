package com.caterer.puja.data.db.entity

data class DishEntity(
    val id: String,
    val defaultName: String,
    val localizedNames: Map<String, String>,
    val category: String,
    val isVeg: Boolean,
    val isMainCourse: Boolean,
)

data class IngredientEntity(
    val id: String,
    val defaultName: String,
    val localizedNames: Map<String, String>,
    val category: String,
    val unit: String,
)

data class DishIngredientMapEntity(
    val dishId: String,
    val ingredientId: String,
    val quantityPerPerson: Double,
    val unit: String,
    val consumptionRate: Double,
)

data class EventEntity(
    val id: Long = 0,
    val name: String,
    val peopleCount: Int,
    val isBuffet: Boolean,
    val eventType: String,
    val bufferPercent: Double,
)

data class EventDishCrossRef(
    val eventId: Long,
    val dishId: String,
)

data class DishWithIngredients(
    val dish: DishEntity,
    val ingredients: List<IngredientEntity>,
)

data class EventWithDishes(
    val event: EventEntity,
    val dishes: List<DishEntity>,
)

