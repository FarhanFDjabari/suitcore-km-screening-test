package com.suitcore.feature.login

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.install.model.ActivityResult
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.suitcore.R
import com.suitcore.base.ui.BaseActivity
import com.suitcore.data.model.UpdateType
import com.suitcore.databinding.ActivityLoginBinding
import com.suitcore.feature.home.HomeActivity
import com.suitcore.firebase.analytics.FireBaseConstant
import com.suitcore.firebase.analytics.FireBaseHelper
import com.suitcore.firebase.remoteconfig.RemoteConfigHelper
import com.suitcore.firebase.remoteconfig.RemoteConfigPresenter
import com.suitcore.firebase.remoteconfig.RemoteConfigView
import com.suitcore.helper.CommonConstant
import com.suitcore.helper.CommonUtils
import com.suitcore.helper.inappupdates.InAppUpdateManager
import com.suitcore.helper.inappupdates.InAppUpdateStatus
import com.suitcore.helper.permission.SuitPermissions
import timber.log.Timber

/**
 * Created by dodydmw19 on 7/18/18.
 */

class LoginActivity : BaseActivity<ActivityLoginBinding>(), LoginView, RemoteConfigView,
        InAppUpdateManager.InAppUpdateHandler {

    private var loginPresenter: LoginPresenter? = null
    private var remoteConfigPresenter: RemoteConfigPresenter? = null
    private var inAppUpdateManager: InAppUpdateManager? = null

    override fun getViewBinding(): ActivityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)

    override fun onViewReady(savedInstanceState: Bundle?) {
        setupPresenter()
        actionClicked()
        needPermissions()
        CommonUtils.getIMEIDeviceId(this)
    }

    override fun onResume() {
        super.onResume()
        sendAnalytics()
        //remoteConfigPresenter?.checkBaseUrl()
        remoteConfigPresenter?.getUpdateType(this)
    }

    private fun sendAnalytics() {
        FireBaseHelper.instance().getFireBaseAnalytics()?.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, FireBaseConstant.SCREEN_LOGIN)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, LoginActivity::class.java.simpleName)
        }
    }

    private fun setupPresenter() {
        loginPresenter = LoginPresenter()
        loginPresenter?.attachView(this)

        remoteConfigPresenter = RemoteConfigPresenter()
        remoteConfigPresenter?.attachView(this)
    }

    @SuppressLint("TimberArgCount")
    private fun needPermissions() {
        SuitPermissions.with(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE)
                .onAccepted {
                    for (s in it) {
                        Timber.d("granted_permission", s)
                    }
                    showToast("Granted")
                }
                .onDenied {
                    showToast("Denied")
                }
                .onForeverDenied {
                    showToast("Forever denied")
                }
                .ask()
    }

    private fun setupInAppUpdate(mode: CommonConstant.UpdateMode) {
        inAppUpdateManager = InAppUpdateManager.builder(this, 100)
                ?.resumeUpdates(true) // Resume the update, if the update was stalled. Default is true
                ?.mode(mode)
                ?.snackBarMessage(getString(R.string.txt_update_completed))
                ?.snackBarAction(getString(R.string.txt_button_restart))
                ?.handler(this)

        inAppUpdateManager?.checkForAppUpdate()
    }

    override fun onLoginSuccess(message: String?) {
        goToActivity(HomeActivity::class.java, null, clearIntent = true, isFinish = true)
        showToast("$message")
    }

    override fun onLoginFailed(message: String?) {
        message?.let {
            showToast(message.toString())
        }
    }

    override fun onPalindrome(message: String?) {
        showDialogAlert(message,"")
    }

    override fun onUpdateBaseUrlNeeded(type: String?, url: String?) {
        RemoteConfigHelper.changeBaseUrl(this, type.toString(), url.toString())
    }

    @SuppressLint("TimberArgCount")
    override fun onUpdateTypeReceive(update: UpdateType?) {
        update?.let {
            when (it.updateType) {
                CommonConstant.INAPPUPDATE -> {
                    setupInAppUpdate(CommonUtils.convertData(update.category))
                }
                CommonConstant.REMOTECONFIG -> {
                    if (CommonUtils.isUpdateAvailable(update.latestVersionCode)) {
                        val message: String = update.messages ?: "Update Available"
                        if (update.category == CommonConstant.IMMEDIATE) {
                            showDialogAlert(title = null, message = message, confirmCallback = {
                                CommonUtils.openAppInStore(this)
                            })
                        } else {
                            showDialogConfirmation(title = null, message, confirmCallback = {
                                CommonUtils.openAppInStore(this)
                            })
                        }
                    }
                }
            }
        }
    }

    override fun onInAppUpdateError(code: Int, error: Throwable?) {
        Timber.d(error, error?.message.toString())
    }

    override fun onInAppUpdateStatus(status: InAppUpdateStatus?) {
        if (status?.isDownloaded == true) {
            val rootView: View = window.decorView.findViewById(R.id.content)
            val snackBar = Snackbar.make(rootView,
                    "An update has just been downloaded.",
                    Snackbar.LENGTH_INDEFINITE)
            snackBar.setAction("RESTART") {

                // Triggers the completion of the update of the app for the flexible flow.
                inAppUpdateManager?.completeUpdate()
            }
            snackBar.show()
        }
    }

    private fun actionClicked() {
        binding.btnCheckPalindrome.setOnClickListener {
            if (!binding.inputPalindrome.text.isNullOrEmpty()) {
                loginPresenter?.checkPalindrome(binding.inputPalindrome.text.toString())
            } else {
                binding.inputPalindrome.error = "This field must not empty";
            }
        }

        binding.btnLogin.setOnClickListener {
            if (binding.inputEmail.text.isNullOrEmpty()) {
                binding.inputEmail.error = "Email field must not empty"
            }
            if (binding.inputPassword.text.isNullOrEmpty()) {
                binding.inputPassword.error = "Password field must not empty"
            }
            if (!binding.inputEmail.text.isNullOrEmpty() && !binding.inputPassword.text.isNullOrEmpty()) {
                loginPresenter?.login(
                    binding.inputEmail.text.toString(),
                    binding.inputPassword.text.toString()
                )
            }
        }

    }

    @SuppressLint("TimberArgCount")
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    Timber.d("appupdates", "Result Ok")
                }
                Activity.RESULT_CANCELED -> {
                    Timber.d("appupdates", "Result Cancelled")
                    inAppUpdateManager?.checkForAppUpdate()
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Timber.d("Update Failure")
                }
            }
        } else {
            if (data != null) {

            }
        }
    }

}