package com.oth.archeology.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.oth.archeology.helpers.readImageFromPath
import com.oth.archeology.models.IMAGE
import com.oth.archeology.models.Images
import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.SiteStore
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class SiteFireStore(val context: Context) : SiteStore {

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
        val key = db.child("users").child(userId).child("sites").push().key
        key?.let {
            site.fbId = key
            sites.add(site)
            db.child("users").child(userId).child("sites").child(key).setValue(site)
            updateImage(site,IMAGE.FIRST)
            updateImage(site,IMAGE.SECOND)
            updateImage(site,IMAGE.THIRD)
            updateImage(site,IMAGE.FOURTH)
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
        }

        db.child("users").child(userId).child("sites").child(site.fbId).setValue(site)
        if ((site.images.first.length) > 0 && (site.images.first[0] != 'h')) {
            updateImage(site,IMAGE.FIRST)
        }
        if ((site.images.second.length) > 0 && (site.images.second[0] != 'h')) {
            updateImage(site,IMAGE.SECOND)
        }
        if ((site.images.third.length) > 0 && (site.images.third[0] != 'h')) {
            updateImage(site,IMAGE.THIRD)
        }
        if ((site.images.fourth.length) > 0 && (site.images.fourth[0] != 'h')) {
            updateImage(site,IMAGE.FOURTH)
        }
    }

    override fun delete(site: SiteModel) {
        db.child("users").child(userId).child("sites").child(site.fbId).removeValue()
        sites.remove(site)
    }

    override fun clear() {
        sites.clear()
    }

    fun updateImage(site: SiteModel,eImage: IMAGE) {
        var selectedImage: String
        selectedImage = when(eImage){
            IMAGE.FIRST -> site.images.first
            IMAGE.SECOND -> site.images.second
            IMAGE.THIRD -> site.images.third
            IMAGE.FOURTH -> site.images.fourth
        }

        if (selectedImage != "") {
            val fileName = File(selectedImage)
            val imageName = fileName.getName()

            var imageRef = st.child(userId + '/' + imageName)
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, selectedImage)

            bitmap?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    println(it.message)
                }.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        selectedImage = it.toString()
                        db.child("users").child(userId).child("sites").child(site.fbId)
                            .setValue(site)
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
        db.child("users").child(userId).child("sites")
            .addListenerForSingleValueEvent(valueEventListener)
    }
}
