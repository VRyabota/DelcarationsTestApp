package com.vrb.apps.test

import android.app.Application
import com.vrb.apps.test.dagger.AppComponent
import com.vrb.apps.test.dagger.AppModule
import com.vrb.apps.test.dagger.DaggerAppComponent
import com.vrb.apps.test.data.Repository
import com.vrb.apps.test.data.api.IApiRepository
import com.vrb.apps.test.data.db.IDbRepository
import io.realm.Realm
import javax.inject.Inject

class Application: Application() {

    companion object{
        lateinit var component: AppComponent
    }

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)

        component = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}