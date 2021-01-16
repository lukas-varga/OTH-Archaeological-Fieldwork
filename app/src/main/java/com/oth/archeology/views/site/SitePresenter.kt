package com.oth.archeology.views.site

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.oth.archeology.helpers.checkLocationPermissions
import com.oth.archeology.helpers.createDefaultLocationRequest
import com.oth.archeology.helpers.isPermissionGranted
import com.oth.archeology.helpers.showImagePicker
import com.oth.archeology.models.*
import com.oth.archeology.views.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.info
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread


class SitePresenter(view: BaseView) : BasePresenter(view) {

    var site = SiteModel()
    var map: GoogleMap? = null
    var defaultLocation = Location(52.245696, -7.139102, 15f)
    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
    val locationRequest = createDefaultLocationRequest()
    var edit = false
    var userLoc = false
    var selectedImage: IMAGE = IMAGE.FIRST

    init {
        if (view.intent.hasExtra("site_edit")) {
            edit = true
            site = view.intent.extras?.getParcelable<SiteModel>("site_edit")!!
            view.showSite(site)
        }
        else{
            if(checkLocationPermissions(view)){
                doSetCurrentLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            locationUpdate(Location(it.latitude, it.longitude))
        }
    }

    @SuppressLint("MissingPermission")
    fun doResartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    if(!userLoc){
                        val l = locationResult.locations.last()
                        locationUpdate(Location(l.latitude, l.longitude))
                    }
                }
            }
        }
        if (!edit) {
            locationService.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isPermissionGranted(requestCode, grantResults)) {
            doSetCurrentLocation()
        } else {
            locationUpdate(defaultLocation)
        }
    }

    fun cacheSite(title: String, description: String){
        site.title = title
        site.description = description
    }

    fun doConfigureMap(map: GoogleMap?) {
        this.map = map
        locationUpdate(site.location)
    }

    private fun locationUpdate(location: Location) {
        site.location = location
        site.location.zoom = 15f
        map?.clear()
        map?.uiSettings?.setZoomControlsEnabled(true)
        val options = MarkerOptions()
                .title(site.title)
                .position(LatLng(site.location.lat, site.location.lng))
        map?.addMarker(options)
        map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(site.location.lat, site.location.lng), site.location.zoom))
        view?.showLocation(site.location)
    }

    fun doAddOrSave(title: String, description: String) {
        site.title = title
        site.description = description
        doAsync {
            if (edit) {
                app.sites.update(site)
            } else {
                app.sites.create(site)
            }
            uiThread {
                view?.finish()
            }
        }
    }

    fun doCancel() {
        view?.finish()
    }

    fun doDelete() {
        doAsync {
            app.sites.delete(site)
            uiThread {
                view?.finish()
            }
        }
    }

    fun showImagePicker(context: Context){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Choose a picture to change")

        var enums = arrayOf(IMAGE.FIRST.name,IMAGE.SECOND.name,IMAGE.THIRD.name,IMAGE.FOURTH.name)
        selectedImage= IMAGE.FIRST
        val checkedItem = 0 // first
        builder.setSingleChoiceItems(enums, checkedItem) { dialog, which ->
            selectedImage = when(which){
                0 -> IMAGE.FIRST
                1 -> IMAGE.SECOND
                2 -> IMAGE.THIRD
                3 -> IMAGE.FOURTH
                else -> IMAGE.FIRST
            }
        }


        builder.setPositiveButton("OK") { dialog, which ->
            view?.toast(""+selectedImage)
            doSelectImage()
        }
        builder.setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.show()
    }

    fun doSelectImage() {
        view?.let {
            showImagePicker(view!!, IMAGE_REQUEST)
        }
    }

    fun displayImage(enum: IMAGE){
        var text = when(enum){
            IMAGE.FIRST ->  site.images.first
            IMAGE.SECOND -> site.images.second
            IMAGE.THIRD -> site.images.third
            IMAGE.FOURTH -> site.images.fourth
        }
        var data = ImagePath(text)
        view?.info("---site presenter "+data.path)
        view?.navigateTo(VIEW.DISPLAY,8,"display_image",data)
    }

    fun doSetLocation() {
        userLoc = true
        view?.navigateTo(VIEW.LOCATION, LOCATION_REQUEST, "location", Location(site.location.lat, site.location.lng, site.location.zoom))
    }


    override fun doActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            IMAGE_REQUEST -> {
                when(selectedImage){
                    IMAGE.FIRST -> site.images.first = data.data.toString()
                    IMAGE.SECOND -> site.images.second = data.data.toString()
                    IMAGE.THIRD -> site.images.third = data.data.toString()
                    IMAGE.FOURTH -> site.images.fourth = data.data.toString()
                }
                view?.showSite(site)
            }
            LOCATION_REQUEST -> {
                val location = data.extras?.getParcelable<Location>("location")!!
                site.location.lat = location.lat
                site.location.lng = location.lng
                site.location.zoom = location.zoom
                locationUpdate(location)
            }
        }
    }
}
