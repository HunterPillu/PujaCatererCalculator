package com.caterer.puja.data.db

import com.caterer.puja.data.db.dao.DishDao
import com.caterer.puja.data.db.dao.EventDao
import com.caterer.puja.data.db.dao.IngredientDao
import com.caterer.puja.data.db.dao.MappingDao
import com.caterer.puja.data.db.dao.SqlDelightDishDao
import com.caterer.puja.data.db.dao.SqlDelightEventDao
import com.caterer.puja.data.db.dao.SqlDelightIngredientDao
import com.caterer.puja.data.db.dao.SqlDelightMappingDao
import com.caterer.puja.db.AppDatabase

class AppDatabaseStore(
    private val database: AppDatabase,
) {
    fun dishDao(): DishDao = SqlDelightDishDao(database)

    fun ingredientDao(): IngredientDao = SqlDelightIngredientDao(database)

    fun mappingDao(): MappingDao = SqlDelightMappingDao(database)

    fun eventDao(): EventDao = SqlDelightEventDao(database)
}

