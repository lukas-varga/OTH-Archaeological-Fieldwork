package com.oth.archeology.views.login

import android.os.Bundle
import android.view.View
import com.oth.archeology.R
import com.oth.archeology.views.BaseView
import com.oth.archeology.views.VIEW
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.toast

class LoginView : BaseView() {

    lateinit var presenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        init(toolbar, false)

        toolbar.setTitle(R.string.title_activity_login)

        presenter = initPresenter(LoginPresenter(this)) as LoginPresenter

        progressBar.visibility = View.GONE

        buttonSignUp.setOnClickListener{
            val email = fieldEmail.text.toString()
            val password = fieldPassword.text.toString()
            presenter.canContinue = false

            if(email == "" || password == ""){
                toast(R.string.toast_missingAuthItems)
            }
            else{
                presenter.doSignUp(email,password)
            }
        }

        buttonLogIn.setOnClickListener {
            val email = fieldEmail.text.toString()
            val password = fieldPassword.text.toString()
            presenter.canContinue = true

            if(email == "" || password == ""){
                toast(R.string.toast_missingAuthItems)
            }
            else{
                presenter.doLogin(email,password)
            }
        }
    }

    override fun showProgress() {
        progressBar.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        navigateTo(VIEW.SPLASH)
    }
}