package com.oth.archeology.views.sitelist

import com.google.firebase.auth.FirebaseAuth
import com.oth.archeology.models.SiteModel
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView
import com.oth.archeology.views.VIEW
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SiteListPresenter  (view: BaseView) : BasePresenter(view){

    fun doAddSite() {
        view?.navigateTo(VIEW.SITE)
    }

    fun doEditSite(site: SiteModel) {
        view?.navigateTo(VIEW.SITE, 0, "site_edit", site)
    }

    fun doShowSiteMap(){
        view?.navigateTo(VIEW.MAPS)
    }

    fun doShowFavourites(){
        doAsync {
            val sites = app.sites.findAll()
            var favourites: MutableList<SiteModel> = mutableListOf()
            sites.forEach(){
                if(it.favourite){
                    favourites.add(it)
                }
            }
            uiThread {
                view?.showSites(favourites)
            }
        }
    }

    fun doLoadSites() {
        doAsync {
            val sites = app.sites.findAll()
            uiThread {
                view?.showSites(sites)
            }
        }
    }

    fun doLogout(){
        FirebaseAuth.getInstance().signOut()
        app.sites.clear()
        view?.navigateTo(VIEW.LOGIN)
    }

    fun doSettings(){
        view?.navigateTo(VIEW.SETTINGS)
    }
}