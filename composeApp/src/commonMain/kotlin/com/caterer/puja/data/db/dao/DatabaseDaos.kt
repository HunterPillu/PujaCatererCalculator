package com.caterer.puja.data.db.dao

import com.caterer.puja.data.db.entity.DishEntity
import com.caterer.puja.data.db.entity.DishIngredientMapEntity
import com.caterer.puja.data.db.entity.EventDishCrossRef
import com.caterer.puja.data.db.entity.EventEntity
import com.caterer.puja.data.db.entity.IngredientEntity
import com.caterer.puja.db.AppDatabase

interface DishDao {
    suspend fun insertDishes(dishes: List<DishEntity>)
    suspend fun getAllDishes(): List<DishEntity>
}

interface IngredientDao {
    suspend fun insertIngredients(list: List<IngredientEntity>)
    suspend fun getAll(): List<IngredientEntity>
}

interface MappingDao {
    suspend fun insertMappings(list: List<DishIngredientMapEntity>)
    suspend fun getByDish(dishId: String): List<DishIngredientMapEntity>
}

interface EventDao {
    suspend fun insert(event: EventEntity): Long
    suspend fun insertEventDishes(list: List<EventDishCrossRef>)
}

class SqlDelightDishDao(
    private val database: AppDatabase,
) : DishDao {
    override suspend fun insertDishes(dishes: List<DishEntity>) {
        database.transaction {
            dishes.forEach { dish ->
                database.appDatabaseQueries.insertDish(
                    id = dish.id,
                    defaultName = dish.defaultName,
                    category = dish.category,
                    isVeg = if (dish.isVeg) 1 else 0,
                    isMainCourse = if (dish.isMainCourse) 1 else 0,
                )
                database.appDatabaseQueries.deleteDishLocalizedNamesByDishId(dish.id)
                dish.localizedNames.forEach { (languageCode, displayName) ->
                    database.appDatabaseQueries.insertDishLocalizedName(
                        dishId = dish.id,
                        languageCode = languageCode,
                        displayName = displayName,
                    )
                }
            }
        }
    }

    override suspend fun getAllDishes(): List<DishEntity> {
        return database.appDatabaseQueries.selectAllDishes().executeAsList().map { row ->
            val localizedNames = database.appDatabaseQueries
                .selectDishLocalizedNamesByDishId(row.id)
                .executeAsList()
                .associate { it.languageCode to it.displayName }

            DishEntity(
                id = row.id,
                defaultName = row.defaultName,
                localizedNames = localizedNames,
                category = row.category,
                isVeg = row.isVeg == 1L,
                isMainCourse = row.isMainCourse == 1L,
            )
        }
    }
}

class SqlDelightIngredientDao(
    private val database: AppDatabase,
) : IngredientDao {
    override suspend fun insertIngredients(list: List<IngredientEntity>) {
        database.transaction {
            list.forEach { ingredient ->
                database.appDatabaseQueries.insertIngredient(
                    id = ingredient.id,
                    defaultName = ingredient.defaultName,
                    category = ingredient.category,
                    unit = ingredient.unit,
                )
                database.appDatabaseQueries.deleteIngredientLocalizedNamesByIngredientId(ingredient.id)
                ingredient.localizedNames.forEach { (languageCode, displayName) ->
                    database.appDatabaseQueries.insertIngredientLocalizedName(
                        ingredientId = ingredient.id,
                        languageCode = languageCode,
                        displayName = displayName,
                    )
                }
            }
        }
    }

    override suspend fun getAll(): List<IngredientEntity> {
        return database.appDatabaseQueries.selectAllIngredients().executeAsList().map { row ->
            val localizedNames = database.appDatabaseQueries
                .selectIngredientLocalizedNamesByIngredientId(row.id)
                .executeAsList()
                .associate { it.languageCode to it.displayName }

            IngredientEntity(
                id = row.id,
                defaultName = row.defaultName,
                localizedNames = localizedNames,
                category = row.category,
                unit = row.unit,
            )
        }
    }
}

class SqlDelightMappingDao(
    private val database: AppDatabase,
) : MappingDao {
    override suspend fun insertMappings(list: List<DishIngredientMapEntity>) {
        database.transaction {
            list.forEach { mapping ->
                database.appDatabaseQueries.insertDishIngredientMapping(
                    dishId = mapping.dishId,
                    ingredientId = mapping.ingredientId,
                    quantityPerPerson = mapping.quantityPerPerson,
                    unit = mapping.unit,
                    consumptionRate = mapping.consumptionRate,
                )
            }
        }
    }

    override suspend fun getByDish(dishId: String): List<DishIngredientMapEntity> {
        return database.appDatabaseQueries.selectMappingsByDishId(dishId).executeAsList().map { row ->
            DishIngredientMapEntity(
                dishId = row.dishId,
                ingredientId = row.ingredientId,
                quantityPerPerson = row.quantityPerPerson,
                unit = row.unit,
                consumptionRate = row.consumptionRate,
            )
        }
    }
}

class SqlDelightEventDao(
    private val database: AppDatabase,
) : EventDao {
    override suspend fun insert(event: EventEntity): Long {
        database.appDatabaseQueries.insertEvent(
            name = event.name,
            peopleCount = event.peopleCount.toLong(),
            isBuffet = if (event.isBuffet) 1 else 0,
            eventType = event.eventType,
            bufferPercent = event.bufferPercent,
        )
        return database.appDatabaseQueries.selectLastInsertedEventId().executeAsOne()
    }

    override suspend fun insertEventDishes(list: List<EventDishCrossRef>) {
        database.transaction {
            list.forEach { link ->
                database.appDatabaseQueries.insertEventDish(
                    eventId = link.eventId,
                    dishId = link.dishId,
                )
            }
        }
    }
}


