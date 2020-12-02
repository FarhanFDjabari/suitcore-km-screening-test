package com.suitcore.feature.fragmentsample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.suitcore.base.ui.BaseFragment
import com.suitcore.databinding.FragmentTestBinding

/**
 * Created by dodydmw19 on 7/30/18.
 */

class SampleFragment : BaseFragment() {

    companion object {
        fun newInstance(): BaseFragment {
            return SampleFragment()
        }
    }

    private lateinit var sampleBinding: FragmentTestBinding
    private val actionClicked = ::dialogPositiveAction

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding {
        sampleBinding = FragmentTestBinding.inflate(inflater, container, false)
        return sampleBinding
    }

    override fun onViewReady(savedInstanceState: Bundle?) {
        actionClicked()
    }

    private fun dialogPositiveAction() {
        showToast("click")
    }

    private fun actionClicked() {
        sampleBinding.relAlertDialog.setOnClickListener {
            showAlertDialog("test")
        }

        sampleBinding.relCondirmDialog.setOnClickListener {
            showConfirmationDialog("test", actionClicked)
        }
    }

}