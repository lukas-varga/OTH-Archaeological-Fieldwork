package com.oth.archeology.views.settings

import com.google.firebase.auth.FirebaseAuth
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView

class SettingsPresenter(view: BaseView) : BasePresenter(view) {

    lateinit var email: String
    lateinit var password: String
    var allSites: Int = 0
    var visited: Int = 0

    init {
        var auth: FirebaseAuth = FirebaseAuth.getInstance()
        var user = auth.currentUser

        if (user != null){
            email = user.email.toString()
            password = app.sites.getPassword()
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