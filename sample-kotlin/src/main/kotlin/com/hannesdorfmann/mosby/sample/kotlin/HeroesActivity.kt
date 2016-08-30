package com.hannesdorfmann.mosby.sample.kotlin

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateActivity
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState
import com.hannesdorfmann.mosby.sample.kotlin.model.Hero

class HeroesActivity : HeroesView, MvpLceViewStateActivity<SwipeRefreshLayout, List<Hero>, HeroesView, HeroesPresenter>(), SwipeRefreshLayout.OnRefreshListener {

    var adapter: HeroesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_heroes)
        retainInstance = true
        contentView.setOnRefreshListener(this)

        val recyclerView = findViewById(R.id.recyclerView) as RecyclerView

        adapter = HeroesAdapter(this, LayoutInflater.from(this))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(this, 2)
    }

    override fun getErrorMessage(e: Throwable?, pullToRefresh: Boolean): String? {
        if (pullToRefresh) return "Error while loading"
        else return "Error while loading. Click here to retry."
    }

    override fun createPresenter(): HeroesPresenter {
        return HeroesPresenter()
    }

    override fun createViewState(): LceViewState<List<Hero>, HeroesView> {
        return RetainingLceViewState()
    }

    override fun getData(): List<Hero>? {
        return adapter?.items
    }

    override fun setData(data: List<Hero>?) {
        adapter?.items = data
        adapter?.notifyDataSetChanged()
    }

    override fun loadData(pullToRefresh: Boolean) {
        presenter.loadHeroes(pullToRefresh)
    }

    override fun onRefresh() {
        loadData(true)
    }

    override fun showContent() {
        super.showContent()
        contentView.isRefreshing = false
    }

    override fun showError(t: Throwable, pullToRefresh: Boolean) {
        super.showError(t, pullToRefresh)
        contentView.isRefreshing = false
    }

    override fun showLoading(pullToRefresh: Boolean) {
        super.showLoading(pullToRefresh)
        if (pullToRefresh && !contentView.isRefreshing) {
            contentView.post(RefreshRunnable(contentView))
        }
    }

    // Don't do that in production, could lead to memory leaks
    class RefreshRunnable(val swipeRefreshLayout: SwipeRefreshLayout) : Runnable {

        override fun run() {
            swipeRefreshLayout.isRefreshing = true
        }
    }

}
