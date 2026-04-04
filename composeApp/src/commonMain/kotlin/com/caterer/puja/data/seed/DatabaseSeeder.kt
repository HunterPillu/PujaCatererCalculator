package com.caterer.puja.data.seed

import com.caterer.puja.data.db.AppDatabaseStore
import com.caterer.puja.data.db.DatabaseSetup
import com.caterer.puja.data.db.entity.DishEntity
import com.caterer.puja.data.db.entity.DishIngredientMapEntity
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
        val hasMappings = store.mappingDao().getAllMappings().isNotEmpty()

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

        if (!hasMappings) {
            seedLegacyMappings(store)
        }
    }

    // Build a practical default mapping set from seeded DB dishes + seeded DB ingredients.
    private suspend fun seedLegacyMappings(store: AppDatabaseStore) {
        val dishes = store.dishDao().getAllDishes()
        val ingredientsById = store.ingredientDao().getAll().associateBy { it.id }
        val mappings = mutableListOf<DishIngredientMapEntity>()

        dishes.forEach { dish ->
            mappings += defaultMappingsForDish(dish, ingredientsById)
        }

        if (mappings.isNotEmpty()) {
            store.mappingDao().insertMappings(
                mappings
                    .distinctBy { it.dishId to it.ingredientId }
                    .filter { it.quantityPerPerson > 0.0 },
            )
        }
    }

    private fun defaultMappingsForDish(
        dish: DishEntity,
        ingredientsById: Map<String, IngredientEntity>,
    ): List<DishIngredientMapEntity> {
        val result = mutableListOf<DishIngredientMapEntity>()
        val name = dish.defaultName.lowercase()
        val category = dish.category.lowercase()

        fun addIfPresent(ingredientId: String, qty: Double, unit: String, rate: Double = 1.0) {
            if (ingredientsById.containsKey(ingredientId)) {
                result += DishIngredientMapEntity(
                    dishId = dish.id,
                    ingredientId = ingredientId,
                    quantityPerPerson = qty,
                    unit = unit,
                    consumptionRate = rate,
                )
            }
        }

        val isRiceDish = category.contains("rice") || name.contains("biryani") || name.contains("pulao")
        val isBreadDish = category.contains("bread") || name.contains("roti") || name.contains("paratha") || name.contains("kulcha")
        val isDalDish = category.contains("daal") || name.contains("dal")
        val isDrinkDish = category.contains("drink") || name.contains("shake") || name.contains("juice") || name.contains("coffee")
        val isSweetDish = category.contains("sweet") || name.contains("halwa") || name.contains("jalebi") || name.contains("rabri")

        if (isRiceDish) {
            addIfPresent("ING_INDIAGATE_RICE", 90.0, "g")
            addIfPresent("ING_REFINED_OIL", 5.0, "ml")
            addIfPresent("ING_TATA_NAMAK", 1.0, "g")
            addIfPresent("ING_GOTA_JEERA", 1.0, "g", rate = if (name.contains("jeera")) 1.0 else 0.4)
        } else if (isBreadDish) {
            addIfPresent("ING_ATTA_SHAKTIBHOG", 80.0, "g")
            addIfPresent("ING_REFINED_OIL", 3.0, "ml")
            addIfPresent("ING_TATA_NAMAK", 1.0, "g")
        } else if (isDalDish) {
            addIfPresent("ING_KALA_URAD", 45.0, "g")
            addIfPresent("ING_ONION", 20.0, "g")
            addIfPresent("ING_TOMATO", 25.0, "g")
            addIfPresent("ING_REFINED_OIL", 8.0, "ml")
        } else if (isDrinkDish) {
            addIfPresent("ING_MILK", 140.0, "ml")
            addIfPresent("ING_SUGAR", 10.0, "g")
            addIfPresent("ING_ICE", 40.0, "g")
        } else if (isSweetDish) {
            addIfPresent("ING_SUGAR", 18.0, "g")
            addIfPresent("ING_KHOA", 25.0, "g")
            addIfPresent("ING_DESI_GHEE", 5.0, "g")
        } else {
            addIfPresent("ING_ONION", 25.0, "g")
            addIfPresent("ING_TOMATO", 20.0, "g")
            addIfPresent("ING_REFINED_OIL", 8.0, "ml")
            addIfPresent("ING_TATA_NAMAK", 1.0, "g")
        }

        if (result.isEmpty()) {
            addIfPresent("ING_REFINED_OIL", 5.0, "ml")
        }

        return result
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
