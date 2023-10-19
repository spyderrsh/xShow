package com.spyderrsh.xshow

import com.spyderrsh.xshow.AppModule.NAMED_APP_STATE
import com.spyderrsh.xshow.model.FileModel
import io.kvision.redux.createTypedReduxStore
import org.koin.core.KoinApplication
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

object AppModule {
    val NAMED_APP_STATE = named("AppState")
}

fun appModule(rootFolder: FileModel.Folder) = module {
    single(NAMED_APP_STATE) {
        createTypedReduxStore(::appReducer, AppState.FileBrowser(rootFolder) )
    }
    singleOf(::AppViewModel)
}