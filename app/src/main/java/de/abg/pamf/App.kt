package de.abg.pamf

import android.app.Application
import de.abg.pamf.remote.FakeReceiver
import de.abg.pamf.ui.centergravity.CogData

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        CogData.init(this)
//        FakeReceiver.init()
    }

}