package com.caterer.puja.data.db

import app.cash.sqldelight.db.SqlDriver
import com.caterer.puja.db.AppDatabase

object DatabaseSetup {
    const val DATABASE_NAME: String = "puja_caterer.db"

    fun createDatabase(driver: SqlDriver): AppDatabase {
        return AppDatabase(driver)
    }
}

