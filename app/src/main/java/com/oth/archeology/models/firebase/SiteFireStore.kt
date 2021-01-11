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
import java.io.ByteArrayOutputStream
import java.io.File

class SiteFireStore(val context: Context) : SiteStore {

    val placemarks = ArrayList<SiteModel>()
    lateinit var userId: String
    lateinit var db: DatabaseReference
    lateinit var st: StorageReference

    override fun findAll(): List<SiteModel> {
        return placemarks
    }

    override fun findById(id: Long): SiteModel? {
        val foundPlacemark: SiteModel? = placemarks.find { p -> p.id == id }
        return foundPlacemark
    }

    override fun create(placemark: SiteModel) {
        val key = db.child("users").child(userId).child("placemarks").push().key
        key?.let {
            placemark.fbId = key
            placemarks.add(placemark)
            db.child("users").child(userId).child("placemarks").child(key).setValue(placemark)
            updateImage(placemark)
        }
    }

    override fun update(placemark: SiteModel) {
        var foundPlacemark: SiteModel? = placemarks.find { p -> p.fbId ==  placemark.fbId }
        if (foundPlacemark != null){
            foundPlacemark.title = placemark.title
            foundPlacemark.description = placemark.description
            foundPlacemark.image = placemark.image
            foundPlacemark.location = placemark.location
        }

        db.child("users").child(userId).child("placemarks").child(placemark.fbId).setValue(placemark)
        if ((placemark.image.length) > 0 && (placemark.image[0] != 'h')) {
            updateImage(placemark)
        }
    }

    override fun delete(placemark: SiteModel) {
        db.child("users").child(userId).child("placemarks").child(placemark.fbId).removeValue()
        placemarks.remove(placemark)
    }

    override fun clear() {
        placemarks.clear()
    }

    fun updateImage(placemark: SiteModel) {
        if (placemark.image != "") {
            val fileName = File(placemark.image)
            val imageName = fileName.getName()

            var imageRef = st.child(userId + '/' + imageName)
            val baos = ByteArrayOutputStream()
            val bitmap = readImageFromPath(context, placemark.image)

            bitmap?.let {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                val uploadTask = imageRef.putBytes(data)
                uploadTask.addOnFailureListener {
                    println(it.message)
                }.addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                        placemark.image = it.toString()
                        db.child("users").child(userId).child("placemarks").child(placemark.fbId).setValue(placemark)
                    }
                }
            }
        }
    }

    fun fetchPlacemarks(placemarksReady: () -> Unit) {
        val valueEventListener = object : ValueEventListener {
            override fun onCancelled(dataSnapshot: DatabaseError) {
            }
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot!!.children.mapNotNullTo(placemarks) { it.getValue<SiteModel>(SiteModel::class.java) }
                placemarksReady()
            }
        }
        userId = FirebaseAuth.getInstance().currentUser!!.uid
        db = FirebaseDatabase.getInstance().reference
        st = FirebaseStorage.getInstance().reference
        placemarks.clear()
        db.child("users").child(userId).child("placemarks").addListenerForSingleValueEvent(valueEventListener)
    }
}