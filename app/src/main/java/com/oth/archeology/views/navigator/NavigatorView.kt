package com.oth.archeology.views.navigator

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.oth.archeology.R
import com.oth.archeology.models.Location
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.navigator.*

class NavigatorView : BaseView(), GoogleMap.OnMarkerClickListener , GoogleMap.OnMapClickListener{

    lateinit var presenter: NavigatorPresenter
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigator)
        super.init(toolbar, true)

        presenter = initPresenter(NavigatorPresenter(this)) as NavigatorPresenter

        mapViewNavigator.onCreate(savedInstanceState)
        mapViewNavigator.getMapAsync {
            map = it
            map.setOnMarkerClickListener(this)
            map.setOnMapClickListener(this)
            map.uiSettings.isZoomControlsEnabled = true

            presenter.doConfigureMap(map)
        }

        presenter.doResartLocationUpdates()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_navigator, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.item_refreshMap -> presenter.doSystemZoom()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showLocation(location: Location) {
        valueLatMap.setText(String.format("%.6f", location.lat))
        valueLngMap.setText(String.format("%.6f", location.lng))
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doUpdateMarker(marker)
        return false
    }

    override fun onMapClick(p0: LatLng?) {
        presenter.doUserZoom()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapViewNavigator.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewNavigator.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapViewNavigator.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapViewNavigator.onResume()
        presenter.doResartLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewNavigator.onSaveInstanceState(outState)
    }
}
