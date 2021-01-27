package com.oth.archeology.views

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.FirebaseAuth
import com.oth.archeology.models.MyDate
import com.oth.archeology.models.Location
import com.oth.archeology.models.SiteModel
import com.oth.archeology.views.image.DisplayImageView
import com.oth.archeology.views.location.EditLocationView
import com.oth.archeology.views.login.LoginView
import com.oth.archeology.views.navigator.NavigatorView
import com.oth.archeology.views.sitemaps.SiteMapView
import com.oth.archeology.views.settings.SettingsView
import com.oth.archeology.views.site.SiteView
import com.oth.archeology.views.sitelist.SiteListView
import com.oth.archeology.views.splash.SplashView
import org.jetbrains.anko.AnkoLogger

val IMAGE_REQUEST = 1
val LOCATION_REQUEST = 2
val REQUEST_IMAGE_CAPTURE = 3


enum class VIEW{
    LOCATION, SITE, MAPS, LIST, LOGIN, SPLASH, SETTINGS, DISPLAY, NAVIGATOR
}

open abstract class BaseView() : AppCompatActivity(), AnkoLogger {

    var basePresenter: BasePresenter? = null

    fun navigateTo(view: VIEW, code: Int = 0, key: String = "", value: Parcelable? = null){
        var intent = Intent(this, SiteListView::class.java)
        when(view){
            VIEW.SPLASH -> intent = Intent(this,SplashView::class.java)
            VIEW.LOGIN -> intent = Intent(this,LoginView::class.java)
            VIEW.LIST -> intent = Intent(this,SiteListView::class.java)
            VIEW.SITE -> intent = Intent(this,SiteView::class.java)
            VIEW.LOCATION -> intent = Intent(this, EditLocationView::class.java)
            VIEW.MAPS -> intent = Intent(this, SiteMapView::class.java)
            VIEW.SETTINGS -> intent = Intent(this, SettingsView::class.java)
            VIEW.DISPLAY  -> intent = Intent(this, DisplayImageView::class.java)
            VIEW.NAVIGATOR -> intent = Intent(this, NavigatorView::class.java)
        }
        if(key != ""){
            intent.putExtra(key,value)
        }
        startActivityForResult(intent,code)
    }

    fun initPresenter(presenter: BasePresenter): BasePresenter{
        basePresenter = presenter
        return presenter
    }


    fun init(toolbar: Toolbar, upEnabled: Boolean) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(upEnabled)
        var user = FirebaseAuth.getInstance().currentUser
        if (user != null){
            var email = user.email.toString()
            var arr = email.split("@")
            var userName = arr[0]
            toolbar.title = "$title: $userName"
        }
    }

    override fun onDestroy() {
        basePresenter?.onDestroy()
        super.onDestroy()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            basePresenter?.doActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun showSite(site: SiteModel) {}
    open fun showSites(sites: List<SiteModel>) {}
    open fun showLocation(location: Location) {}
    open fun showImage(path: String) {}
    open fun showDate(date: MyDate) {}
    open fun displayInfo() {}
    open fun showProgress() {}
    open fun hideProgress() {}
}