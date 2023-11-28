package com.spyderrsh.xshow.scanner

import org.jetbrains.exposed.sql.Transaction
import java.io.File

interface DirectoryProcessor {
    suspend fun process(transaction: Transaction, file: File)
}
