package com.ranosys.theexecutive.modules.category.adapters

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ranosys.theexecutive.R
import com.ranosys.theexecutive.databinding.PromotionViewBinding
import com.ranosys.theexecutive.modules.category.PromotionsResponseDataClass

/**
 * Created by Mohammad Sunny on 26/3/18.
 */
class CustomViewPageAdapter(context : Context, list : List<PromotionsResponseDataClass>?) : PagerAdapter() {

    interface OnItemClickListener {
        fun onItemClick(item : PromotionsResponseDataClass?)
    }

    var clickListener: OnItemClickListener? = null
    var context : Context? = null
    var layoutInflater : LayoutInflater? = null
    var promotionList : List<PromotionsResponseDataClass>? = null

    fun setItemClickListener(listener: OnItemClickListener){
        clickListener = listener
    }

    init {
        this.context = context
        promotionList = list
        layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater?
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as View
    }

    override fun getCount(): Int {
        promotionList?.run {
            return size
        }
        return 0
    }

    override fun instantiateItem(container: ViewGroup, position: Int): View {
        val listGroupBinding: PromotionViewBinding? = DataBindingUtil.inflate(layoutInflater, R.layout.promotion_view, container, false);
        listGroupBinding?.promotionResponse = promotionList?.get(position)

        listGroupBinding?.imgPromotion?.setOnClickListener {
            clickListener?.onItemClick(promotionList?.get(position))

        }
        container.addView(listGroupBinding?.root)
        return listGroupBinding?.root!!
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}