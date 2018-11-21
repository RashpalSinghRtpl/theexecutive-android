package com.ranosys.theexecutive.base

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.ObservableField
import android.graphics.Bitmap
import android.support.design.widget.TextInputLayout
import android.text.TextUtils
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.Spinner
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.adapters.CategoryThreeLevelAdapter
import com.ranosys.theexecutive.utils.Constants
import com.ranosys.theexecutive.utils.GlideApp
import com.ranosys.theexecutive.utils.GlobalSingelton
import com.ranosys.theexecutive.utils.Utils


/**
 * @Details Adapters method all binding in xml
 * @Author Ranosys Technologies
 * @Date 22,Feb,2018
 */
class BindingAdapters {

    companion object {
        @JvmStatic
        @BindingAdapter("app:errorText")
        fun setErrorMessage(view: TextInputLayout, errorMessage: String?) {
            view.error = errorMessage
            view.isErrorEnabled = !TextUtils.isEmpty(errorMessage)
            view.requestFocus()
        }

        @JvmStatic
        @InverseBindingAdapter(attribute = "app:selectedValue", event = "app:selectedValueAttrChanged")
        fun captureSelectedValue(pAppCompatSpinner: Spinner): String {
            return pAppCompatSpinner.selectedItem.toString()
        }

        @JvmStatic
        @BindingAdapter("android:src")
        fun setImageResource(imageView: ImageView, resource: Int) {
            imageView.setImageResource(resource)
        }

        @JvmStatic
        @BindingAdapter("categoryItems")
        fun bindList(view: ExpandableListView, response: ObservableField<CategoryResponseDataClass>?) {
            val baseUrl = GlobalSingelton.instance?.configuration?.category_media_url

            if(response?.get() != null){
                val ratioList : ArrayList<Double> = ArrayList()
                for (data in response?.get()?.children_data!!){
                    val imageUrl =  data.image

                    GlideApp.with(view.context.applicationContext)
                            .asBitmap()
                            .load(baseUrl+imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    val ratio =  resource.height.toDouble() / resource.width.toDouble()
                                    ratioList.add(ratio)
                                }
                            })
                }

                val adapter = CategoryThreeLevelAdapter(view.context, response?.get()?.children_data, ratioList)
                view.setAdapter(adapter)


            }


        }


        //for images at home promotion
        @JvmStatic
        @BindingAdapter("bind:imageUrl")
        fun loadImage(imageView: ImageView, imageUrl: String?) {
            imageUrl?.run {
                GlideApp.with(imageView.context)
                        .load(imageUrl)
                        .error(R.drawable.placeholder)// will be displayed if the image cannot be loaded
                        .fallback(R.drawable.placeholder)// will be displayed if the image url is null
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(imageView.width, imageView.height)
                        .into(imageView)
            }
        }

        //for images at home category
        @JvmStatic
        @BindingAdapter("bind:baseWithimageUrl")
        fun loadImageWithBaseUrl(imageView: ImageView, imageUrl: String?) {
            val baseUrl = GlobalSingelton.instance?.configuration?.category_media_url
            imageUrl?.run {
                GlideApp.with(imageView.context)
                        .load(baseUrl+imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(imageView.width, imageView.height)
                        .centerCrop()
                        .into(imageView)
            }
        }


        //for images at home category
        @JvmStatic
        @BindingAdapter("bind:baseWithimageUrlCategory")
        fun loadImageWithBaseUrlCategory(imageView: ImageView, imageUrl: String?) {
            val baseUrl = GlobalSingelton.instance?.configuration?.category_media_url
            if(imageUrl.isNullOrBlank().not()){
                GlideApp.with(imageView.context)
                        .asBitmap()
                        .load(baseUrl+imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .centerCrop()
                        .into(object : SimpleTarget<Bitmap>() {
                            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                val ratio =  resource.height.toDouble() / resource.width.toDouble()
                                Utils.setImageViewHeightWrtDeviceWidth(imageView.context, imageView, ratio)
                                imageView.setImageBitmap(resource)
                            }
                        })
            }

        }


        //for images in product listing product details
        @JvmStatic
        @BindingAdapter("bind:baseUrlWithProductImageUrl")
        fun loadProductImageWithBaseUrl(imageView: ImageView, imageUrl: String?) {
            val baseUrl = GlobalSingelton.instance?.configuration?.product_media_url
            if(imageUrl.isNullOrEmpty().not()){
                GlideApp.with(imageView.context)
                        .asBitmap()
                        .load(baseUrl+imageUrl)
                        .error(R.drawable.placeholder)// will be displayed if the image cannot be loaded
                        .fallback(R.drawable.placeholder)// will be displayed if the image url is null
                        .placeholder(R.drawable.placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(imageView.width, imageView.height)
                        .into(imageView)

            }
        }

        //for images in order result page
        @JvmStatic
        @BindingAdapter("bind:orderResult")
        fun showOrderStatus(imageView: ImageView, status: String?) {

            var imageName = ""
            when(status){
                Constants.SUCCESS -> {
                    imageName = "order_success"

                }
                Constants.CANCEL -> {
                    imageName = "order_cancel"

                }
                Constants.FAILURE -> {
                    imageName = "order_fail"

                }
            }

            val iconId = imageView.context.resources.getIdentifier(imageName, "drawable", imageView.context.packageName)

            GlideApp.with(imageView.context)
                    .asBitmap()
                    .load(iconId)
                    .centerCrop()
                    .into(imageView)


        }
    }


}