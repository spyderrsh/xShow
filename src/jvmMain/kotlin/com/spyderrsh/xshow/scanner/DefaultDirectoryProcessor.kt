package com.spyderrsh.xshow.scanner

import org.jetbrains.exposed.sql.Transaction
import java.io.File

class DefaultDirectoryProcessor : DirectoryProcessor {

    override suspend fun process(transaction: Transaction, file: File) {

    }
}