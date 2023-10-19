package com.spyderrsh.xshow

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AppComponent: KoinComponent {
    val appViewModel: AppViewModel by inject()
}