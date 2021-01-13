package com.oth.archeology.views.location

import android.app.Activity
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.oth.archeology.models.Location
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView

class EditLocationPresenter (view: BaseView) : BasePresenter(view){

    var location = Location()

    init {
        location = view.intent.extras?.getParcelable<Location>("location")!!
    }

    fun doConfigureMap(map: GoogleMap) {
        val loc = LatLng(location.lat, location.lng)
        val options: MarkerOptions = MarkerOptions()
                .title("Site")
                .snippet("GPS: $loc")
                .draggable(true)
                .position(loc)
        map.addMarker(options)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
        view?.showLocation(location)
    }

    fun doUpdateLocation(location: Location) {
        this.location.lat = location.lat
        this.location.lng = location.lng
        this.location.zoom = location.zoom
    }

    fun doSaveLocation() {
        val resultInt = Intent()
        resultInt.putExtra("location", location)
        view?.setResult(Activity.RESULT_OK, resultInt)
        view?.finish()
    }

    fun doUpdateMarker(marker: Marker){
        var newLoc = LatLng(location.lat, location.lng)
        marker.snippet = "GPS: $newLoc"
    }
}
