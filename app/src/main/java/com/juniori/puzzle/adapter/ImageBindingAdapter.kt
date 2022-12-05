package com.juniori.puzzle.adapter

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.ColorUtils
import androidx.databinding.BindingAdapter
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.juniori.puzzle.R

private const val DRAWABLE_WIDTH = 120
private const val DRAWABLE_HEIGHT = 120

@BindingAdapter("setImage")
fun setImage(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) return

    Glide.with(view.context)
        .load(url)
        .into(view)
}

@BindingAdapter("setDrawableLeft")
fun setDrawableLeft(view: TextView, url: String?) {
    if (url.isNullOrEmpty()) return

    try {
        val resourceId = url.toInt()
        Glide.with(view.context)
            .load(resourceId)
            .into(object : CustomTarget<Drawable>(DRAWABLE_WIDTH, DRAWABLE_HEIGHT) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    resource.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawables(resource, null, null, null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null)
                }
            })
    } catch (e: Exception) {
        Glide.with(view.context)
            .load(url)
            .into(object : CustomTarget<Drawable>(DRAWABLE_WIDTH, DRAWABLE_HEIGHT) {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    resource.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawables(resource, null, null, null)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    placeholder?.setBounds(0, 0, DRAWABLE_WIDTH, DRAWABLE_HEIGHT)
                    view.setCompoundDrawablesWithIntrinsicBounds(placeholder, null, null, null)
                }
            })
    }

}

@BindingAdapter("imageBytes")
fun setImageBytes(view: ImageView, imageBytes: ByteArray) {
    Glide.with(view.context)
        .load(imageBytes)
        .apply(
            RequestOptions.bitmapTransform(
                MultiTransformation(RoundedCorners(20), FitCenter())
            )
        )
        .into(view)
}

@BindingAdapter("setImageByPalette")
fun setImageByPalette(view: ImageView, url: String?) {
    if (url.isNullOrEmpty()) return

    Glide.with(view.context)
        .asBitmap()
        .load(url)
        .listener(
            object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        Palette.from(resource).generate { palette ->
                            palette?.let {
                                view.setBackgroundColor(
                                    ColorUtils.setAlphaComponent(
                                        it.dominantSwatch?.rgb ?: R.color.disabled_color, 128
                                    )
                                )
                            }
                        }
                    }
                    return false
                }

            }
        )
        .into(view)
}