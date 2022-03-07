package com.suitcore.feature.choose

import com.suitcore.base.presenter.MvpView
import com.suitcore.data.model.User
import io.realm.RealmResults

interface ChooseView : MvpView {
    fun onMemberCacheLoaded(members: RealmResults<User>?)

    fun onMemberLoaded(members: List<User>?)

    fun onMemberEmpty()

    fun onFailed(error: Any?)
}