package com.vrb.apps.test.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.vrb.apps.test.Application
import com.vrb.apps.test.R
import com.vrb.apps.test.data.models.Declaration
import com.vrb.apps.test.ui.adapters.DataAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this).get(MainViewModel::class.java) }

    private val adapter by lazy {
        DataAdapter(
            this,
            mutableListOf(),
            { viewModel.onBookmarkClicked(it) },
            { openDocument(it) })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.init()

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.adapter = adapter

        subscribe()
        initDrawer()
        initToolbar()
    }

    private fun subscribe() {
        viewModel.getDeclarations().observe(this, Observer {
            Log.d(MainActivity::class.java.simpleName, "declarations observer triggered")
            adapter.setData(it.first, it.second)
        })

        viewModel.getLoadingStatus().observe(this, Observer {
            Log.d(MainActivity::class.java.simpleName, "loading status triggered with status " + it.name)
            when (it) {
                MainViewModel.Status.Loading -> {
                    displayLoading(true)
                    displayNoResult(false)
                }
                MainViewModel.Status.Loaded -> {
                    displayLoading(false)
                    displayNoResult(false)

                }
                MainViewModel.Status.Empty -> {
                    Log.d(MainActivity::class.java.simpleName, "status: no result")
                    displayLoading(false)
                    displayNoResult(true)
                }
                MainViewModel.Status.Failed -> {
                    displayLoading(false)
                    displayNoResult(true)
                    displayError()
                }
            }
        })
    }

    private fun initToolbar() {

        toolbarSearch.setOnClickListener {
            toolbar.visibility = View.GONE
            searchContainer.visibility = View.VISIBLE
            searchText.requestFocus()
        }

        closeSearch.setOnClickListener {
            toolbar.visibility = View.VISIBLE
            searchContainer.visibility = View.GONE
            hideKeyboard()
            searchText.setText("")
        }

        searchText.setOnEditorActionListener { textView, action, _ ->
            var handled = false
            if (action == EditorInfo.IME_ACTION_DONE) {
                viewModel.search(textView.text.toString())
                hideKeyboard()
                handled = true
            }
            handled
        }

        searchText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                val inputManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }
        }
    }

    private fun initDrawer() {

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        navView.setNavigationItemSelectedListener { menuItem ->
            adjustToolbar(menuItem.itemId)
            hideKeyboard()

            when (menuItem.itemId) {
                R.id.openSearch -> {
                    viewModel.resetDeclarations()
                }
                R.id.openSaved -> {
                    viewModel.loadBookmarks()
                }
            }

            drawerLayout.closeDrawers()
            menuItem.isChecked = true
            true
        }


        navView.itemIconTintList = null
        navView.setCheckedItem(R.id.openSearch)
    }

    private fun adjustToolbar(id: Int) {
        when (id) {
            R.id.openSearch -> {
                toolbar.visibility = View.VISIBLE
                searchContainer.visibility = View.GONE
                toolbarSearch.visibility = View.VISIBLE
                toolbarTitle.text = getString(R.string.toolbar_title_search)
            }

            R.id.openSaved -> {
                toolbar.visibility = View.VISIBLE
                searchText.setText("")
                searchContainer.visibility = View.GONE
                toolbarSearch.visibility = View.GONE
                toolbarTitle.text = getString(R.string.toolbar_title_bookmarks)
            }
        }
    }

    private fun displayNoResult(display: Boolean) {
        if (display)
            noResult.visibility = View.VISIBLE
        else
            noResult.visibility = View.GONE
    }

    private fun displayLoading(display: Boolean) {
        if (display)
            loader.visibility = View.VISIBLE
        else
            loader.visibility = View.GONE
    }

    private fun displayError() {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetworkInfo.isConnected) {
            Toast.makeText(this, "Виникла помилка зі сторони серверу, перевірте дані пошуку", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Немає підключення до інтернету", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDocument(linkToPdf: String) {
        val openURL = Intent(Intent.ACTION_VIEW)
        openURL.data = Uri.parse(linkToPdf)
        startActivity(openURL)
    }

    private fun hideKeyboard(){
        val inputManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = this.currentFocus
        if (view == null) {
            view = View(this)
        }
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                hideKeyboard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onBackPressed() {
        when {
            drawerLayout.isDrawerOpen(GravityCompat.START) -> drawerLayout.closeDrawers()
            else -> super.onBackPressed()
        }
    }
}
