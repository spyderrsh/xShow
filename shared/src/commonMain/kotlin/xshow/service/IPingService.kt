package com.spyderrsh.xshow.service

interface IPingService {
    suspend fun ping(message: String): String
}
