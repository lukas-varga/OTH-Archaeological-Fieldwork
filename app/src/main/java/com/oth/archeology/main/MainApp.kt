package com.oth.archeology.main

import android.app.Application
import com.oth.archeology.models.SiteStore
import com.oth.archeology.models.firebase.SiteFireStore
import com.oth.archeology.models.json.SiteJSONStore
import com.oth.archeology.models.mem.SiteMemStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    lateinit var sites: SiteStore

    /**
     * Initial data was used from page below:
     * https://www.english-heritage.org.uk/visit/familydaysout/top-10-castles/
     */
    override fun onCreate() {
        super.onCreate()
        sites = SiteFireStore(applicationContext)
//        sites = SiteJSONStore(applicationContext)
//        sites = SiteMemStore()
        info("Argeo started")
    }
}