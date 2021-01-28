package com.oth.archeology.views.image

import android.os.Bundle
import com.bumptech.glide.Glide
import com.oth.archeology.R
import com.oth.archeology.helpers.readImageFromPath
import com.oth.archeology.views.BaseView
import kotlinx.android.synthetic.main.activity_display_image.*
import org.jetbrains.anko.info

class DisplayImageView : BaseView() {

    lateinit var presenter: DisplayImagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_image)
        super.init(toolbar, true)

        presenter = initPresenter(DisplayImagePresenter(this)) as DisplayImagePresenter
    }

    override fun showImage(path: String){
        try {
            Glide.with(this).load(path).into(displayImage)
        } catch (e: Exception) {
            print("Couldn't load image.")
//            displayImage.setImageBitmap(readImageFromPath(this,path))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}