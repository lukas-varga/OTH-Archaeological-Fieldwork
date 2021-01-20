package com.oth.archeology.models.mem

import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.SiteStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class SiteMemStore : SiteStore, AnkoLogger {

    val sites = ArrayList<SiteModel>()

    override fun findAll(): List<SiteModel> {
        return sites
    }

    override fun create(site: SiteModel) {
        site.id = getId()
        sites.add(site)
        logAll()
    }

    override fun update(site: SiteModel) {
        var foundSite: SiteModel? = sites.find { p -> p.id == site.id }
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
            logAll();
        }
    }

    override fun delete(site: SiteModel) {
        sites.remove(site)
    }

    override fun findById(id:Long) : SiteModel? {
        val foundSite: SiteModel? = sites.find { it.id == id }
        return foundSite
    }

    fun logAll() {
        sites.forEach { info("${it}") }
    }

    override fun clear() {
        sites.clear()
    }

    override fun getPassword(): String {
        return ""
    }
}