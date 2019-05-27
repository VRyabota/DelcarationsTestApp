package com.vrb.apps.test.data.api

import com.vrb.apps.test.data.models.Declaration
import io.reactivex.Observable

interface IApiRepository {

    fun getDeclarations(keyword: String): Observable<List<Declaration>?>
}