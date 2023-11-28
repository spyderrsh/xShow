package com.spyderrsh.xshow.db

import com.spyderrsh.xshow.ServerConfig
import com.spyderrsh.xshow.db.table.ImageTable
import com.spyderrsh.xshow.db.table.VideoTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.sql.Connection
import java.util.logging.Logger

class DatabaseInitializer(
    private val config: ServerConfig
) {
    fun initialize() {
        val dbFile = File(config.dbPath)
        if (!dbFile.exists()) {
            Logger.getGlobal().info("db doesn't exist. Creating new one at ${dbFile.absolutePath}")
            dbFile.parentFile.mkdirs()
            dbFile.createNewFile()
            Logger.getGlobal().info("db created!")
        }
        Logger.getGlobal().info("Connecting to database...")
        Database.connect("jdbc:sqlite:${config.dbPath}", "org.sqlite.JDBC")
        Logger.getGlobal().info("Success!!!")

        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(VideoTable, ImageTable)
        }
    }
}