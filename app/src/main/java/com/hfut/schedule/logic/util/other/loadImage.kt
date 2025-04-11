package com.hfut.schedule.logic.util.other

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hfut.schedule.App.MyApplication
import com.hfut.schedule.R


fun loadImage(
    url: String,
    @DrawableRes defaultImageId: Int = R.drawable.ic_launcher_background,
    cookie: String? = null
): MutableState<Bitmap?> {
    val TAG = "LoadImage"
    val bitmapState: MutableState<Bitmap?> = mutableStateOf(null)

    //为请求加上 Headers ，提高访问成功率
    // 构造 GlideUrl，可选地添加 Cookie
    val headers = LazyHeaders.Builder().apply {
        if (!cookie.isNullOrEmpty()) {
            addHeader("Cookie", cookie)
        }
    }.build()
    val glideUrl = GlideUrl(url, headers)

    //先加载本地图片
    Glide.with(MyApplication.context)
        .asBitmap()
        .load(defaultImageId)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                //自定义Target，在加载完成后将图片资源传递给bitmapState
                bitmapState.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })

    //然后再加载网络图片
    try {
        Glide.with(MyApplication.context)
            .asBitmap()
            .load(glideUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //自定义Target，在加载完成后将图片资源传递给bitmapState
                    bitmapState.value = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    } catch (glideException: GlideException) {
//        Log.d(TAG, "error: ${glideException.rootCauses}")
    }

    return bitmapState
}