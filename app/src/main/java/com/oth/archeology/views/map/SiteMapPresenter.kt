package com.oth.archeology.views.map

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.oth.archeology.models.SiteModel
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class SiteMapPresenter(view: BaseView) : BasePresenter(view){

    fun doPopulateMap(map: GoogleMap, sites: List<SiteModel>){
        map.uiSettings.setZoomControlsEnabled(true)
        sites.forEach {
            val loc = LatLng(it.location.lat, it.location.lng)
            val options = MarkerOptions().title(it.title).position(loc)
            map.addMarker(options).tag = it.id
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, it.location.zoom))
        }
    }

    fun doMarkerSelected(marker: Marker) {
        val tag = marker.tag as Long
        doAsync {
            val site = app.sites.findById(tag)
            uiThread {
                if (site != null) view?.showSite(site)
            }
        }
    }

    fun loadSites() {
        doAsync {
            val sites = app.sites.findAll()
            uiThread {
                view?.showSites(sites)
            }
        }
    }
}