package com.caterer.puja.data.db

import android.content.Context
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.android.AndroidSqliteDriver

actual class DatabaseDriverFactory actual constructor(
    private val platformContext: Any?,
) {
    actual fun createDriver(
        schema: SqlSchema<QueryResult.Value<Unit>>,
        databaseName: String,
    ): SqlDriver {
        val context = platformContext as? Context
            ?: error("Android Context is required to create the database driver")
        return AndroidSqliteDriver(schema, context, databaseName)
    }
}

