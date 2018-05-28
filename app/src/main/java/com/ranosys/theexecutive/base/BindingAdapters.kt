package com.ranosys.theexecutive.base

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.ObservableField
import android.support.design.widget.TextInputLayout
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.Spinner
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.modules.category.CategoryResponseDataClass
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass
import com.ranosys.theexecutive.modules.category.adapters.CategoryThreeLevelAdapter
import com.ranosys.theexecutive.modules.category.adapters.CustomViewPageAdapter
import com.ranosys.theexecutive.utils.GlideApp
import com.ranosys.theexecutive.utils.GlobalSingelton


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
            val adapter = CategoryThreeLevelAdapter(view.context, response?.get()?.children_data)
            view.setAdapter(adapter)
        }

        @JvmStatic
        @BindingAdapter("promotionData")
        fun bindViewPager(view: ViewPager, response: List<PromotionsResponseDataClass>?) {
            val customViewPagerAdapter = CustomViewPageAdapter(view.context, response)
            view.adapter = customViewPagerAdapter
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
                        .error(R.drawable.placeholder)// will be displayed if the image cannot be loaded
                        .fallback(R.drawable.placeholder)// will be displayed if the image url is null
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(imageView.width, imageView.height)
                        .into(imageView)
            }
        }


        //for images in product listing product details
        @JvmStatic
        @BindingAdapter("bind:baseUrlWithProductImageUrl")
        fun loadProductImageWithBaseUrl(imageView: ImageView, imageUrl: String?) {
            val baseUrl = GlobalSingelton.instance?.configuration?.product_media_url
            imageUrl?.run {
                GlideApp.with(imageView.context)
                        .asBitmap()
                        .load(baseUrl+imageUrl)
                        .error(R.drawable.placeholder)// will be displayed if the image cannot be loaded
                        .fallback(R.drawable.placeholder)// will be displayed if the image url is null
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .override(imageView.width, imageView.height)
                        .into(imageView)
            }
        }
    }
}