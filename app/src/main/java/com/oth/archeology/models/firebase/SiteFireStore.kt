package com.oth.archeology.models.firebase

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.oth.archeology.helpers.readImageFromPath
import com.oth.archeology.models.IMAGE
import com.oth.archeology.models.Images
import com.oth.archeology.models.SiteModel
import com.oth.archeology.models.SiteStore
import kotlinx.android.synthetic.main.activity_site.*
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

            for (enum in IMAGE.values()){
                updateImage(site,enum)
            }
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

        db.child("users")
                .child(userId)
                .child("sites")
                .child(site.fbId)
                .setValue(site)

        var images = arrayOf(site.images.first, site.images.second, site.images.third, site.images.fourth)
        var enums = arrayOf(IMAGE.FIRST,IMAGE.SECOND,IMAGE.THIRD,IMAGE.FOURTH)

        for (i in images.indices) {
            if((images[i].length) > 0 && (images[i][0] != 'h')){
                updateImage(site,enums[i])
            }
        }
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


    fun updateImage(site: SiteModel, enum: IMAGE) {
         var selectedImage = when(enum){
            IMAGE.FIRST -> site.images.first
            IMAGE.SECOND -> site.images.second
            IMAGE.THIRD -> site.images.third
            IMAGE.FOURTH -> site.images.fourth
        }

        if (selectedImage != "") {
            val fileName = File(selectedImage)
            val imageName = fileName.getName()

            var imageRef = st.child(userId + "/" + imageName)
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
                        db.child("users")
                                .child(userId).child("sites")
                                .child(site.fbId)
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
        db.child("users")
                .child(userId)
                .child("sites")
                .addListenerForSingleValueEvent(valueEventListener)
    }
}
