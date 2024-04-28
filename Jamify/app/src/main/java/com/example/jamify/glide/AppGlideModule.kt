package com.example.jamify.glide

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.example.jamify.R
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import retrofit2.http.Url
import java.io.InputStream

@GlideModule
class AppGlideModule: AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}
object Glide {
    private var glideOptions = RequestOptions ()
        // Options like CenterCrop are possible, but I like this one best
        // Evidently you need fitCenter or dontTransform.  If you use centerCrop, your
        // list disappears.  I think that was an old bug.
        .fitCenter()
        // Rounded corners are so lovely.
        .transform(RoundedCorners (20))

    fun fetch(storageReference: StorageReference, imageView: ImageView) {
        // Layout engine does not know size of imageView
        // Hardcoding this here is a bad idea.  What would be better?
        val width = 400
        val height = 400
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(storageReference)
            .apply(glideOptions)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
            .error(R.drawable.baseline_account_circle_24) // Set the error drawable here
            .override(width, height)
            .into(imageView)
    }

    fun fetchProfile(downloadURL: String, imageView: ImageView) {
        // Layout engine does not know size of imageView
        // Hardcoding this here is a bad idea.  What would be better?
        val width = 400
        val height = 400
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(downloadURL)
            .apply(glideOptions)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
//            .skipMemoryCache(true)
            .error(R.drawable.baseline_account_circle_24) // Set the error drawable here
            .override(width, height)
            .into(imageView)
    }
}