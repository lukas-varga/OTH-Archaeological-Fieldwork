package com.oth.archeology.views.settings

import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.Menu
import android.view.MenuItem
import com.oth.archeology.R
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.activity_settings.toolbar
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.toast

class SettingsView : BaseView() {

    lateinit var presenter: SettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init(toolbar, true)
        
        presenter = initPresenter(SettingsPresenter(this)) as SettingsPresenter

        showHidePassword.setOnClickListener(){
            var title = showHidePassword.text
            if(title == getString(R.string.button_showPassword)){
                settingsPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                showHidePassword.setText(R.string.button_hidePassword)
            }
            else if(title == getString(R.string.button_hidePassword)){
                settingsPassword.transformationMethod = PasswordTransformationMethod.getInstance();
                showHidePassword.setText(R.string.button_showPassword)
            }
        }
    }

    override  fun displayInfo(){
        settingsEmail.setText(presenter.email)
        settingsPassword.setText(presenter.password)
        settingsAllSites.setText(presenter.allSites.toString())
        settingsVisited.setText(presenter.visited.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_refreshInfo -> {
                presenter.doRefreshInfo()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}