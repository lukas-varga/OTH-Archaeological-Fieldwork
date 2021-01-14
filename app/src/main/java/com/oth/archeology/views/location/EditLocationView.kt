package com.oth.archeology.views.location

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.oth.archeology.R
import com.oth.archeology.models.Location
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_edit_map.*


class EditLocationView : BaseView(), GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener  {

    lateinit var presenter: EditLocationPresenter
    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_map)
        super.init(toolbar, true)

        presenter = initPresenter(EditLocationPresenter(this)) as EditLocationPresenter

        mapViewEdit.onCreate(savedInstanceState)
        mapViewEdit.getMapAsync {
            map = it
            map.setOnMarkerDragListener(this)
            map.setOnMarkerClickListener(this)
            presenter.doConfigureMap(map)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_edit_map, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId){
            R.id.item_map -> {
                presenter.doSaveLocation()
            }
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

    private fun countLocation(marker: Marker) {
        showLocation(Location(marker.position.latitude, marker.position.longitude))
    }

    override fun onMarkerDragStart(marker: Marker) {
        countLocation(marker)
    }

    override fun onMarkerDrag(marker: Marker) {
        countLocation(marker)
    }

    override fun onMarkerDragEnd(marker: Marker) {
        countLocation(marker)
        presenter.doUpdateLocation(
                Location(
                        marker.position.latitude,
                        marker.position.longitude,
                        map.cameraPosition.zoom
                )
        )
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        presenter.doUpdateMarker(marker)
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        mapViewEdit.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapViewEdit.onLowMemory()
    }

    override fun onPause() {
        super.onPause()
        mapViewEdit.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapViewEdit.onResume()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapViewEdit.onSaveInstanceState(outState)
    }
}
