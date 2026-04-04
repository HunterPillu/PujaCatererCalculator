package com.caterer.puja.data.seed

import com.caterer.puja.data.db.DatabaseSetup
import com.caterer.puja.data.db.entity.DishEntity
import com.caterer.puja.data.db.entity.IngredientEntity
import com.caterer.puja.resources.Res
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.ExperimentalResourceApi

object DatabaseSeeder {
    private const val DISHES_FILE = "files/seeding/dishes.json"
    private const val INGREDIENTS_VEGETABLES_FILE = "files/seeding/ingredients_vegetables.json"
    private const val INGREDIENTS_RATION_FILE = "files/seeding/ingredients_ration.json"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    suspend fun seedIfNeeded(platformContext: Any? = null) {
        val store = DatabaseSetup.createStore(platformContext)
        val hasDishes = store.dishDao().getAllDishes().isNotEmpty()
        val hasIngredients = store.ingredientDao().getAll().isNotEmpty()

        if (hasDishes && hasIngredients) return

        if (!hasDishes) {
            store.dishDao().insertDishes(parseDishes(readSeedFile(DISHES_FILE)))
        }

        if (!hasIngredients) {
            val ingredients = buildList {
                addAll(parseIngredients(readSeedFile(INGREDIENTS_VEGETABLES_FILE)))
                addAll(parseIngredients(readSeedFile(INGREDIENTS_RATION_FILE)))
            }.distinctBy { it.id }

            store.ingredientDao().insertIngredients(ingredients)
        }
    }

    private fun parseDishes(raw: String): List<DishEntity> {
        val items = json.parseToJsonElement(raw).jsonArray
        return items.mapNotNull { element ->
            val obj = element as? JsonObject ?: return@mapNotNull null
            val id = obj.string("id") ?: return@mapNotNull null
            val defaultName = obj.string("defaultName") ?: return@mapNotNull null
            val category = obj.string("category") ?: return@mapNotNull null
            val localizedNames = obj.localizedMap("localizedNames").ifEmpty { mapOf("en" to defaultName) }

            DishEntity(
                id = id,
                defaultName = defaultName,
                localizedNames = localizedNames,
                category = category,
                isVeg = obj.bool("isVeg") ?: true,
                isMainCourse = obj.bool("isMainCourse") ?: false,
            )
        }
    }

    private fun parseIngredients(raw: String): List<IngredientEntity> {
        val items: JsonArray = json.parseToJsonElement(raw).jsonArray
        return items.mapNotNull { element ->
            val obj = element as? JsonObject ?: return@mapNotNull null
            val id = obj.string("id") ?: return@mapNotNull null
            val defaultName = obj.string("defaultName") ?: obj.string("nameEn") ?: return@mapNotNull null
            val category = obj.string("category") ?: return@mapNotNull null
            val unit = obj.string("unit") ?: return@mapNotNull null

            val localizedNames = buildMap {
                obj.string("nameEn")?.let { put("en", it) }
                obj.string("nameHi")?.let { put("hi", it) }
                putAll(obj.localizedMap("localizedNames"))
                if (isEmpty()) put("en", defaultName)
            }

            IngredientEntity(
                id = id,
                defaultName = defaultName,
                localizedNames = localizedNames,
                category = category,
                unit = unit,
            )
        }
    }

    private fun JsonObject.string(key: String): String? {
        return runCatching { this[key]?.jsonPrimitive?.content }.getOrNull()
    }

    private fun JsonObject.bool(key: String): Boolean? {
        return this[key]?.jsonPrimitive?.booleanOrNull
    }

    private fun JsonObject.localizedMap(key: String): Map<String, String> {
        val map = this[key] as? JsonObject ?: return emptyMap()
        val result = mutableMapOf<String, String>()
        map.forEach { (lang, textElement) ->
            val text = runCatching { textElement.jsonPrimitive.content }.getOrNull()
            if (text != null) {
                result[lang] = text
            }
        }
        return result
    }

    @OptIn(ExperimentalResourceApi::class)
    private suspend fun readSeedFile(path: String): String {
        return Res.readBytes(path).decodeToString()
    }
}
