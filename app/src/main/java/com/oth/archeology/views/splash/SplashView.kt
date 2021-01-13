package com.oth.archeology.views.splash

import android.os.Bundle
import android.view.View
import com.oth.archeology.R
import com.oth.archeology.views.BaseView
import com.oth.archeology.views.VIEW
import kotlinx.android.synthetic.main.activity_splash.*
import kotlin.system.exitProcess

class SplashView : BaseView (){

    lateinit var presenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        init(toolbar,false)

        toolbar.title = getString(R.string.title_splash)
        toolbar.visibility = View.GONE

        presenter = initPresenter(SplashPresenter(this)) as SplashPresenter

        progressBar.visibility = View.GONE

        presenter.loading()
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitProcess(-1)
    }
}