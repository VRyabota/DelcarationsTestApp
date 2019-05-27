package com.vrb.apps.test.dagger

import com.vrb.apps.test.Application
import com.vrb.apps.test.ui.MainViewModel
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(mainViewModel: MainViewModel)
    fun inject(app: Application)
}