package com.vrb.apps.test.data

import com.vrb.apps.test.data.api.IApiRepository
import com.vrb.apps.test.data.db.DbRepository
import com.vrb.apps.test.data.db.IDbRepository
import com.vrb.apps.test.data.models.Declaration
import io.reactivex.Observable

class Repository(private val apiRepository: IApiRepository,
                 private val dbRepository: IDbRepository): IRepository {

    override fun getDeclarations(keyword: String): Observable<List<Declaration>?> {
        return apiRepository.getDeclarations(keyword)
    }

    override fun getBookmarks(): Observable<List<Declaration>> {
        return dbRepository.getBookmarks()
    }

    override fun addOrUpdateBookmark(declaration: Declaration) {
        dbRepository.addOrUpdateBookmark(declaration)
    }

    override fun removeBookmark(id: String) {
        dbRepository.removeBookmark(id)
    }


}