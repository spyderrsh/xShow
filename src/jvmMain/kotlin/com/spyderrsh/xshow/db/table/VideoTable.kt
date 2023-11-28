package com.spyderrsh.xshow.db.table

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.kotlin.datetime.duration

object VideoTable: IntIdTable() {
    const val MAX_PATH_LENGTH = 300
    val path = char("path", MAX_PATH_LENGTH).uniqueIndex()
    val shortName = varchar("shortName", 255)
    val extension = varchar("extension", 10)
    val duration = duration("duration")
    val serverPath = text("serverPath")
}