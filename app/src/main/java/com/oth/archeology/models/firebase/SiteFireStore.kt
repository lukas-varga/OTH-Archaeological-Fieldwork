package com.oth.archeology.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.oth.archeology.helpers.readImageFromPath
import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.SiteStore
import kotlinx.android.synthetic.main.activity_site.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SiteFireStore(val context: Context) : SiteStore, AnkoLogger {

    val sites = ArrayList<SiteModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference

    override fun findAll(): List<SiteModel> {
        return sites
    }

    override fun findById(id: Long): SiteModel? {
        val foundSite: SiteModel? = sites.find { p -> p.id == id }
        return foundSite
    }

    override fun create(site: SiteModel) {
        val key = db.child("users")
                .child(userId)
                .child("sites")
                .push().key
        key?.let {
            site.fbId = key
            sites.add(site)
            db.child("users")
                    .child(userId)
                    .child("sites")
                    .child(key).setValue(site)

            updateImages(site)
        }
    }

    override fun update(site: SiteModel) {
        var foundSite: SiteModel? = sites.find { p -> p.fbId == site.fbId }
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

        db.child("users")
                .child(userId)
                .child("sites")
                .child(site.fbId)
                .setValue(site)

        updateImages(site)
    }

    override fun delete(site: SiteModel) {
        db.child("users")
                .child(userId)
                .child("sites")
                .child(site.fbId)
                .removeValue()

        sites.remove(site)
    }

    override fun clear() {
        sites.clear()
    }


    fun updateImages(site: SiteModel) {
        var images = arrayOf(site.images.first, site.images.second, site.images.third, site.images.fourth)
        for (i in images.indices){
            if (images[i] != "") {
                val fileName = File(images[i])
                val imageName = fileName.getName()

                var imageRef = st.child(userId + "/" + imageName)
                val baos = ByteArrayOutputStream()
                val bitmap = readImageFromPath(context, images[i])

                bitmap?.let {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    val uploadTask = imageRef.putBytes(data)
                    uploadTask.addOnFailureListener {
                        println(it.message)
                    }.addOnSuccessListener { taskSnapshot ->
                        taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                            when(i){
                                0 -> site.images.first = it.toString()
                                1 -> site.images.second = it.toString()
                                2 -> site.images.third = it.toString()
                                3 -> site.images.fourth = it.toString()

                            }
                            db.child("users")
                                    .child(userId).child("sites")
                                    .child(site.fbId)
                                    .setValue(site)
                        }
                    }
                }
            }
        }
    }

    fun fetchSites(sitesReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot!!.children.mapNotNullTo(sites) { it.getValue<SiteModel>(SiteModel::class.java) }
                sitesReady()
            }
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference
        st = FirebaseStorage.getInstance().reference
        sites.clear()
        db.child("users")
                .child(userId)
                .child("sites")
                .addListenerForSingleValueEvent(valueEventListener)
    }
}
