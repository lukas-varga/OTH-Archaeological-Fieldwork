package com.oth.archeology.main

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.oth.archeology.R
import com.oth.archeology.models.SiteStore
import com.oth.archeology.models.firebase.SiteFireStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    lateinit var sites: SiteStore

    override fun onCreate() {
        super.onCreate()
        sites = SiteFireStore(applicationContext)
        info("Argeo started")
    }
}