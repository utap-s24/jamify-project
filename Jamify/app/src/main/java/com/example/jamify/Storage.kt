package com.example.jamify

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import java.io.File

// Store files in firebase storage
//By: Emmett Witchel
class Storage {
    // Create a storage reference from our app
    private val photoStorage: StorageReference =
        FirebaseStorage.getInstance().reference.child("images")

    private fun deleteFile(localFile: File, uuid: String) {
        if(localFile.delete()) {
            Log.d(javaClass.simpleName, "Upload FAILED $uuid, file deleted")
        } else {
            Log.d(javaClass.simpleName, "Upload FAILED $uuid, file delete FAILED")
        }
    }
//    deleted from below : , uploadSuccess:()->Unit
    fun uploadImage(file: Uri) {
        //SSS
//        val file = Uri.fromFile(localFile)
        val uuidRef = photoStorage.child(file.toString())
        val metadata = StorageMetadata.Builder()
            .setContentType("image/jpg")
            .build()
        val uploadTask = uuidRef.putFile(file, metadata)
        //EEE // XXX Write me
        val localFile = file.toFile()
        // Register observers to listen for when the download is done or if it fails
        uploadTask
            .addOnFailureListener {
                // Handle unsuccessful uploads
                deleteFile(localFile, file.toString())
            }
            .addOnSuccessListener {
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
//                uploadSuccess()
                deleteFile(localFile, file.toString())
            }
    }
    fun deleteImage(pictureUUID: Uri) {
        // Delete the file
        photoStorage.child(pictureUUID.toString()).delete()
            .addOnSuccessListener {
                Log.d(javaClass.simpleName, "Deleted $pictureUUID")
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "Delete FAILED of $pictureUUID")
            }
    }
    fun listAllImages(listSuccess:(List<String>)->Unit) {
        // https://firebase.google.com/docs/storage/android/list-files#list_all_files
        photoStorage.listAll()
            .addOnSuccessListener { listResult ->
                Log.d(javaClass.simpleName, "listAllImages len: ${listResult.items.size}")
                val pictureUUIDs = listResult.items.map {
                    it.name
                }
                listSuccess(pictureUUIDs)
            }
            .addOnFailureListener {
                Log.d(javaClass.simpleName, "listAllImages FAILED")
            }
    }
    fun uuid2StorageReference(pictureUUID: String): StorageReference {
        return photoStorage.child(pictureUUID)
    }
}