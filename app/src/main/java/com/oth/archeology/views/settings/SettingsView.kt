package com.oth.archeology.views.settings

import android.os.Bundle
import com.oth.archeology.R
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsView : BaseView() {

    lateinit var presenter: SettingsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        init(toolbar, true)
        
        presenter = initPresenter(SettingsPresenter(this)) as SettingsPresenter
    }
}