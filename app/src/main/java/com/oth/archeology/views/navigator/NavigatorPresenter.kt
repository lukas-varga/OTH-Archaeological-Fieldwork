package com.oth.archeology.views.navigator

import android.annotation.SuppressLint
import android.graphics.Color
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.oth.archeology.R
import com.oth.archeology.helpers.checkLocationPermissions
import com.oth.archeology.helpers.createDefaultLocationRequest
import com.oth.archeology.helpers.isPermissionGranted
import com.oth.archeology.models.Location
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView

class NavigatorPresenter(view: BaseView) : BasePresenter(view){

    var map: GoogleMap? = null
    var defaultLocation = Location(52.245696, -7.139102, 15f)
    var locationService: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view)
    val locationRequest = createDefaultLocationRequest()
    var siteLoc = Location()
    var userZoom: Boolean = false

    init {
        siteLoc = view.intent.extras?.getParcelable<Location>("navigator")!!
    }

    fun createSiteMarker(map: GoogleMap){
        val finish = LatLng(siteLoc.lat, siteLoc.lng)
        val options: MarkerOptions = MarkerOptions()
            .title("Site")
            .snippet("GPS: $finish")
            .draggable(false)
            .position(finish)
        map.addMarker(options)
    }

    @SuppressLint("MissingPermission")
    fun doSetCurrentLocation() {
        locationService.lastLocation.addOnSuccessListener {
            locationUpdate(Location(it.latitude, it.longitude))
        }
    }

    override fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (isPermissionGranted(requestCode, grantResults)) {
            doSetCurrentLocation()
        } else {
            locationUpdate(defaultLocation)
        }
    }

    @SuppressLint("MissingPermission")
    fun doResartLocationUpdates() {
        var locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult != null && locationResult.locations != null) {
                    val l = locationResult.locations.last()
                    locationUpdate(Location(l.latitude, l.longitude))
                }
            }
        }
        locationService.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun locationUpdate(location: Location) {
        map?.clear()

        var start = LatLng(location.lat,location.lng)
        var finish = LatLng(siteLoc.lat,siteLoc.lng)

        makeLine(start,finish)
        createSiteMarker(map!!)

        val latlngBuilder = LatLngBounds.Builder()
        latlngBuilder.include(start)
        latlngBuilder.include(finish)


        if(!userZoom){
            map?.animateCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 100))
//        map.moveCamera(CameraUpdateFactory.newLatLngBounds(latlngBuilder.build(), 100))
        }


        view?.showLocation(location)
    }

    fun makeLine(start: LatLng, finish: LatLng){
        val polyline: Polyline = map!!.addPolyline(PolylineOptions()
                .clickable(true)
                .add(
                        LatLng(start.latitude, start.longitude),
                        LatLng(finish.latitude, finish.longitude))
        )

        polyline.endCap = RoundCap()
        polyline.color = Color.parseColor("#3973e6")
        polyline.width = 10f
        polyline.jointType = JointType.ROUND
    }

    fun doConfigureMap(map: GoogleMap) {
        this.map = map
        if(checkLocationPermissions(view!!)){
            doSetCurrentLocation()
        }
    }

    fun doUpdateMarker(marker: Marker){
        var newLoc = LatLng(siteLoc.lat, siteLoc.lng)
        marker.snippet = "GPS: $newLoc"
    }

    fun doUserZoom(){
        userZoom = true
    }

    fun doSystemZoom(){
        userZoom = false
    }
}
