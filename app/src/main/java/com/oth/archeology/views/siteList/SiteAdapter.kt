package com.oth.archeology.views.siteList


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oth.archeology.R
import com.oth.archeology.models.SiteModel
import kotlinx.android.synthetic.main.card_site.view.*


interface SiteListener {
    fun onPlacemarkClick(placemark: SiteModel)
}

class PlacemarkAdapter constructor(
    private var placemarks: List<SiteModel>,
    private val listener: SiteListener
) : RecyclerView.Adapter<PlacemarkAdapter.MainHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(
            LayoutInflater.from(parent?.context).inflate(
                R.layout.card_site,
                parent,
                false
            )
        )
    }


    //binding data to cards
    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        val placemark = placemarks[holder.adapterPosition]
        holder.bind(placemark, listener)
    }

    override fun getItemCount(): Int = placemarks.size

    class MainHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        //implementation of binding
        fun bind(placemark: SiteModel, listener: SiteListener) {
            itemView.siteTitle.text = placemark.title
            itemView.description.text = placemark.description
//            itemView.placemarkImage.setImageBitmap(readImageFromPath(itemView.context,placemark.image))
            Glide.with(itemView.context).load(placemark.image).into(itemView.siteImage)
            itemView.setOnClickListener{listener.onPlacemarkClick(placemark)}
        }
    }
}
