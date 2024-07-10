package com.spyderrsh.xshow.db.dao

import com.spyderrsh.xshow.db.table.VideoTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class VideoDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<VideoDao>(VideoTable)

    var path by VideoTable.path
    var shortName by VideoTable.shortName
    var extension by VideoTable.extension
    var duration by VideoTable.duration
    var serverPath by VideoTable.serverPath
}