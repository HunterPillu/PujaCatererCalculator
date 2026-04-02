package com.caterer.puja.data.repo

import com.caterer.puja.data.model.Dish
import com.caterer.puja.data.model.Ingredient
import com.caterer.puja.data.model.UnitType

class InMemoryCateringRepository : CateringRepository {
    private val dishes = listOf(
        Dish(id = "chicken_curry", name = "Chicken Curry"),
        Dish(id = "rice", name = "Rice"),
        Dish(id = "roti", name = "Roti"),
        Dish(id = "dal", name = "Dal"),
    )

    private val ingredients = listOf(
        Ingredient("chicken_curry", "Chicken", 250.0, UnitType.GRAM),
        Ingredient("chicken_curry", "Onion", 40.0, UnitType.GRAM),
        Ingredient("chicken_curry", "Tomato", 35.0, UnitType.GRAM),
        Ingredient("chicken_curry", "Oil", 12.0, UnitType.ML),
        Ingredient("rice", "Rice", 120.0, UnitType.GRAM),
        Ingredient("rice", "Water", 220.0, UnitType.ML),
        Ingredient("rice", "Salt", 1.0, UnitType.GRAM),
        Ingredient("roti", "Wheat Flour", 90.0, UnitType.GRAM),
        Ingredient("roti", "Oil", 4.0, UnitType.ML),
        Ingredient("roti", "Salt", 0.8, UnitType.GRAM),
        Ingredient("dal", "Lentils", 70.0, UnitType.GRAM),
        Ingredient("dal", "Onion", 20.0, UnitType.GRAM),
        Ingredient("dal", "Tomato", 20.0, UnitType.GRAM),
        Ingredient("dal", "Oil", 6.0, UnitType.ML),
    )

    override fun getDishes(): List<Dish> = dishes

    override fun getIngredients(): List<Ingredient> = ingredients
}

