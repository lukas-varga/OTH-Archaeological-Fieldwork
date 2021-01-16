package com.oth.archeology.views

import android.content.Intent
import com.oth.archeology.main.MainApp

open abstract class BasePresenter(var view: BaseView?) {

    var app: MainApp = view?.application as MainApp

    open fun onDestroy(){
        view = null
    }

    open fun doActivityResult(requestCode: Int,resultCode: Int, data: Intent) {}
    open fun doRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {}
}