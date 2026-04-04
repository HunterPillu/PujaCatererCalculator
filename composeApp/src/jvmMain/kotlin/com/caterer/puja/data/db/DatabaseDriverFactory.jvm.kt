package com.caterer.puja.data.db

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver

actual class DatabaseDriverFactory actual constructor(
    platformContext: Any?,
) {
    actual fun createDriver(
        schema: SqlSchema<QueryResult.Value<Unit>>,
        databaseName: String,
    ): SqlDriver {
        val driver = JdbcSqliteDriver(url = "jdbc:sqlite:$databaseName")
        schema.create(driver).value
        return driver
    }
}

