package com.vrb.apps.test.dagger

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.vrb.apps.test.data.IRepository
import com.vrb.apps.test.data.Repository
import com.vrb.apps.test.data.api.Api
import com.vrb.apps.test.data.api.ApiRepository
import com.vrb.apps.test.data.api.EnvelopeConverter
import com.vrb.apps.test.data.api.IApiRepository
import com.vrb.apps.test.data.db.DbRepository
import com.vrb.apps.test.data.db.IDbRepository
import dagger.Module
import dagger.Provides
import io.realm.Realm
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val context: Context) {

    private val CONNECT_TIMEOUT = 30L
    private val READ_TIMEOUT = 30L
    private val BASE_URL = "https://public-api.nazk.gov.ua/v1/declaration/"

    @Provides
    @Singleton
    fun provideClient() = provideOkHttpClient()

    @Provides
    @Singleton
    fun provideApi(client: OkHttpClient) = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(EnvelopeConverter())
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(client)
        .build()
        .create(Api::class.java)

    @Provides
    @Singleton
    fun provideRealmInstance(): Realm = Realm.getDefaultInstance()

    @Provides
    @Singleton
    fun provideApiRepository(api: Api): IApiRepository = ApiRepository(api)

    @Provides
    @Singleton
    fun provideDbRepository(realm: Realm): IDbRepository = DbRepository(realm)

    @Provides
    @Singleton
    fun provideRepository(apiRepository: IApiRepository, dbRepository: IDbRepository): IRepository = Repository(apiRepository, dbRepository)

    @Provides
    @Singleton
    fun provideContext() = context

    private fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addNetworkInterceptor(StethoInterceptor())
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
}