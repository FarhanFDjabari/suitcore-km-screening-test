package com.suitcore.feature.home

import com.suitcore.BaseApplication
import com.suitcore.base.presenter.BasePresenter

class HomePresenter : BasePresenter<HomeView> {

    private var mvpView: HomeView? = null

    init {
        BaseApplication.applicationComponent.inject(this)
    }

    override fun onDestroy() {
    }

    override fun attachView(view: HomeView) {
        mvpView = view
    }

    override fun detachView() {
        mvpView = null
    }
}