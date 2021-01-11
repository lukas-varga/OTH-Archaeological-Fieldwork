package com.oth.archeology.views.splash

import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView
import com.oth.archeology.views.VIEW

class SplashPresenter(view: BaseView) : BasePresenter(view) {

    val LOADING_DUR: Long = 3_000

    fun loading(){
        view?.showProgress()
        Thread.sleep(LOADING_DUR)
        view?.hideProgress()
        view?.navigateTo(VIEW.LOGIN)
    }
}