package com.oth.archeology.models.json

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.oth.archeology.R
import com.oth.archeology.helpers.exists
import com.oth.archeology.helpers.read
import com.oth.archeology.helpers.write
import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.SiteStore
import org.jetbrains.anko.AnkoLogger
import java.util.*

val JSON_FILE = "sites.json"
val gsonBuilder = GsonBuilder().setPrettyPrinting().create()
val listType = object : TypeToken<ArrayList<SiteModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class SiteJSONStore : SiteStore, AnkoLogger {

    val context: Context
    var sites = mutableListOf<SiteModel>()

    constructor (context: Context) {
        this.context = context
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<SiteModel> {
        return sites
    }

    override fun create(site: SiteModel) {
        site.id = generateRandomId()
        sites.add(site)
        serialize()
    }

    override fun update(site: SiteModel) {
        val sitesList = findAll() as ArrayList<SiteModel>
        var foundSite: SiteModel? = sitesList.find { p -> p.id == site.id }
        if (foundSite != null) {
            foundSite.title = site.title
            foundSite.description = site.description
            foundSite.images = site.images
            foundSite.location = site.location
            foundSite.date = site.date
            foundSite.notes = site.notes
            foundSite.visited = site.visited
            foundSite.favourite = site.favourite
            foundSite.rating = site.rating
        }
        serialize()
    }

    override fun delete(site: SiteModel) {
        val foundSite: SiteModel? = sites.find { it.id == site.id }
        sites.remove(foundSite)
        serialize()
    }

    override fun findById(id:Long) : SiteModel? {
        val foundSite: SiteModel? = sites.find { it.id == id }
        return foundSite
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(sites, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        sites = Gson().fromJson(jsonString, listType)
    }

    override fun clear() {
        sites.clear()
    }
}
