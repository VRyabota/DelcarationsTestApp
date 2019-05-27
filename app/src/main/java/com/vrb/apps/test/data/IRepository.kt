package com.vrb.apps.test.data

import com.vrb.apps.test.data.db.DbRepository
import com.vrb.apps.test.data.models.Declaration
import io.reactivex.Observable

interface IRepository {

    fun getDeclarations(keyword: String): Observable<List<Declaration>?>

    fun getBookmarks(): Observable<List<Declaration>>

    fun addOrUpdateBookmark(declaration: Declaration)

    fun removeBookmark(id: String)
}