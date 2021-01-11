package com.oth.archeology.views.login


import com.google.firebase.auth.FirebaseAuth
import com.oth.archeology.R
import com.oth.archeology.models.firebase.SiteFireStore
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView
import com.oth.archeology.views.VIEW
import org.jetbrains.anko.toast

class LoginPresenter(view: BaseView) : BasePresenter(view) {

    var auth: FirebaseAuth = FirebaseAuth.getInstance()
    var fireStore: SiteFireStore? = null

    init {
        if(app.sites is SiteFireStore){
            fireStore = app.sites as SiteFireStore
        }
    }

    fun doLogin(email: String, password: String){
        view?.showProgress()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(view!!) { task ->
            if(task.isSuccessful){
                if (fireStore != null) {
                    fireStore!!.fetchPlacemarks {
                        view?.hideProgress()
                        view?.navigateTo(VIEW.LIST)
                    }
                }
                else{
                    view?.hideProgress()
                    view?.navigateTo(VIEW.LIST)
                }
            }
            else {
                view?.hideProgress()
                view?.toast(R.string.toast_logInFailed.toString() + ": ${task.exception?.message}")
            }
        }
    }

    fun doSignUp(email: String, password: String){
        view?.showProgress()
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(view!!) { task ->
            if(task.isSuccessful){
                if (fireStore != null) {
                    fireStore!!.fetchPlacemarks {
                        view?.hideProgress()
                        view?.navigateTo(VIEW.LIST)
                    }
                }
                else{
                    view?.hideProgress()
                    view?.navigateTo(VIEW.LIST)
                }
            }
            else{
                view?.hideProgress()
                view?.toast(R.string.toast_signUpFailed.toString() + ": ${task.exception?.message}")
            }
        }
    }
}
