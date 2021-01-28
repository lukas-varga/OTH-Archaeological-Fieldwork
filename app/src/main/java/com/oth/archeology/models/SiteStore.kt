package com.oth.archeology.models

import android.content.Context

interface SiteStore {
    fun findAll(): List<SiteModel>
    fun findById(id:Long) : SiteModel?
    fun create(site: SiteModel)
    fun update(site: SiteModel)
    fun delete(site: SiteModel)
    fun clear()
}