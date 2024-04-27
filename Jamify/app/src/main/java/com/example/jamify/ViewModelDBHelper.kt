package com.example.jamify

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.jamify.model.PostMeta
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ViewModelDBHelper() {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val collectionRoot = "posts"

    private fun elipsizeString(string: String) : String {
        if(string.length < 10)
            return string
        return string.substring(0..9) + "..."
    }

    fun fetchInitialNotes(notesList: MutableLiveData<List<PostMeta>>,
                          sortInfo: SortInfo,
                          callback:()->Unit) {
        dbFetchNotes(notesList, sortInfo, callback)
    }
    /////////////////////////////////////////////////////////////
    // Interact with Firestore db
    // https://firebase.google.com/docs/firestore/query-data/get-data
    //
    // If we want to listen for real time updates use this
    // .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
    // But be careful about how listener updates live data
    // and noteListener?.remove() in onCleared
    private fun dbFetchNotes(notesList: MutableLiveData<List<PostMeta>>,
                             sortInfo: SortInfo,
                             callback:()->Unit = {}) {
        if (sortInfo.ascending) {
            db.collection(collectionRoot)
                .orderBy("timeStamp", Query.Direction.ASCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener { result ->
                    Log.d(javaClass.simpleName, "posts fetch ${result!!.documents.size}")
                    // NB: This is done on a background thread
                    notesList.postValue(result.documents.mapNotNull {
                        it.toObject(PostMeta::class.java)
                    })
                    callback()
                }
                .addOnFailureListener {
                    Log.d(javaClass.simpleName, "posts fetch FAILED ", it)
                    callback()
                }
        } else {
            db.collection(collectionRoot)
                .orderBy("timeStamp", Query.Direction.DESCENDING)
                .limit(100)
                .get()
                .addOnSuccessListener { result ->
                    Log.d(javaClass.simpleName, "posts fetch ${result!!.documents.size}")
                    // NB: This is done on a background thread
                    notesList.postValue(result.documents.mapNotNull {
                        it.toObject(PostMeta::class.java)
                    })
                    callback()
                }
                .addOnFailureListener {
                    Log.d(javaClass.simpleName, "posts fetch FAILED ", it)
                    callback()
                }
        }
    }




    // After we successfully modify the db, we refetch the contents to update our
    // live data.  Hence the dbFetchNotes() calls below.
    fun updateNote(
        note: PostMeta,
        notesList: MutableLiveData<List<PostMeta>>,
        sortInfo: SortInfo
    ) {
        val pictureUUIDs = note.photoUuid
        //SSS
        db.collection(collectionRoot)
            .document(note.firestoreID)
            .set(note)
            //EEE // XXX Writing a note
            .addOnSuccessListener {
//                Log.d(
//                    javaClass.simpleName,
//                   "Note update \"${elipsizeString(note.caption)}\" len ${pictureUUIDs.length} id: ${note.firestoreID}"
//                )
                dbFetchNotes(notesList, sortInfo)
            }
            .addOnFailureListener { e ->
//                Log.d(javaClass.simpleName, "Note update FAILED \"${elipsizeString(note.text)}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun createNote(
        note: PostMeta,
        notesList: MutableLiveData<List<PostMeta>>,
        sortInfo: SortInfo
    ) {
        // We can get ID locally
        // note.firestoreID = db.collection("allNotes").document().id

        db.collection(collectionRoot)
            .add(note)
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Note create \"${elipsizeString(note.caption)}\" id: ${note.firestoreID}"
                )
                dbFetchNotes(notesList, sortInfo)
            }
            .addOnFailureListener { e ->
//                Log.d(javaClass.simpleName, "Note create FAILED \"${elipsizeString(note.text)}\"")
                Log.w(javaClass.simpleName, "Error ", e)
            }
    }

    fun removeNote(
        note: PostMeta,
        notesList: MutableLiveData<List<PostMeta>>,
        sortInfo: SortInfo
    ) {
        db.collection(collectionRoot)
            .document(note.firestoreID)
            .delete()
            .addOnSuccessListener {
                Log.d(
                    javaClass.simpleName,
                    "Note delete \"${elipsizeString(note.caption)}\" id: ${note.firestoreID}"
                )
                dbFetchNotes(notesList, sortInfo)
            }
            .addOnFailureListener { e ->
//                Log.d(javaClass.simpleName, "Note deleting FAILED \"${elipsizeString(note.text)}\"")
                Log.w(javaClass.simpleName, "Error adding document", e)
            }
    }
}