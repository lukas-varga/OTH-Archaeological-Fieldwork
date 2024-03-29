package com.oth.archeology.views.login

import android.os.Handler
import com.google.firebase.auth.FirebaseAuth
import com.oth.archeology.R
import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.firebase.SiteFireStore
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView
import com.oth.archeology.views.VIEW
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import kotlin.concurrent.thread

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
                    fireStore!!.fetchSites {
                        view?.hideProgress()
                        fireStore!!.userPassword = password
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
            if(task.isSuccessful) {
                if (fireStore != null) {
                    fireStore!!.fetchSites {
                        view?.hideProgress()
                        fireStore!!.userPassword = password
//                        doPopulateSites(email,password)
                        view?.navigateTo(VIEW.LIST)
                    }

                } else {
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

    fun doLogOut(){
        FirebaseAuth.getInstance().signOut()
        app.sites.clear()
    }

//    /**
//     *     login as admin and creating assigned sites
//     */
//    fun doPopulateSites(email: String, password: String){
//        doLogOut()
//        doLogin(emailAdmin,passwordAdmin)
//        var defaultSites: List<SiteModel> = listOf()
//        var deepCopy : MutableList<SiteModel> = mutableListOf()
//
//        doAsync {
//            defaultSites = app.sites.findAll()
//            uiThread {
//                defaultSites.forEach() {
//                    var newSite = SiteModel()
//
//                    newSite.title = it.title
//                    newSite.description = it.description
//                    newSite.location = it.location
//
//                    deepCopy.add(newSite)
//                }
//            }
//        }
//
//        doLogOut()
//        doLogin(email,password,false)
//
//        doAsync {
//            deepCopy.forEach(){
//                var newSite = SiteModel()
//
//                newSite.title = it.title
//                newSite.description = it.description
//                newSite.location = it.location
//
//                app.sites.create(newSite)
//            }
//            uiThread {
//                doLogOut()
//                doLogin(email,password)
//            }
//        }
//    }
}
