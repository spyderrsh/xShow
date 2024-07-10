package com.spyderrsh.xshow

import com.spyderrsh.xshow.service.IPingService

class PingService : IPingService {

    override suspend fun ping(message: String): String {
        println(message)
        return "Hello world from server!"
    }
}
