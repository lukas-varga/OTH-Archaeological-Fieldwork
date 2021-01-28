package com.oth.archeology.views.settings

import com.google.firebase.auth.FirebaseAuth
import com.oth.archeology.R
import com.oth.archeology.models.firebase.SiteFireStore
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView

class SettingsPresenter(view: BaseView) : BasePresenter(view) {

    var email: String
    var password: String
    var allSites: Int = 0
    var visited: Int = 0

    init {
        if(app.sites is SiteFireStore){
            var fireStore = app.sites as SiteFireStore
            email = fireStore.getEmail()
            password = fireStore.getPassword()
        } else{
            email = view.resources.getString(R.string.settings_notAuth)
            password = view.resources.getString(R.string.settings_notAuth)
        }

        var siteArr = app.sites.findAll()
        allSites = siteArr.size

        var counter = 0
        for (site in siteArr){
            if(site.visited) counter++
        }
        visited = counter
    }

    fun doRefreshInfo(){
        view?.displayInfo()
    }
}