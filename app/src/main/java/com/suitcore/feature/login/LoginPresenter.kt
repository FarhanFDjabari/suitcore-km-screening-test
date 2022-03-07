package com.suitcore.feature.login

import com.google.android.material.textfield.TextInputEditText
import com.suitcore.BaseApplication
import com.suitcore.base.presenter.BasePresenter
import com.suitcore.data.remote.services.APIService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Created by dodydmw19 on 7/18/18.
 */

class LoginPresenter : BasePresenter<LoginView>, CoroutineScope {

    @Inject
    lateinit var apiService: APIService
    private var mvpView: LoginView? = null
    override val coroutineContext: CoroutineContext get() =  Dispatchers.IO + job
    private var job: Job = Job()

    init {
        BaseApplication.applicationComponent.inject(this)
    }

    fun login(email: String, password: String) = launch(Dispatchers.Main){
        runCatching {
            apiService.loginAsync(email, password).await()
        }.onSuccess { data ->
            if (data.token?.isNotEmpty() == true) {
                mvpView?.onLoginSuccess("login success")
            } else {
                mvpView?.onLoginFailed("login error")
            }
        }.onFailure {
            mvpView?.onLoginFailed(it.localizedMessage)
        }
    }

    fun checkPalindrome(text: String) = launch(Dispatchers.Main) {
            var isPalindrome: Boolean? = null
        runCatching {
            val textReversed = text.reversed();
            isPalindrome = text.filter{ letter -> letter != ' '} == textReversed.filter { letter -> letter != ' ' }
        }.onSuccess {
            if (isPalindrome == true) {
                mvpView?.onPalindrome("This text is palindrome")
            } else {
                mvpView?.onPalindrome("This text is not palindrome")
            }
        }.onFailure {

        }
    }

    override fun onDestroy() {
    }

    override fun attachView(view: LoginView) {
        mvpView = view
    }

    override fun detachView() {
        mvpView = null
    }
}