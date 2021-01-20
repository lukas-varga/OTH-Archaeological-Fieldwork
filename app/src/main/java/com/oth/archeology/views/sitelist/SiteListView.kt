package com.oth.archeology.views.sitelist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.oth.archeology.R
import com.oth.archeology.models.SiteModel
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_site_list.*


class   SiteListView  : BaseView(), SiteListener, NavigationView.OnNavigationItemSelectedListener{

    lateinit var presenter: SiteListPresenter
    lateinit var menuList: Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_list)
        super.init(toolbar, false)

        toolbar.setNavigationOnClickListener(){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        navigationView.setNavigationItemSelectedListener(this)


        presenter = initPresenter(SiteListPresenter(this)) as SiteListPresenter

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        presenter.doLoadSites()
    }

    override fun showSites(sites: List<SiteModel>) {
        var layout: RecyclerView = findViewById(R.id.recyclerView)
        layout.invalidate()
        layout.requestLayout()

        recyclerView.adapter = SiteAdapter(sites, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu!=null){
            this.menuList = menu
        }
        menuInflater.inflate(R.menu.menu_site_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item?.itemId) {
            R.id.item_add -> presenter.doAddSite()
            R.id.item_map -> presenter.doShowSiteMap()
            R.id.item_favourite -> {
                var fav: MenuItem = menuList.findItem(R.id.item_favourite)
                if (fav.title == getString(R.string.menu_showFavourite)) {
                    presenter.doShowFavourites()
                    fav.setTitle(R.string.menu_showAll)
                } else if (fav.title == getString(R.string.menu_showAll)) {
                    presenter.doLoadSites()
                    fav.setTitle(R.string.menu_showFavourite)
                }
            }
            R.id.item_logout -> presenter.doLogout()
            R.id.item_settings -> presenter.doSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.drawer_SiteAdd -> presenter.doAddSite()
            R.id.drawer_map -> presenter.doShowSiteMap()
            R.id.drawer_favourite -> {
                var menu = navigationView.menu
                var fav: MenuItem = menu.findItem(R.id.drawer_favourite)
                if (fav.title == getString(R.string.menu_showFavourite)) {
                    presenter.doShowFavourites()
                    fav.setTitle(R.string.menu_showAll)
                } else if (fav.title == getString(R.string.menu_showAll)) {
                    presenter.doLoadSites()
                    fav.setTitle(R.string.menu_showFavourite)
                }
            }
            R.id.drawer_logout -> presenter.doLogout()
            R.id.drawer_settings -> presenter.doSettings()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    //edit existing site
    override fun onSiteClick(site: SiteModel) {
        presenter.doEditSite(site)
    }

    //upadte recyclerView while changing site
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var fav: MenuItem = menuList.findItem(R.id.item_favourite)
        if(fav.title == getString(R.string.menu_showFavourite)){
            presenter.doLoadSites()
        }
        else if(fav.title == getString(R.string.menu_showAll)){
            presenter.doShowFavourites()
        }

        recyclerView.adapter?.notifyDataSetChanged()
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.doLogout()
    }
}