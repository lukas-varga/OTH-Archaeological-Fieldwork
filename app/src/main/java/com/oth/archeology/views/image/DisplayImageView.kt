package com.oth.archeology.views.image

import android.os.Bundle
import com.bumptech.glide.Glide
import com.oth.archeology.R
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_display_image.*

class DisplayImageView : BaseView() {

    lateinit var presenter: DisplayImagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_map)
        super.init(toolbar, true)

        presenter = initPresenter(DisplayImagePresenter(this)) as DisplayImagePresenter
    }

    override fun showImage(path: String){
//        displayImage.setImageBitmap(readImageFromPath(this,path)
        Glide.with(this).load(path).into(displayImage)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}