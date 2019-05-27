package com.vrb.apps.test.data.api

import com.vrb.apps.test.data.models.Declaration
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface Api {

    @GET(".")
    fun search(@Query("q") keyword: String): Observable<List<Declaration>?>
}