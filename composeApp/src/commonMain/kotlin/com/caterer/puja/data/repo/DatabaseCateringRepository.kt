package com.caterer.puja.data.repo

import com.caterer.puja.data.db.DatabaseSetup
import com.caterer.puja.data.model.Dish
import com.caterer.puja.data.model.Ingredient
import com.caterer.puja.data.model.UnitType
import kotlinx.coroutines.runBlocking

class DatabaseCateringRepository(
    platformContext: Any? = null,
) : CateringRepository {
    private val store = DatabaseSetup.createStore(platformContext)

    override fun getDishes(): List<Dish> = runBlocking {
        store.dishDao().getAllDishes().mapNotNull { dish ->
            val dishId = parseTrailingNumber(dish.id) ?: return@mapNotNull null
            Dish(
                id = dishId,
                name = dish.localizedNames["en"] ?: dish.defaultName,
                category = dish.category,
            )
        }.sortedBy { it.id }
    }

    override fun getIngredients(): List<Ingredient> = runBlocking {
        val ingredientsById = store.ingredientDao().getAll().associateBy { it.id }

        store.mappingDao().getAllMappings().mapNotNull { mapping ->
            val dishId = parseTrailingNumber(mapping.dishId) ?: return@mapNotNull null
            val ingredient = ingredientsById[mapping.ingredientId] ?: return@mapNotNull null

            Ingredient(
                dishId = dishId,
                name = ingredient.localizedNames["en"] ?: ingredient.defaultName,
                perPersonQty = mapping.quantityPerPerson * mapping.consumptionRate,
                unit = mapping.unit.toUnitType(),
            )
        }
    }

    private fun parseTrailingNumber(value: String): Int? {
        val digits = value.takeLastWhile { it.isDigit() }
        return digits.toIntOrNull()
    }

    private fun String.toUnitType(): UnitType {
        return when (trim().lowercase()) {
            "g", "gram", "grams", "gm", "kg" -> UnitType.GRAM
            "ml", "milliliter", "millilitre", "milli liter", "ltr", "liter", "litre" -> UnitType.ML
            "pc", "pcs", "piece", "pieces", "nos", "count" -> UnitType.PIECE
            else -> UnitType.GRAM
        }
    }
}
