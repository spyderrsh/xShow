package com.spyderrsh.xshow

import io.ktor.server.application.*
import io.ktor.server.websocket.*
import org.koin.dsl.module

internal object KoinModule {
    internal val threadLocalApplicationCall = ThreadLocal<ApplicationCall>()
    internal val threadLocalWebSocketServerSession = ThreadLocal<WebSocketServerSession>()

    internal fun applicationModule(app: Application) = module {
        single { app }
        factory<ApplicationCall> {
            threadLocalApplicationCall.get()
        }
        factory<WebSocketServerSession> {
            threadLocalWebSocketServerSession.get()
        }
    }
}
