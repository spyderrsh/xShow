package com.spyderrsh.xshow.db.dao

import com.spyderrsh.xshow.db.table.ImageTable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class ImageDao(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ImageDao>(ImageTable)

    var path by ImageTable.path
    var shortName by ImageTable.shortName
    var extension by ImageTable.extension
    var serverPath by ImageTable.serverPath
}