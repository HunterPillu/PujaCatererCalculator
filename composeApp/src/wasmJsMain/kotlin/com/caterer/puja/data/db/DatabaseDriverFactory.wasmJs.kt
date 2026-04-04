package com.caterer.puja.data.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

actual class DatabaseDriverFactory actual constructor(
    platformContext: Any?,
) {
    actual fun createDriver(
        schema: SqlSchema<QueryResult.Value<Unit>>,
        databaseName: String,
    ): SqlDriver {
        error("SQL driver wiring for Wasm will be added with the final schema")
    }
}

