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

    var emailAdmin = "admin@argeo.com"
    var passwordAdmin = "adminArgeo"

    init {
        if(app.sites is SiteFireStore){
            fireStore = app.sites as SiteFireStore
        }
    }

    fun doLogin(email: String, password: String, navigateTo: Boolean){
        view?.showProgress()
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(view!!) { task ->
            if(task.isSuccessful){
                if (fireStore != null) {

                    //TODO maybe change something in firestore

                    fireStore!!.fetchSites {
                        view?.hideProgress()
                        fireStore!!.userPassword = password
                        if(navigateTo){
                            view?.navigateTo(VIEW.LIST)
                        }
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
//                        doPopulateSites()
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

    /**
     *     login as admin and creating assigned sites
     *     email: admin@argeo.com
     *     password: adminArgeo
     */
    fun doPopulateSites(email: String, password: String){
        doLogin(emailAdmin,passwordAdmin,false)
        var defaultSites: List<SiteModel> = listOf()
        var deepCopy : MutableList<SiteModel> = mutableListOf()

        doAsync {
            defaultSites = app.sites.findAll()
            uiThread {
                defaultSites.forEach() {
                    var newSite = SiteModel()

                    newSite.title = it.title
                    newSite.title = it.title
                    newSite.description = it.description
                    newSite.images = it.images
                    newSite.location = it.location
                    newSite.date = it.date
                    newSite.notes = it.notes
                    newSite.visited = it.visited
                    newSite.favourite = it.favourite
                    newSite.rating = it.rating
                    deepCopy.add(newSite)
                }
            }
        }

        doLogOut()
        doLogin(email,password,false)

        doAsync {
            deepCopy.forEach(){
                var newSite = SiteModel()

                newSite.title = it.title
                newSite.title = it.title
                newSite.description = it.description
                newSite.images = it.images
                newSite.location = it.location
                newSite.date = it.date
                newSite.notes = it.notes
                newSite.visited = it.visited
                newSite.favourite = it.favourite
                newSite.rating = it.rating

                app.sites.create(newSite)

            }
            uiThread {
                view?.navigateTo(VIEW.LIST)
            }
        }
    }
}
