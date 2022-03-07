package com.suitcore.feature.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.suitcore.R
import com.suitcore.base.ui.BaseActivity
import com.suitcore.databinding.ActivityHomeBinding
import com.suitcore.feature.choose.ChooseActivity
import com.suitcore.feature.login.LoginActivity
import com.suitcore.firebase.analytics.FireBaseConstant
import com.suitcore.firebase.analytics.FireBaseHelper
import okhttp3.internal.wait

class HomeActivity : BaseActivity<ActivityHomeBinding>(), HomeView {

    private var homePresenter: HomePresenter? = null


    override fun getViewBinding(): ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupPresenter()
        setupData()
        clickHandler()
    }

    private fun setupPresenter() {
        homePresenter = HomePresenter()
        homePresenter?.attachView(this)
    }

    override fun onResume() {
        super.onResume()
        sendAnalytics()
    }

    private fun setupData() {
        binding.selectedMemberImg.setImageResource(R.drawable.empty_state)
        binding.username.text = "Member not yet selected"
        binding.userEmail.text = "Choose member by clicking the button below"
    }

    private fun clickHandler() {
        binding.chooseMemberBtn.setOnClickListener {
            goToActivity(1, ChooseActivity::class.java, null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

                binding.selectedMemberImg.setImageURI(data?.getStringExtra("user_avatar"))

                binding.username.text = data?.getStringExtra("user_name")
                binding.userEmail.text = data?.getStringExtra("user_email")

        }
    }

    private fun sendAnalytics() {
        FireBaseHelper.instance().getFireBaseAnalytics()?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, FireBaseConstant.SCREEN_LOGIN)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, LoginActivity::class.java.simpleName)
        }
    }
}