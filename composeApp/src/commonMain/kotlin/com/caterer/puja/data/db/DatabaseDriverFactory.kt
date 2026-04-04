package com.caterer.puja.data.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

expect class DatabaseDriverFactory(platformContext: Any? = null) {
    fun createDriver(
        schema: SqlSchema<QueryResult.Value<Unit>>,
        databaseName: String = DatabaseSetup.DATABASE_NAME,
    ): SqlDriver
}

