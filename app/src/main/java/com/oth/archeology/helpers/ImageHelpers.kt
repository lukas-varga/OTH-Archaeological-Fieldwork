package com.oth.archeology.helpers

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Environment.getExternalStoragePublicDirectory
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.getExternalFilesDirs
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.FileProvider
import com.oth.archeology.R
import com.oth.archeology.views.REQUEST_IMAGE_CAPTURE
import org.jetbrains.anko.contentView
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.toast
import org.jetbrains.anko.info
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

lateinit var currentPhotoPath: String

fun showImagePicker(parent: Activity, id: Int) {
    val intent = Intent()
    intent.type = "image/*"
    intent.action = Intent.ACTION_OPEN_DOCUMENT
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    val chooser = Intent.createChooser(intent, R.string.picture_chooser.toString())
    parent.startActivityForResult(chooser, id)
}

fun readImage(activity: Activity, resultCode: Int, data: Intent?): Bitmap? {
    var bitmap: Bitmap? = null
    if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, data.data)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    return bitmap
}

fun readImageFromPath(context: Context, path : String) : Bitmap? {
    var bitmap : Bitmap? = null
    val uri = Uri.parse(path)
    if (uri != null) {
        try {
            val parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r")
            val fileDescriptor = parcelFileDescriptor?.getFileDescriptor()
            bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor?.close()
        } catch (e: Exception) {
        }
    }
    return bitmap
}

fun dispatchTakePictureIntent(parent: Activity, id: Int) {
    Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(parent.packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                createImageFile(parent)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    parent,
                    "com.oth.archeology.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                parent.startActivityForResult(takePictureIntent, id)
            }
        }
    }
}

@Throws(IOException::class)
fun createImageFile(context: Context): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = "file:" + absolutePath
//        currentPhotoPath = absolutePath
    }
}
