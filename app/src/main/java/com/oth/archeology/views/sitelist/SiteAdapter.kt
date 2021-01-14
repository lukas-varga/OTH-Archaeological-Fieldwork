package com.oth.archeology.views.sitelist


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oth.archeology.R
import com.oth.archeology.helpers.readImageFromPath
import com.oth.archeology.models.SiteModel
import kotlinx.android.synthetic.main.card_site.view.*


interface SiteListener {
    fun onSiteClick(placemark: SiteModel)
}

class SiteAdapter constructor(
    private var placemarks: List<SiteModel>,
    private val listener: SiteListener
) : RecyclerView.Adapter<SiteAdapter.MainHolder>() {

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
        fun bind(site: SiteModel, listener: SiteListener) {
            itemView.siteTitle.text = site.title

            itemView.latitude.text = (String.format("%.6f", site.location.lng))
            itemView.longitude.text = (String.format("%.6f", site.location.lng))

            itemView.visited.isChecked = site.visited
            itemView.favourite.isChecked = site.favourite

            itemView.siteImage.setImageBitmap(readImageFromPath(itemView.context,site.image))
            Glide.with(itemView.context).load(site.image).into(itemView.siteImage)
            itemView.setOnClickListener{listener.onSiteClick(site)}
        }
    }
}
