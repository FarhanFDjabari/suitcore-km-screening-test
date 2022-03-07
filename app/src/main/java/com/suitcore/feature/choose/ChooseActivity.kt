package com.suitcore.feature.choose

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import com.suitcore.R
import com.suitcore.base.ui.BaseActivity
import com.suitcore.base.ui.recyclerview.BaseRecyclerView
import com.suitcore.base.ui.recyclerview.EndlessScrollCallback
import com.suitcore.data.model.ErrorCodeHelper
import com.suitcore.data.model.User
import com.suitcore.databinding.ActivityChooseBinding
import com.suitcore.feature.member.MemberAdapter
import com.suitcore.feature.member.SingleMemberItemView
import io.realm.RealmResults


class ChooseActivity : BaseActivity<ActivityChooseBinding>(), ChooseView, SingleMemberItemView.OnActionListener {

    private var choosePresenter: ChoosePresenter? = null
    private var currentPage: Int = 1
    private var memberAdapter: MemberAdapter? = null

    override fun getViewBinding(): ActivityChooseBinding = ActivityChooseBinding.inflate(layoutInflater)

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupProgressView()
        setupEmptyView()
        setupErrorView()
        setupList()
        onClickHandler()
        Handler(Looper.getMainLooper()).postDelayed({
            choosePresenter = ChoosePresenter()
            choosePresenter?.attachView(this)
            choosePresenter?.getMemberCache()
        }, 100)
    }

    private fun setupList() {
        memberAdapter = MemberAdapter(this)
        binding.rvMember.apply {
            setUpAsList()
            setAdapter(memberAdapter)
            /* Disable pull to refresh & pagination */
            setSwipeRefreshLoadingListener {
                currentPage = 1
                loadData(currentPage)
            }
            setLoadingListener(object : EndlessScrollCallback {
                override fun loadMore() {
                    currentPage++
                    loadData(currentPage)
                }
            })
        }
        memberAdapter?.setOnActionListener(this)
        binding.rvMember.showShimmer()

    }

    private fun loadData(page: Int) {
        choosePresenter?.getMemberWithCoroutines(page)
    }

    private fun setData(data: List<User>?) {
        data?.let {
            if (currentPage == 1) {
                memberAdapter.let {
                    memberAdapter?.clear()
                }
            }
            memberAdapter?.add(it)
        }
        binding.rvMember.stopShimmer()
        binding.rvMember.showRecycler()
    }

    private fun setupProgressView() {
        R.layout.layout_shimmer_member.apply {
            binding.rvMember.baseShimmerBinding.viewStub.layoutResource = this
        }

        binding.rvMember.baseShimmerBinding.viewStub.inflate()
    }

    private fun onClickHandler() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setupEmptyView() {
        binding.rvMember.setImageEmptyView(R.drawable.empty_state)
        binding.rvMember.setTitleEmptyView(getString(R.string.txt_empty_member))
        binding.rvMember.setContentEmptyView(getString(R.string.txt_empty_member_content))
        binding.rvMember.setEmptyButtonListener(object : BaseRecyclerView.ReloadListener {

            override fun onClick(v: View?) {
                loadData(1)
            }

        })
    }

    private fun setupErrorView() {
        binding.rvMember.setImageErrorView(R.drawable.empty_state)
        binding.rvMember.setTitleErrorView(getString(R.string.txt_error_no_internet))
        binding.rvMember.setContentErrorView(getString(R.string.txt_error_connection))
        binding.rvMember.setErrorButtonListener(object : BaseRecyclerView.ReloadListener {

            override fun onClick(v: View?) {
                loadData(1)
            }

        })
    }

    private fun showError() {
        finishLoad(binding.rvMember)
        binding.rvMember.showError()
    }

    private fun showEmpty() {
        finishLoad(binding.rvMember)
        binding.rvMember.showEmpty()
    }

    override fun onMemberCacheLoaded(members: RealmResults<User>?) {
        members.let {
            if (members?.isNotEmpty()!!) {
                setData(members)
            }
        }
        finishLoad(binding.rvMember)
        loadData(currentPage)
    }

    override fun onMemberLoaded(members: List<User>?) {
        members.let {
            if (members?.isNotEmpty()!!) {
                setData(members)
            }
        }
        finishLoad(binding.rvMember)
    }

    override fun onMemberEmpty() {
        showEmpty()
        binding.rvMember.setLastPage()
    }

    override fun onFailed(error: Any?) {
        error?.let { ErrorCodeHelper.getErrorMessage(this, it)?.let { msg -> showToast(msg) } }
        showError()
    }

    override fun onClicked(view: SingleMemberItemView?) {
        view?.getData()?.let {
            val resultIntent = Intent()
            resultIntent.putExtra("user_name", "${it.firstName} ${it.lastName}")
            resultIntent.putExtra("user_email", "${it.email}")
            resultIntent.putExtra("user_avatar", "${it.avatar}")
            setResult(RESULT_OK, resultIntent)
            finish()
            showToast(it.firstName.toString())
        }
    }
}