package com.vrb.apps.test.data.api

import com.vrb.apps.test.data.models.Declaration
import io.reactivex.Observable

class ApiRepository(private val api: Api): IApiRepository{

    override fun getDeclarations(keyword: String): Observable<List<Declaration>?> = api.search(keyword)
}