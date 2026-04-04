package com.caterer.puja.data.seed

import android.content.Context
import com.caterer.puja.data.db.AppDatabaseStore
import com.caterer.puja.data.db.DatabaseSetup
import com.caterer.puja.data.db.entity.DishEntity
import com.caterer.puja.data.db.entity.IngredientEntity
import org.json.JSONArray
import org.json.JSONObject

object AssetDatabaseSeeder {
    private const val DISHES_FILE = "dishes.json"
    private const val INGREDIENTS_VEGETABLES_FILE = "ingredients_vegetables.json"
    private const val INGREDIENTS_RATION_FILE = "ingredients_ration.json"

    suspend fun seedIfNeeded(context: Context) {
        val store = DatabaseSetup.createStore(context)
        val hasDishes = store.dishDao().getAllDishes().isNotEmpty()
        val hasIngredients = store.ingredientDao().getAll().isNotEmpty()

        if (hasDishes && hasIngredients) return

        if (!hasDishes) {
            store.dishDao().insertDishes(parseDishes(readAsset(context, DISHES_FILE)))
        }

        if (!hasIngredients) {
            val ingredients = buildList {
                addAll(parseIngredients(readAsset(context, INGREDIENTS_VEGETABLES_FILE)))
                addAll(parseIngredients(readAsset(context, INGREDIENTS_RATION_FILE)))
            }.distinctBy { it.id }
            store.ingredientDao().insertIngredients(ingredients)
        }
    }

    private fun parseDishes(json: String): List<DishEntity> {
        val array = JSONArray(json)
        return buildList {
            repeat(array.length()) { index ->
                val item = array.getJSONObject(index)
                val dishId = item.getString("id")
                val name = item.getString("name")
                val category = item.getString("category")

                add(
                    DishEntity(
                        id = dishId,
                        defaultName = name,
                        localizedNames = mapOf("en" to name),
                        category = category,
                        isVeg = isVegDish(name, category),
                        isMainCourse = isMainCourseDish(category),
                    ),
                )
            }
        }
    }

    private fun parseIngredients(json: String): List<IngredientEntity> {
        val array = JSONArray(json)
        return buildList {
            repeat(array.length()) { index ->
                val item: JSONObject = array.getJSONObject(index)
                val name = item.getString("defaultName")
                add(
                    IngredientEntity(
                        id = item.getString("id"),
                        defaultName = name,
                        localizedNames = mapOf("en" to name),
                        category = item.getString("category"),
                        unit = item.getString("unit"),
                    ),
                )
            }
        }
    }

    private fun readAsset(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    private fun isVegDish(name: String, category: String): Boolean {
        val text = "${name.lowercase()} ${category.lowercase()}"
        return !(text.contains("chicken") || text.contains("mutton") || text.contains("fish") || text.contains("non veg"))
    }

    private fun isMainCourseDish(category: String): Boolean {
        return category !in setOf(
            "Welcome Drinks",
            "Namkeen",
            "Sweets",
            "Snacks",
            "Fruit Counter",
            "Bakery Items",
            "Soup",
            "Papad Counter",
            "Salad",
            "Chutneys",
            "Ice Cream",
            "Kulfi",
            "Water",
            "Pan Counter",
        )
    }
}

