package com.vrb.apps.test.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vrb.apps.test.Application
import com.vrb.apps.test.data.IRepository
import com.vrb.apps.test.data.db.DbRepository
import com.vrb.apps.test.data.models.Declaration
import com.vrb.apps.test.ui.adapters.DataAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel : ViewModel() {

    @Inject
    lateinit var repository: IRepository

    private val disposeBag: CompositeDisposable = CompositeDisposable()

    private val loadingStatus: MutableLiveData<Status> = MutableLiveData()
    private val declarationsData: MutableLiveData<Pair<DataAdapter.ListType, List<Declaration>>> = MutableLiveData()

    enum class Status {
        Loading,
        Loaded,
        Empty,
        Failed
    }

    fun init() {
        Application.component.inject(this)
    }

    fun search(keyword: String) {

        disposeBag.add(Observable.combineLatest<List<Declaration>, List<Declaration>, Pair<List<Declaration>, List<Declaration>>>(
            repository.getDeclarations(keyword),
            repository.getBookmarks(),
            BiFunction { result, bookmarks ->
                result to bookmarks
            })
            .subscribeOn(Schedulers.io())
            .map {
                Log.d("ApiRequest", "Api data: " + it.first.toString())
                Log.d("ApiRequest", "Bookmarks data: " + it.second.toString())
                it.first.forEach { declaration ->
                    it.second.forEach { bookmark ->
                        if (declaration.id == bookmark.id){
                            Log.d("ApiRequest/Bookmarks", "value marked as bookmarked")
                            declaration.isBookmarked = true
                        }
                    }
                }
                return@map it.first
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveDeclarationsStart() }
            .subscribe(
                { result ->
                    if (result.isEmpty()) {
                        Log.d("ApiRequest", "Result is empty")
                        onRetrieveDeclarationsEmpty()
                    } else {
                        Log.d("ApiRequest", "Successful result")
                        onRetrieveDeclarationsSuccess(DataAdapter.ListType.Search, result)
                        onRetrieveDeclarationsFinish()
                    }
                },
                {
                    Log.d("ApiRequest", "Error while retrieving data")
                    Log.d("ApiRequest", "Error: " + it.localizedMessage)
                    onRetrieveDeclarationsError()
                }
            ))
    }

    fun loadBookmarks() {
        disposeBag.add(repository.getBookmarks()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { onRetrieveDeclarationsStart() }
            .subscribe(
                { result ->
                    if (result.isEmpty()) {
                        Log.d("DbRequest", "Result is empty")
                        onRetrieveDeclarationsEmpty()
                    } else {
                        Log.d("DbRequest", "Successful result")
                        Log.d("DbRequest", "Bookmarks data: $result")
                        onRetrieveDeclarationsSuccess(DataAdapter.ListType.Bookmark, result)
                        onRetrieveDeclarationsFinish()
                    }
                },
                {
                    Log.d("DbRequest", "Error while retrieving data")
                    Log.d("DbRequest", "Error: " + it.localizedMessage)
                    onRetrieveDeclarationsError() }
            ))
    }

    fun getDeclarations(): MutableLiveData<Pair<DataAdapter.ListType, List<Declaration>>> = declarationsData

    fun getLoadingStatus(): MutableLiveData<Status> = loadingStatus

    fun resetDeclarations() {
        declarationsData.postValue(Pair(DataAdapter.ListType.Search, emptyList()))
    }

    private fun onRetrieveDeclarationsStart() {
        loadingStatus.postValue(Status.Loading)
    }

    private fun onRetrieveDeclarationsFinish() {
        loadingStatus.postValue(Status.Loaded)
    }

    private fun onRetrieveDeclarationsEmpty() {
        loadingStatus.postValue(Status.Empty)
    }

    private fun onRetrieveDeclarationsSuccess(type: DataAdapter.ListType, result: List<Declaration>) {
        declarationsData.postValue(Pair(type, result))
    }

    private fun onRetrieveDeclarationsError() {
        loadingStatus.postValue(Status.Failed)
    }

    override fun onCleared() {
        disposeBag.dispose()
        super.onCleared()
    }

    fun onBookmarkClicked(data: Pair<Declaration, Boolean>) {
        val isRemoved = data.second
        if (isRemoved) {
            repository.removeBookmark(data.first.id)
        } else {
            repository.addOrUpdateBookmark(data.first)
        }
    }
}