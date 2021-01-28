package com.oth.archeology.views.sitelist

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.R
import com.google.firebase.database.ValueEventListener
import com.oth.archeology.models.MyDate
import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.firebase.SiteFireStore
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView
import com.oth.archeology.views.VIEW
import kotlinx.android.synthetic.main.activity_site_list.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
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

    fun doSearchedSites(search: String){
        doAsync {
            val sites = app.sites.findAll()
            var resultList: MutableList<SiteModel> = mutableListOf()
            sites.forEach(){
                when {
                    containsSearched(it.title,search) -> resultList.add(it)
                    containsSearched(it.description,search) -> resultList.add(it)
                    containsSearched(it.notes,search) -> resultList.add(it)
                    containsSearched(it.date,search) -> resultList.add(it)
                }
            }
            var finalResultList = resultList.distinct()
            uiThread {
                view?.showSites(finalResultList)
            }
        }
    }

    fun containsSearched(siteAttribute: String, search: String): Boolean{
        return siteAttribute.contains(search,true)
    }

    fun containsSearched(date: MyDate, search: String): Boolean{
        var siteString = "%02d/%02d/%04d".format(date.day, date.month, date.year)
        return (siteString == search)
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

    fun changeStatus(fireStore: SiteFireStore){
        if(fireStore.isDbInitialized()){
            val connectedRef = fireStore.db.database.getReference(".info/connected")
            connectedRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val connected = snapshot.getValue(Boolean::class.java) ?: false
                    if (connected) {
                        view?.onlineStatus()
                        print("online")
                    } else {
                        view?.offlineStatus()
                        print("offline")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    print("Listener was cancelled")
                }
            })
        }
    }
}