package com.vrb.apps.test.data.db

import com.vrb.apps.test.data.models.Declaration
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm

class DbRepository(private val realm: Realm) : IDbRepository {

    override fun getBookmarks(): Observable<List<Declaration>> {
        return Observable.just(realm.where(Declaration::class.java).findAll() as List<Declaration>)
            .subscribeOn(AndroidSchedulers.mainThread())
    }

    override fun addOrUpdateBookmark(declaration: Declaration) {
        realm.executeTransaction {
            it.copyToRealmOrUpdate(declaration)
        }
    }

    override fun removeBookmark(id: String) {
        realm.executeTransaction {
            it.where(Declaration::class.java).equalTo("id", id).findAll().deleteAllFromRealm()
        }
    }
}