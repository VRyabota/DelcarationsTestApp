package com.vrb.apps.test.data.db

import com.vrb.apps.test.data.models.Declaration
import io.reactivex.Observable

interface IDbRepository {

    fun getBookmarks(): Observable<List<Declaration>>

    fun addOrUpdateBookmark(declaration: Declaration)

    fun removeBookmark(id: String)
}