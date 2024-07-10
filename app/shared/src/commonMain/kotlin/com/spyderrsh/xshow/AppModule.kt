package com.spyderrsh.xshow

//import com.spyderrsh.xshow.AppModule.NAMED_APP_STATE
//import com.spyderrsh.xshow.filesystem.fileSystemBrowserModule
//import com.spyderrsh.xshow.slideshow.slideShowModule
//import io.kvision.redux.createTypedReduxStore
//import org.koin.core.qualifier.named
//import org.koin.dsl.module
//
//object AppModule {
//    val NAMED_APP_STATE = named("AppState")
//}
//
//fun appModule() = module {
//    single(NAMED_APP_STATE) {
//        createTypedReduxStore(::appReducer, AppState() )
//    }
//    single { AppViewModel(get(qualifier = NAMED_APP_STATE)) }
//    fileSystemBrowserModule()
//    slideShowModule()
//}
