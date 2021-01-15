package com.oth.archeology.views.site

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.oth.archeology.R
import com.oth.archeology.helpers.readImageFromPath
import com.oth.archeology.models.Location
import com.oth.archeology.models.SiteModel
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jetbrains.anko.toast

class  SiteView : BaseView(), AnkoLogger {

lateinit var presenter: SitePresenter
lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site)
        super.init(toolbar,true)

        presenter = initPresenter(SitePresenter(this)) as SitePresenter

        siteMapView.onCreate(savedInstanceState)
        siteMapView.getMapAsync{
            map=it
            presenter.doConfigureMap(map)
            map.setOnMapClickListener{
                presenter.cacheSite(siteTitle.text.toString(),description.text.toString())
                presenter.doSetLocation()
            }
        }

        chooseImage.setOnClickListener(){
            info("image1 selected")
            presenter.cacheSite(siteTitle.text.toString(),description.text.toString())
            presenter.doSelectImage()
        }
    }

    override fun showSite(site: SiteModel){
        if(siteTitle.text.isEmpty()) siteTitle.setText(site.title)
        if(description.text.isEmpty())description.setText(site.description)
        siteImage.setImageBitmap(readImageFromPath(this,site.images.first))
        Glide.with(this).load(site.images).into(siteImage);

        if (site.images != null) {
           chooseImage.setText(R.string.button_changeImage)
        }

        this.showLocation(site.location)
    }

    override fun showLocation(location: Location) {
        valueLat.setText(String.format("%.6f",location.lat))
        valueLng.setText(String.format("%.6f",location.lng))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_site, menu)
        if(presenter.edit) menu.getItem(0).setVisible(true)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_delete -> {
                presenter.doDelete()
            }
            R.id.item_cancel -> {
                presenter.doCancel()
            }
            R.id.item_save -> {
                if (siteTitle.text.toString().isEmpty()) {
                    toast(R.string.toast_missingTitle)
                } else {
                    presenter.doAddOrSave(siteTitle.text.toString(), description.text.toString())
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null){
            presenter.doActivityResult(requestCode,resultCode,data)
        }
    }

    override fun onBackPressed() {
        presenter.doCancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        siteMapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        siteMapView.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        siteMapView.onPause()
    }

    override fun onResume() {
        super.onResume()
        siteMapView.onResume()
        presenter.doResartLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        siteMapView.onSaveInstanceState(outState)
    }
}