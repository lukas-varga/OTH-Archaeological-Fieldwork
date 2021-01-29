package com.oth.archeology.views.sitelist

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.oth.archeology.R
import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.firebase.SiteFireStore
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_site_list.*
import org.jetbrains.anko.toast


class   SiteListView  : BaseView(), SiteListener, NavigationView.OnNavigationItemSelectedListener{

    lateinit var presenter: SiteListPresenter
    lateinit var menuList: Menu
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_site_list)
        super.init(toolbar, false)
        presenter = initPresenter(SiteListPresenter(this)) as SiteListPresenter

        toolbar.setNavigationOnClickListener(){
            drawerLayout.openDrawer(GravityCompat.START)
        }
        navigationView.setNavigationItemSelectedListener(this)

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        presenter.doLoadSites()

        handleStatus()
    }

    override fun showSites(sites: List<SiteModel>) {
        var layout: RecyclerView = findViewById(R.id.recyclerView)
        layout.invalidate()
        layout.requestLayout()

        recyclerView.adapter = SiteAdapter(sites, this)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    fun handleStatus(){
        if(presenter.app.sites is SiteFireStore){
            var fireStore = presenter.app.sites as SiteFireStore
            presenter.changeStatus(fireStore)
        }
    }

    override fun onlineStatus() {
        super.onlineStatus()
        invalidateDrawer()

        var menuNavigator = navigationView.menu
        var item: MenuItem = menuNavigator.findItem(R.id.drawer_status)

        if(item.title == getString(R.string.menu_offline)){
                toast("Online")
        }

        item.setTitle(R.string.menu_online)
        item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.presence_online))

    }

    override fun offlineStatus() {
        super.offlineStatus()
        invalidateDrawer()

        var menuNavigator = navigationView.menu
        var item: MenuItem = menuNavigator.findItem(R.id.drawer_status)

        if(item.title == getString(R.string.menu_online)){
            toast("Offline")
        }

        item.setTitle(R.string.menu_offline)
        item.setIcon(ContextCompat.getDrawable(this, android.R.drawable.ic_menu_recent_history))
    }

    fun invalidateDrawer(){
        var layout: DrawerLayout = findViewById(R.id.drawerLayout)
        layout.invalidate()
        layout.requestLayout()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if(menu != null){
            this.menuList = menu
        }
        menuInflater.inflate(R.menu.menu_site_list, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.item_add -> presenter.doAddSite()
            R.id.item_map -> presenter.doShowSiteMap()
            R.id.item_search -> showSearchDialog()
            R.id.item_searchCancel -> {
                presenter.doLoadSites()
                var searchCancel: MenuItem = menuList.findItem(R.id.item_searchCancel)
                searchCancel.isVisible = false
            }
            R.id.item_favourite -> showFavourites()
            R.id.item_logout -> presenter.doLogout()
            R.id.item_settings -> presenter.doSettings()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.drawer_SiteAdd -> presenter.doAddSite()
            R.id.drawer_map -> presenter.doShowSiteMap()
            R.id.drawer_search -> showSearchDialog()
            R.id.drawer_favourite -> showFavourites()
            R.id.drawer_logout -> presenter.doLogout()
            R.id.drawer_settings -> presenter.doSettings()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun showFavourites(){
        var favMenu: MenuItem = menuList.findItem(R.id.item_favourite)

        var menuNavigator = navigationView.menu
        var favNavi: MenuItem = menuNavigator.findItem(R.id.drawer_favourite)

        if (favMenu.title == getString(R.string.menu_showFavourite) && favNavi.title == getString(R.string.menu_showFavourite)) {
            presenter.doShowFavourites()
            favMenu.setTitle(R.string.menu_showAll)
            favNavi.setTitle(R.string.menu_showAll)
        }
        else if (favMenu.title == getString(R.string.menu_showAll) && favNavi.title == getString(R.string.menu_showAll)) {
            presenter.doLoadSites()
            favMenu.setTitle(R.string.menu_showFavourite)
            favNavi.setTitle(R.string.menu_showFavourite)
        }
    }

    fun showSearchDialog(){
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle(R.string.menu_search)

        val dialogLayout = inflater.inflate(R.layout.search_dialog, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.search_editText)
        builder.setView(dialogLayout)

        builder.setPositiveButton("OK") { dialogInterface, i ->
            var searchedText = editText.text.toString()
            toast(searchedText)
            presenter.doSearchedSites(searchedText)

            var item: MenuItem = menuList.findItem(R.id.item_searchCancel)
            item.isVisible = true
        }

        builder.setNegativeButton("Cancel"){  dialogInterface, i ->
            presenter.doLoadSites()

            var searchCancel: MenuItem = menuList.findItem(R.id.item_searchCancel)
            searchCancel.isVisible = false
        }
        builder.show()
    }

    //edit existing site
    override fun onSiteClick(site: SiteModel) {
        presenter.doEditSite(site)
    }

    //upadte recyclerView while changing site
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        resetCategoryButtons()
        presenter.doLoadSites()
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun resetCategoryButtons(){
        var searchCancel: MenuItem = menuList.findItem(R.id.item_searchCancel)
        searchCancel.isVisible = false

        var menuNavigator = navigationView.menu
        var favNavi: MenuItem = menuNavigator.findItem(R.id.drawer_favourite)
        var favMenu: MenuItem = menuList.findItem(R.id.item_favourite)

        favNavi.setTitle(R.string.menu_showFavourite)
        favMenu.setTitle(R.string.menu_showFavourite)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        presenter.doLogout()
    }
}