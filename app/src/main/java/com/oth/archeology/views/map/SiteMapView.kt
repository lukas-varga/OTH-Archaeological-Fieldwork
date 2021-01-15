package com.oth.archeology.views.map

import android.os.Bundle
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.oth.archeology.R
import com.oth.archeology.models.SiteModel
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_edit_map.toolbar
import kotlinx.android.synthetic.main.activity_site_maps.*

class SiteMapView : BaseView(), GoogleMap.OnMarkerClickListener {

    lateinit var presenter: SiteMapPresenter
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_maps)
        super.init(toolbar,true)

        presenter = initPresenter(SiteMapPresenter(this)) as SiteMapPresenter

        mapViewAll.onCreate(savedInstanceState)
        mapViewAll.getMapAsync {
            map = it
            map.setOnMarkerClickListener(this)
            presenter.loadSites()
        }
    }

    override fun showSite(site: SiteModel){
        currentTitle.text = site.title
        currentDescription.text = site.description
//        currentImage.setImageBitmap(readImageFromPath(this, site.image))
        Glide.with(this).load(site.images).into(currentImage);
    }

    override fun showSites(sites: List<SiteModel>) {
        presenter.doPopulateMap(map, sites)
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doMarkerSelected(marker)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        mapViewAll.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewAll.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapViewAll.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapViewAll.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewAll.onSaveInstanceState(outState)
    }
}