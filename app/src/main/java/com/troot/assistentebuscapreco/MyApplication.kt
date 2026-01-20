package com.troot.assistentebuscapreco

import android.app.Application
import com.troot.assistentebuscapreco.utils.ThemeHelper

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Aplica o tema salvo ao iniciar o app
        ThemeHelper.applyTheme(this)
    }
}
