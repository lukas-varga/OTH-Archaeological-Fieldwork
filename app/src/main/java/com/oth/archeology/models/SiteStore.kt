package com.oth.archeology.models

interface SiteStore {
    fun findAll(): List<SiteModel>
    fun findById(id:Long) : SiteModel?
    fun create(placemark: SiteModel)
    fun update(placemark: SiteModel)
    fun delete(placemark: SiteModel)
    fun clear()
}