package com.oth.archeology.views.image

import com.oth.archeology.models.ImagePath
import com.oth.archeology.views.BasePresenter
import com.oth.archeology.views.BaseView
import org.jetbrains.anko.toast

class DisplayImagePresenter(view: BaseView) : BasePresenter (view) {

    var data = ImagePath()

    init {
        if (view.intent.hasExtra("display_image")) {
            data = view.intent.extras?.getParcelable<ImagePath>("display_image")!!
            view.showImage(data.path)
            view.toast("---"+data.path)
        }
        view.toast("---Nope")
//TODO Firebase
    }
}