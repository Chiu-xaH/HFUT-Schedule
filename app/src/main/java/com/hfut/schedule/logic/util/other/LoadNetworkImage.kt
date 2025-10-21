package com.hfut.schedule.logic.util.other

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hfut.schedule.application.MyApplication
import com.hfut.schedule.R

//fun loadImage(
//    url: String,
//    @DrawableRes defaultImageId: Int = R.drawable.ic_launcher_background,
//    cookie: String? = null
//): MutableState<Bitmap?> {
//    val bitmapState: MutableState<Bitmap?> = mutableStateOf(null)
//
//    val headers = LazyHeaders.Builder().apply {
//        if (!cookie.isNullOrEmpty()) {
//            addHeader("Cookie", cookie)
//        }
//    }.build()
//    val glideUrl = GlideUrl(url, headers)
//
//    //先加载本地图片
//    Glide.with(MyApplication.context)
//        .asBitmap()
//        .load(defaultImageId)
//        .into(object : CustomTarget<Bitmap>() {
//            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                //自定义Target，在加载完成后将图片资源传递给bitmapState
//                bitmapState.value = resource
//            }
//
//            override fun onLoadCleared(placeholder: Drawable?) {}
//        })
//
//    //然后再加载网络图片
//    try {
//        Glide.with(MyApplication.context)
//            .asBitmap()
//            .load(glideUrl)
//            .into(object : CustomTarget<Bitmap>() {
//                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                    //自定义Target，在加载完成后将图片资源传递给bitmapState
//                    bitmapState.value = resource
//                }
//
//                override fun onLoadCleared(placeholder: Drawable?) {}
//            })
//    } catch (glideException: GlideException) {
//        Log.d("Glide", "error: ${glideException.rootCauses}")
//    }
//
//    return bitmapState
//}

@Composable
fun rememberImageState(
    url: String,
    cookie: String? = null
): MutableState<Bitmap?> {
    val context = LocalContext.current
    val bitmapState = remember { mutableStateOf<Bitmap?>(null) }

    DisposableEffect(url, cookie) {
        val headers = LazyHeaders.Builder().apply {
            cookie?.let { addHeader("Cookie", it) }
        }.build()
        val glideUrl = GlideUrl(url, headers)

        val target = object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        }

        Glide.with(context)
            .asBitmap()
            .load(glideUrl)
            .into(target)

        onDispose {
            Glide.with(context).clear(target)
        }
    }

    return bitmapState
}
